## Без Docker
Сборка без Docker
git clone https://github.com/Almir-Shammasov/Wallets.git

После клонирования подключаем субд Postgre к проекту

Настройки в properties

spring.datasource.url=jdbc:postgresql://localhost:your_port/your_db
spring.datasource.username=your_username
spring.datasource.password=your_password
spring.jpa.hibernate.ddl-auto=none
spring.liquibase.enabled=true
spring.liquibase.change-log=classpath:db/changelog/db.changelog-master.yaml

mvn clean install
mvn spring-boot:run

## С Docker
Сборка с помощью Docker
git clone https://github.com/Almir-Shammasov/Wallets.git

mvn clean package

docker-compose up --build

## Пользователь
В миграции liquibase сразу создается пользователь с правами admin
с его помощью можно будет создавать других ADMIN пользователей
"email": "root@mail.com"
"password": "1111"
Эндпоинт http://localhost:8081/api/auth/login выдает JWT для зарегистрированных пользователей

Для проверки эндпоинтов прикладываю коллекцию для postman 






