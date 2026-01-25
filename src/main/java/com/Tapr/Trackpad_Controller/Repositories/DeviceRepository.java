package com.Tapr.Trackpad_Controller.Repositories;

import com.Tapr.Trackpad_Controller.Entities.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
