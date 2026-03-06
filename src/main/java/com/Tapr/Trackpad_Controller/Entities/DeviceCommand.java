package com.Tapr.Trackpad_Controller.Entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "device_commands")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeviceCommand {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonBackReference
    @ManyToOne
    @JoinColumn(name = "gesture_rule_id")
    private GestureRule gestureRule;

    @Column(nullable = false)
    private String sku;

    @Column(nullable = false)
    private String device;

    @Column(nullable = false)
    private String capabilityType;

    @Column(nullable = false)
    private String capabilityInstance;

    @Column(nullable = false)
    private String value = "0";
}