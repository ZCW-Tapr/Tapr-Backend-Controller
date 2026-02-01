package com.Tapr.Trackpad_Controller.Services;

import com.Tapr.Trackpad_Controller.DataTransferObject.ActionDTO;
import com.Tapr.Trackpad_Controller.Entities.Action;
import com.Tapr.Trackpad_Controller.Entities.Device;
import com.Tapr.Trackpad_Controller.Entities.LightAction;
import com.Tapr.Trackpad_Controller.ExceptionHandling.DeviceNotFoundException;
import com.Tapr.Trackpad_Controller.Repositories.ActionRepository;
import com.Tapr.Trackpad_Controller.Repositories.DeviceRepository;
import com.Tapr.Trackpad_Controller.Repositories.LightActionRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

@Service
public class ActionService {

    private final ActionRepository actionRepository;
    private final LightActionRepository lightActionRepository;
    private final DeviceRepository deviceRepository;



    public ActionService(ActionRepository actionRepository, LightActionRepository lightActionRepository, DeviceRepository deviceRepository) {
        this.actionRepository = actionRepository;
        this.lightActionRepository = lightActionRepository;
        this.deviceRepository = deviceRepository;
    }

    @Transactional
    public Action createActionWithLightAction(ActionDTO dto) {


        Action action = new Action();
        action.setActionType(dto.getActionType());
        action.setActionName(dto.getActionName());
        action.setDescription(dto.getDescription());

        Action savedAction = actionRepository.save(action);

        if (dto.getLightAction() != null) {
            LightAction lightAction = new LightAction();

            lightAction.setOperation(dto.getLightAction().getOperation());
            lightAction.setColor(dto.getLightAction().getColor());
            lightAction.setBrightness(dto.getLightAction().getBrightness());
            lightAction.setDuration(dto.getLightAction().getDuration());

            if (dto.getLightAction().getDeviceId() != null) {
                Device device = deviceRepository.findById(dto.getLightAction().getDeviceId())
                        .orElseThrow(() -> new DeviceNotFoundException(dto.getLightAction().getDeviceId()));
                lightAction.setDevice(device);
            }
            lightAction.setAction(savedAction);

            lightActionRepository.save(lightAction);
        }

        return savedAction;
    }
}
