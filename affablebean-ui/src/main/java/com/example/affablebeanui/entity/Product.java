package com.example.affablebeanui.entity;

import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class Product {

    private int id;
    private String name;
    private double price;
    private String description;
    private LocalDate lastUpdate;
    private List<Integer> quantities;
    private int quantity;

    private Category category;
}
