package com.a2v5lv.sockstorage.repository;

import com.a2v5lv.sockstorage.entity.Sock;
import org.springframework.data.jpa.repository.JpaRepository;  // методы работы с БД
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface SockRepository extends JpaRepository<Sock, Long> { // механизм обращения к БД и взаимодействие
    // работаем с объектами Sock, с типом как у id

    Sock findByColorAndCottonPart(String color, Integer cottonPart); // метод проверки наличия в БД

    @Query(value =
            "select sum(socks.quantity) from socks " +
            "where socks.color = :color and socks.cotton_part > :cottonPart",
            nativeQuery = true)
        // запрос в базу SQL
    Integer sumByColorAndCottonPartMoreThan(String color, Integer cottonPart);

    @Query(value =
            "select sum(socks.quantity) from socks " +
            "where socks.color = :color and socks.cotton_part < :cottonPart",
            nativeQuery = true)
        // запрос в базу SQL
    Integer sumByColorAndCottonPartLessThan(String color, Integer cottonPart);

    @Query(value =
            "select sum(socks.quantity) from socks " +
            "where socks.color = :color and socks.cotton_part = :cottonPart",
            nativeQuery = true)
        // запрос в базу SQL
    Integer sumByColorAndCottonPartEqual(String color, Integer cottonPart);
}


