package com.Tapr.Trackpad_Controller.Controllers;

import com.Tapr.Trackpad_Controller.ExceptionHandling.DeviceNotFoundException;
import com.Tapr.Trackpad_Controller.Entities.Device;
import com.Tapr.Trackpad_Controller.Repositories.DeviceRepository;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/devices")
class DeviceController {

    private final DeviceRepository repository;

    public DeviceController(DeviceRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    public List<Device> allDevices() {
        return repository.findAll();
    }

    @PostMapping
    public Device addNewDevice(@RequestBody Device newDevice) {
        return repository.save(newDevice);
    }

    @GetMapping("/{id}")
    Device findDevice(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new DeviceNotFoundException(id));
    }

    @PutMapping("/{id}")
    Device replaceDevice(@RequestBody Device newDevice, @PathVariable Long id) {

        return repository.findById(id)
                .map(device -> {
                    device.setDeviceName(newDevice.getDeviceName());
                    device.setDeviceType(newDevice.getDeviceType());
                    device.setIpAddress(newDevice.getIpAddress());
                    device.setMacAddress(newDevice.getMacAddress());
                    return repository.save(device);
                })
                .orElseGet(() -> {
                    return repository.save(newDevice);
                });
    }

    @DeleteMapping("/{id}")
    void deleteDevice(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
