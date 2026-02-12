//package com.Tapr.Trackpad_Controller.Entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//import org.springframework.cglib.core.Local;
//
//import java.time.LocalDateTime;
//
//@Entity
//@Table(name="actions")
//@NoArgsConstructor
//@AllArgsConstructor
//@Data
//public class Action {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//    private String actionType;
//    private String actionName;
//    private String description;
//
//    @Column(nullable = false, updatable = false)
//    private LocalDateTime createdAt = LocalDateTime.now();
//}
