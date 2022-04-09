PROJECT DESCRIPTION AND IMPLEMENTATION



ЗАДАНИЕ:


Реализовать приложение для автоматизации учёта носков на складе магазина.
Кладовщик должен иметь возможность:

- учесть приход и отпуск носков;
- узнать общее количество носков определенного цвета и состава в данный момент времени.

Внешний интерфейс приложения представлен в виде HTTP API (или REST).

Список URL HTTP-методов:

1.  POST /api/socks/income - (регистрирует приход носков на склад).

Параметры запроса передаются в теле запроса в виде JSON-объекта со следующими атрибутами:

- color (цвет носков, строка (например, black, red, yellow));
- cottonPart (процентное содержание хлопка в составе носков, целое число от 0 до 100 (например, 30, 18, 42));
- quantity (количество пар носков, целое число больше 0).

Результаты:

HTTP 200 — удалось добавить приход;
HTTP 400 — параметры запроса отсутствуют или имеют некорректный формат;
HTTP 500 — произошла ошибка, не зависящая от вызывающей стороны (например, база данных недоступна).

2.  POST /api/socks/outcome - (регистрирует отпуск носков со склада).

Результаты:

Параметры и результаты аналогичные.
Общее количество носков указанного цвета и состава не увеличивается, а уменьшается.

3.  GET /api/socks - (возвращает общее количество носков на складе, соответствующих переданным в параметрах).

Параметры запроса передаются в URL:

- color (цвет носков, строка);
- operation (оператор сравнения значения количества хлопка в составе носков, одно значение из:
	- moreThan;
	- lessThan;
	- equal.);
- cottonPart (значение процента хлопка в составе носков из сравнения).

Результаты:

- HTTP 200 (запрос выполнен, результат в теле ответа в виде строкового представления целого числа);
- HTTP 400 (параметры запроса отсутствуют или имеют некорректный формат);
- HTTP 500 (произошла ошибка, не зависящая от вызывающей стороны (например, база данных недоступна)).

Примеры запросов:

/api/socks?color=red&operation=moreThan&cottonPart=90 
(должен вернуть общее количество красных носков с долей хлопка более 90%);
/api/socks?color=black&operation=lessThan?cottonPart=10 
(должен вернуть общее количество черных носков с долей хлопка менее 10%).

Для хранения данных системы можно использовать любую реляционную базу данных.
Схему БД желательно хранить в репозитории в любом удобном виде.
Технологии для построения сервиса могут быть выбраны произвольно.


=========================================================================================================
=========================================================================================================
=========================================================================================================


ОПИСАНИЕ РЕШЕНИЯ:



Запросы в Postman:

http://localhost:8080/api/socks/income
{
    "color" : "RED",
    "cottonPart" : 70,
    "quantity" : 30
}

http://localhost:8080/api/socks/outcome
{
    "color" : "RED",
    "cottonPart" : 70,
    "quantity" : 10
}



Создание и работа с БД на примере MySQL в Docker:

docker run -d --name sockstorage -p 3306:3306 -e MYSQL_ROOT_PASSWORD=root mysql
docker exec --interactive --tty sockstorage mysql -uroot -p
CREATE DATABASE sockstorage;
SHOW DATABASES;
use sockstorage;
show tables;
select * from socks;
drop table socks; (очистка таблицы)
выбока из таблицы:
select sum(s.quantity) from socks s where s.color = "RED" and s.cotton_part >= 70;
или без псевдонима "s":
select sum(socks.quantity) from socks where socks.color = "RED" and socks.cotton_part >= 70;

//******//

mysql> select * from socks;
+----+-------+-------------+----------+
| id | color | cotton_part | quantity |
+----+-------+-------------+----------+
|  1 | BLUE  |          65 |        5 |
|  2 | RED   |          80 |       25 |
|  3 | GREEN |          95 |       10 |
|  4 | RED   |          70 |       30 |
+----+-------+-------------+----------+

//******//


Зависимости (pom.xml):

<dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-jpa</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>

        <dependency>
            <groupId>org.projectlombok</groupId>
            <artifactId>lombok</artifactId>
            <optional>true</optional>
        </dependency>

        <dependency>
            <groupId>mysql</groupId>
            <artifactId>mysql-connector-java</artifactId>
            <scope>runtime</scope>
        </dependency>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-test</artifactId>
            <scope>test</scope>
        </dependency>
    </dependencies>


Настройка Spring (application.properties):

spring.datasource.url=jdbc:mysql://localhost:3306/sockstorage
spring.datasource.username=root
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true


Основные этапы создания проекта:

1. Настройка зависимостей и Spring (Spring Web, Spring Data JPA, MySQL Driver);
2. Разворачиваем в Docker базу MySQL и создаем в ней таблицу sockstorage;
3. Указываем параметры в application.properties;
4. Указываем зависимость Lombok;
5. Запускаем проект - проверяем работу, например на браузере http://localhost:8080/
6. Создаем пакет "entity" с классом "Sock" для сущностей и связи с БД;
7. Создаем пакет "controller" с классом "SockController" для логики;
8. Создаем пакет "repository" с классом "SockRepository" для работы с БД;
9. Добавляем логику в "SockController" для работы с "SockRepository";
10. Пишем логику (addSocksToSockStorage) (пока void) для п.1 (POST /api/socks/income) в "SockController";
11. Проверяем работу логики в Postman;
12. Добавляем логику (findByColorAndCottonPart) проверки наличия в "SockRepository" 
(для правильного суммирования одинаковых носков в БД);
13. Добавляем логику findByColorAndCottonPart в метод addSocksToSockStorage;
14. Пишем логику (subSocksFromSockStorage) (пока void) для п.2 (POST /api/socks/outcome) в "SockController";
15. Добавляем логику findByColorAndCottonPart в метод subSocksFromSockStorage;
16. Добавляем проверку на отрицательный остаток в метод subSocksFromSockStorage;
17. Создаем метод (getSockByColorAndCottonPart) (пока Integer) для п.3 (GET /api/socks) в "SockController";
18. Добавляем логику запросов к БД (sumByColorAndCottonPartMoreThan) интерфейс в "SockRepository";
19. Добавляем логику запросов к БД (sumByColorAndCottonPartLessThan) интерфейс в "SockRepository";
20. Добавляем логику запросов к БД (sumByColorAndCottonPartEqual) интерфейс в "SockRepository";
21. В методе getSockByColorAndCottonPart добавляем switch для обработки запросов к БД;
22. Создаем пакет "dto" с классом "SockResponseResult" для отправки ответов на запросы;
23. Пишем логику в классе SockResponseResult (конструктор с параметрами и пустой конструктор);
24. Меняем возврат из метода getSockByColorAndCottonPart (Integer) на ResponseEntity<SockResponseResult>;
25. Меняем возврат из метода subSocksFromSockStorage (void) ResponseEntity<?> и прописывем статусы;
26. Меняем возврат из метода addSocksToSockStorage(void) ResponseEntity<?> и прописывем статусы;
