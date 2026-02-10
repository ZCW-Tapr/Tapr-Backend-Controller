package com.Tapr.Trackpad_Controller.Repositories;

import com.Tapr.Trackpad_Controller.Entities.DeviceCommand;
import com.Tapr.Trackpad_Controller.Entities.GestureRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceCommandRepository extends JpaRepository<DeviceCommand, Long> {

    List<DeviceCommand> findByGestureRule(GestureRule gestureRule);

    List<DeviceCommand> findByGestureRuleId(Long gestureRuleId);
}