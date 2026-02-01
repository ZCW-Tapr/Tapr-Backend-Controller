package com.Tapr.Trackpad_Controller.Repositories;

import com.Tapr.Trackpad_Controller.Entities.LightAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LightActionRepository extends JpaRepository<LightAction, Long> {

    // Find LightAction by its parent Action
    Optional<LightAction> findByActionId(Long actionId);

    // Find LightActions by operation type
    List<LightAction> findByOperation(String operation);
}
