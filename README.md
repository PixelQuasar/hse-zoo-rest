```
 ___________  ___________          _   
|___  /  _  ||  _  | ___ \        | |  
   / /| | | || | | | |_/ /___  ___| |_ 
  / / | | | || | | |    // _ \/ __| __|
./ /__\ \_/ /\ \_/ / |\ \  __/\__ \ |_ 
\_____/\___/  \___/\_| \_\___||___/\__|
```

#### Проект полностью реализует все требуемые Use Cases:
a. Добавление/удаление животного

    Реализация: класс AnimalController (presentation/controller/AnimalController.java)
    Методы:
        createAnimal() - создание животного
        deleteAnimal() - удаление животного
    Поддержка: доменная модель Animal (domain/model/animal/Animal.java)

b. Добавление/удаление вольера

    Реализация: класс EnclosureController (presentation/controller/EnclosureController.java)
    Методы:
        createEnclosure() - создание вольера
        deleteEnclosure() - удаление вольера
    Поддержка: доменная модель Enclosure (domain/model/enclosure/Enclosure.java)

c. Перемещение животного между вольерами

    Реализация: класс AnimalTransferService (application/service/AnimalTransferService.java)
    Методы:
        transferAnimal() - перемещение животного
    API Endpoint: AnimalController.transferAnimal() (POST /api/animals/transfer)
    Событие: AnimalMovedEvent (domain/event/AnimalMovedEvent.java)

d. Просмотр расписания кормления

    Реализация: класс FeedingScheduleController (presentation/controller/FeedingScheduleController.java)
    Методы:
        getAllFeedingSchedules() - получение всех расписаний
        getFeedingSchedulesForAnimal() - расписание для конкретного животного
        getPendingFeedings() - получение ожидающих кормлений

e. Добавление нового кормления в расписание

    Реализация: класс FeedingOrganizationService (application/service/FeedingOrganizationService.java)
    Методы:
        createFeedingSchedule() - создание расписания кормления
    API Endpoint: FeedingScheduleController.createFeedingSchedule() (POST /api/feeding-schedules)

f. Просмотр статистики зоопарка

    Реализация: класс ZooStatisticsService (application/service/ZooStatisticsService.java)
    API: класс StatisticsController (presentation/controller/StatisticsController.java)
    Методы:
        getZooStatistics() - полная статистика
        getStatisticsSummary() - краткая сводка
        getHealthAlerts() - оповещения о здоровье
        getCapacityStatistics() - статистика заполненности

2. Применение принципов `Domain-Driven Design`
   Использование `Value Objects` вместо примитивов

   `AnimalId, EnclosureId, FeedingId` - идентификаторы сущностей
   `Species` - моделирует вид животного с дополнительными свойствами
   `FavoriteFood` - инкапсулирует информацию о любимой пище
   `Capacity` - моделирует вместимость вольера с бизнес-правилами
   `FeedingTime` - инкапсулирует время кормления с методами для проверки

Пример: FeedingTime.java


```java
public class FeedingTime {
private final LocalTime time;

    // Приватный конструктор для контроля создания
    private FeedingTime(LocalTime time) {
        this.time = time;
    }
    
    // Фабричный метод
    public static FeedingTime of(LocalTime time) {
        return new FeedingTime(time);
    }
    
    public boolean isFeedingTime(LocalTime currentTime) {
        return currentTime.equals(time) || currentTime.isAfter(time);
    }
}
```

Богатая доменная модель с инкапсуляцией бизнес-правил

`Animal` - инкапсулирует правила кормления, лечения и перемещения животных
`Enclosure` - содержит логику добавления/удаления животных, проверку совместимости
`FeedingSchedule` - контролирует правила кормления и отслеживает историю

Пример: Проверка совместимости в Enclosure.java

```java

public boolean addAnimal(Animal animal) {
if (!hasAvailableSpace()) {
throw new IllegalStateException("Enclosure is at full capacity");
}

    if (!canHouseAnimal(animal)) {
        throw new IllegalArgumentException(
            "This enclosure type is not suitable for " + animal.getSpecies().getValue()
        );
    }
    
    return animals.add(animal);
}
```

Агрегаты и корни агрегатов

`Animal` - корень агрегата животного
`Enclosure` - корень агрегата вольера, управляет коллекцией животных
`FeedingSchedule` - корень агрегата расписания кормления

Доменные события

`AnimalMovedEvent` - событие при перемещении животного между вольерами
`FeedingTimeEvent` - событие при наступлении времени кормления
Инфраструктура событий: `EventPublisher` и `SpringEventPublisher`

Репозитории для работы с агрегатами

`AnimalRepository` - интерфейс для работы с агрегатом Animal
`EnclosureRepository` - интерфейс для работы с агрегатом Enclosure
`FeedingScheduleRepository` - интерфейс для работы с агрегатом FeedingSchedule

3. Применение принципов Clean Architecture
   Структура проекта

Проект структурирован согласно принципам Clean Architecture:

Domain Layer - ядро приложения, не зависит ни от каких внешних компонентов:
- domain/model/ - доменные модели (Animal, Enclosure, FeedingSchedule)
- domain/event/ - доменные события
- domain/repository/ - интерфейсы репозиториев

Application Layer - содержит бизнес-логику, зависит только от домена:
- application/service/ - сервисы (AnimalTransferService, FeedingOrganizationService)
- application/dto/ - объекты передачи данных

Infrastructure Layer - реализация интерфейсов из домена:
- infrastructure/persistence/inmemory/ - in-memory реализации репозиториев
- infrastructure/event/ - реализация инфраструктуры событий

Presentation Layer - REST API для взаимодействия с системой:
- presentation/controller/ - контроллеры REST API
- presentation/request/ - модели запросов
- presentation/response/ - модели ответов
- presentation/advice/ - обработка исключений

Соблюдение принципов зависимостей

Зависимость только внутрь:
- Domain Layer не зависит ни от чего
- Application Layer зависит только от Domain Layer
- Infrastructure Layer зависит от Domain Layer (реализует интерфейсы)
- Presentation Layer зависит от Application Layer и Domain Layer

Зависимости через интерфейсы:
Репозитории определены в Domain Layer как интерфейсы:

```java

        // domain/repository/AnimalRepository.java
        public interface AnimalRepository {
            Animal save(Animal animal);
            Optional<Animal> findById(AnimalId id);
            // ...
        }
```
События публикуются через интерфейс EventPublisher

Изоляция бизнес-логики:
Бизнес-правила для животных содержатся в Animal (перемещение, лечение)
Бизнес-правила для вольеров содержатся в Enclosure (совместимость, вместимость)
Сложные бизнес-операции инкапсулированы в сервисах (например, AnimalTransferService)
