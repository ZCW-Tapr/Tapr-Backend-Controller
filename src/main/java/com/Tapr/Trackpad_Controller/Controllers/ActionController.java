package com.Tapr.Trackpad_Controller.Controllers;

import com.Tapr.Trackpad_Controller.Entities.Action;
import com.Tapr.Trackpad_Controller.ExceptionHandling.ActionNotFoundException;
import com.Tapr.Trackpad_Controller.Repositories.ActionRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@RestController
@RequestMapping("/action")
public class ActionController {

    private final ActionRepository repository;

    public ActionController(ActionRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<Action> allActions() {
        return repository.findAll();
    }

    @PostMapping
    Action newAction(@RequestBody Action newAction) {
        return repository.save(newAction);
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
