package com.Tapr.Trackpad_Controller.Repositories;

import com.Tapr.Trackpad_Controller.Entities.LightAction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LightActionRepository extends JpaRepository<LightAction, Long>{
}
