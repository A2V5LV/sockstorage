package com.a2v5lv.sockstorage.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Getter                 // автогерерация геттеров и сеттеров для корректной обрработки входных POST запросов
@Setter
@Entity
@Table(name = "socks")  // имя таблицы в БД
public class Sock {     // Java <-> MySQL (запись объектов в БД и возврат данных из БД в объекты)

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // автоматическое генерирование id
    private Long id;

    private String color;

    private Integer cottonPart;

    private Integer quantity;

}
