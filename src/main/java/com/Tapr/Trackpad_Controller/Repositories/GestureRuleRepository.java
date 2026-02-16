package com.Tapr.Trackpad_Controller.Repositories;

import com.Tapr.Trackpad_Controller.Entities.GestureRule;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface GestureRuleRepository extends JpaRepository<GestureRule, Long>{

    // Find gesture by name
    Optional<GestureRule> findByGestureName(String gestureName);

    // Find all enabled gestures
    List<GestureRule> findByEnabled(Boolean enabled);

    // Find gestures by finger count
    @Query("SELECT g FROM GestureRule g LEFT JOIN FETCH g.deviceCommands WHERE g.fingerCount = :fingerCount AND g.gestureType = :gestureType")
    Optional<GestureRule> findByFingerCountAndGestureType(@Param("fingerCount") Integer fingerCount, @Param("gestureType") String gestureType);
}
