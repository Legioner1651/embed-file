# 📋 Документация проекта embed-file

## 🎯 Обзор проекта

**embed-file** - это Spring Boot сервис для генерации HTML файлов на основе данных из различных источников (БД Oracle, PostgreSQL, REST API, файловая система).

### Основная информация
- **Группа**: ru.ruslan
- **Артефакт**: embed-file
- **Версия Spring Boot**: 4.0.7
- **Java версия**: 17
- **Порт**: 8080 (по умолчанию)

---

## 🏗️ Архитектура проекта
embed-file/
├── src/
│ ├── main/
│ │ ├── java/ru/ruslan/
│ │ │ ├── EmbedFileApplication.java # Главный класс приложения
│ │ │ ├── config/
│ │ │ │ ├── DatabaseConfig.java # Конфигурация БД
│ │ │ │ └── RestTemplateConfig.java # Конфигурация REST клиента
│ │ │ ├── controller/
│ │ │ │ └── VkEpcController.java # REST контроллер
│ │ │ ├── model/
│ │ │ │ ├── CreateFileVKRequest.java # Модель запроса
│ │ │ │ └── CreateFileVKResponse.java # Модель ответа
│ │ │ └── service/
│ │ │ ├── VkEpcService.java # Основной сервис
│ │ │ ├── VkEpcApp.java # Координатор компонентов
│ │ │ └── impl/
│ │ │ ├── RestClientService.java # REST клиент
│ │ │ ├── OracleDbService.java # Oracle сервис
│ │ │ ├── PostgreSqlDbService.java # PostgreSQL сервис
│ │ │ ├── JavaScriptService.java # JavaScript обработка
│ │ │ ├── JsonProcessingService.java # JSON обработка
│ │ │ └── HtmlProcessingService.java # HTML генерация
│ │ └── resources/
│ │ ├── application.yml # Конфигурация приложения
│ │ └── logback-spring.xml # Конфигурация логирования
│ └── test/
│ └── java/ru/ruslan/
│ └── service/
│ └── VkEpcServiceTest.java # Тесты
└── pom.xml # Maven зависимости


---

## 🔄 Поток обработки запроса


graph TD
    A[POST /api/createFileVK] --> B[VkEpcController]
    B --> C[VkEpcService.processRequest]
    C --> D[OracleDbService]
    C --> E[PostgreSqlDbService]
    C --> F[RestClientService]
    C --> G[JavaScriptService]
    C --> H[JsonProcessingService]
    C --> I[HtmlProcessingService]
    D --> J[HTML Document]
    E --> J
    F --> J
    G --> J
    H --> J
    I --> J
    J --> K[CreateFileVKResponse]


---

## 📡 API Endpoints

Создание VK EPC файла
Endpoint: POST /api/createFileVK

Content-Type: application/json

Request Body:
{
  "pathCreateFileVK": "/path/to/files",
  "orderId": "ORDER-12345",
  "exerciseId": "EX-67890",
  "KNS": "KNS-001",
  "billingAccount": "BA-54321",
  "time": "2024-01-15T10:30:00",
  "epcParams": "{\"param1\": \"value1\", \"param2\": \"value2\"}"
}

Параметры запроса:
Параметр	Тип	Обязательный	Описание
pathCreateFileVK	String	Да	Путь к файлам для чтения
orderId	String	Да	Идентификатор заказа
exerciseId	String	Да	Идентификатор упражнения
KNS	String	Да	КНС номер
billingAccount	String	Да	Лицевой счет
time	String	Да	Временная метка
epcParams	String	Да	EPC параметры в JSON формате
Response (Успех):

{
  "status": "SUCCESS",
  "message": "File created successfully",
  "htmlContent": "<!DOCTYPE html>...",
  "fileName": "vk_epc_ORDER-12345.html"
}

Response (Ошибка):

{
  "status": "ERROR",
  "message": "Failed to process request: ...",
  "htmlContent": null,
  "fileName": null
}

---

## 🗄️ Базы данных

Oracle

Назначение: Хранение данных об упражнениях
URL: jdbc:oracle:thin:@//localhost:1521/ORCLPDB1
Драйвер: oracle.jdbc.OracleDriver

Основные запросы:

SELECT * FROM exercises WHERE order_id = ?

PostgreSQL

Назначение: Хранение данных о биллинговых счетах
URL: jdbc:postgresql://localhost:5432/vk_epc_db
Драйвер: org.postgresql.Driver

Основные запросы:

SELECT * FROM billing_accounts WHERE account_number = ?

---

## 🛠️ Технологический стек

Технология	Версия	Назначение
Spring Boot	4.0.7	Основной фреймворк
Java	17	Язык программирования
Oracle JDBC	23.3.0	Подключение к Oracle
PostgreSQL JDBC	42.7.1	Подключение к PostgreSQL
Mozilla Rhino	1.7.14	JavaScript движок
Jackson	2.17.x	JSON обработка
Jsoup	1.17.2	HTML парсинг и генерация
Logback	1.5.x	Логирование
Maven	3.9.x	Сборка проекта

---

## 📊 Структура HTML документа

Генерируемый HTML файл содержит следующие секции:

Request Information

Order ID

Exercise ID

KNS

Billing Account

Time

Oracle Database Data

Данные из таблицы exercises

PostgreSQL Database Data

Данные из таблицы billing_accounts

JSON Data

Обработанные данные из REST запросов

Processed EPC Parameters

Результаты JavaScript обработки

File Contents

Содержимое файлов из указанной директории

---

## 🔧 Конфигурация
Переменные окружения

# Oracle
export ORACLE_USERNAME=your_oracle_user
export ORACLE_PASSWORD=your_oracle_password

# PostgreSQL
export POSTGRES_USERNAME=your_postgres_user
export POSTGRES_PASSWORD=your_postgres_password

Настройки пула соединений

HikariCP настройки:
  maximum-pool-size: 10
  minimum-idle: 5
  connection-timeout: 30000ms
  idle-timeout: 600000ms
  max-lifetime: 1800000ms

---

## 🚀  Запуск приложения

Локальный запуск

# Клонирование проекта
git clone <repository-url>

# Сборка проекта
mvn clean install

# Запуск приложения
mvn spring-boot:run

Запуск с профилем

# Production профиль
java -jar target/embed-file-1.0.0-SNAPSHOT.jar --spring.profiles.active=production

Docker (опционально)

FROM openjdk:17-slim
COPY target/embed-file-1.0.0-SNAPSHOT.jar app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]

---

## 📝 Логирование
Уровни логирования
INFO: Общая информация о работе приложения

DEBUG: Детальная информация для отладки

ERROR: Ошибки и исключения

Файлы логов

logs/
├── embed-file.log                    # Текущий лог
└── archived/
    └── embed-file-YYYY-MM-DD.N.log   # Архивированные логи

Ротация логов
Максимальный размер файла: 10MB

Хранение: 30 дней

Периодичность: Ежедневно

---

## 🧪 Тестирование
Запуск тестов

# Все тесты
mvn test

# Конкретный тест
mvn test -Dtest=VkEpcServiceTest


Health Check

curl http://localhost:8080/actuator/health