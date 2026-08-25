package com.example.travel.entity;

import lombok.Data;
import java.util.Date;

@Data
public class LocationItem {
    private Integer id;
    private String name;
    private Integer category; // 1: 景点, 2: 餐厅
    private Double latitude;
    private Double longitude;
    private String address;
    private String imageUrl;
    private String description;
    private Date createdAt;
    private String city;
}
