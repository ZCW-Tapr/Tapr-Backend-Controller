package com.Tapr.Trackpad_Controller.Controllers;

import com.Tapr.Trackpad_Controller.DataTransferObject.ControlOfDevices.ActionDTO;
import com.Tapr.Trackpad_Controller.Entities.Action;
import com.Tapr.Trackpad_Controller.ExceptionHandling.ActionNotFoundException;
import com.Tapr.Trackpad_Controller.Repositories.ActionRepository;
import com.Tapr.Trackpad_Controller.Services.ActionService;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/actions")
public class ActionController {

    private final ActionRepository repository;
    private final ActionService service;

    public ActionController(ActionRepository repository, ActionService service) {
        this.repository = repository;
        this.service = service;
    }

    @GetMapping
    List<Action> allActions() {
        return repository.findAll();
    }

    @PostMapping
    Action newAction(@RequestBody ActionDTO newActionDTO) {
        return service.createActionWithLightAction(newActionDTO);
    }

    @GetMapping("/{id}")
    Action findActionById(@PathVariable Long id) {

        return repository.findById(id)
                .orElseThrow(() -> new ActionNotFoundException(id));
    }

    @PutMapping("/{id}")
    Action changeAction(@RequestBody Action newAction, @PathVariable Long id) {
        return repository.findById(id)
                .map(action -> {
                    action.setActionName(newAction.getActionName());
                    action.setActionType(newAction.getActionType());
                    action.setDescription(newAction.getDescription());
                    return repository.save(action);
                })
                .orElseGet(() -> {
                    return repository.save(newAction);
                });
    }

    @DeleteMapping("/{id}")
    void deleteAction(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
