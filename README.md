# Проект
## Предисловие
Мне достаточно 4+ на самом деле, проект был сделан со скрипом 

А, да, кстати, чекни дз у меня, говорили что можно дозаслать будет и я все же запушил его 

## Немного о проекте


*   **Заказы (Order Service):** Принимает заказы.
*   **Платежи (Payments Service):** Оперирует средствами с аккаунтои 

Все это дело общается через Kafka.

## Запуск



1.
    ```bash
    docker-compose up --build
    ```

## Как посмотреть

Два варианта:

*   **Swagger UI :**
    *   Заказы: [http://localhost:8082/swagger-ui.html](http://localhost:8082/swagger-ui.html)
    *   Платежи: [http://localhost:8081/swagger-ui.html](http://localhost:8081/swagger-ui.html)
    

*   **Postman Collection :


## Структура

*   Разделение на микросервисы .
*   Kafka для обмена сообщениями .
*   Transactional Outbox в Orders .
*   Transactional Inbox и Outbox в Payments .
*   "Exactly once" семантика для корректной обработки.
