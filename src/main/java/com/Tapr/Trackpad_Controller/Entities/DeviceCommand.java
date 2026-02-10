package com.Tapr.Trackpad_Controller.Entities;

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

    @ManyToOne
    @JoinColumn(name = "gesture_rule_id")
    private GestureRule gestureRule;

    private String sku;
    private String device;
    private String capabilityType;
    private String capabilityInstance;
    private String value;
}