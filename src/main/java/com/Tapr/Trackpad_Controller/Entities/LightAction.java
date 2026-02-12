//package com.Tapr.Trackpad_Controller.Entities;
//
//import jakarta.persistence.*;
//import lombok.AllArgsConstructor;
//import lombok.Data;
//import lombok.NoArgsConstructor;
//
//@Entity
//@Table(name = "light_actions")
//@Data
//@NoArgsConstructor
//@AllArgsConstructor
//public class LightAction {
//
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @ManyToOne
//    @JoinColumn(name = "action_id")
//    private Action action;
//
//    private String operation;
//    private String color;
//    private Integer brightness;
//    private Integer duration;
//}
