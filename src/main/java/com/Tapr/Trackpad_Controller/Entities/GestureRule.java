package com.Tapr.Trackpad_Controller.Entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

/**
 * This is no longer attached to a singular action. This gives the user the
 * flexibility to control multiple devices with one gesture.
 */
@Entity
@Table(name = "gesture_rules")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GestureRule {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String gestureName;

    @Column(nullable = false)
    private String gestureType;

    private Boolean enabled = true;

    @OneToMany(mappedBy = "gestureRule", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DeviceCommand> deviceCommands;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}