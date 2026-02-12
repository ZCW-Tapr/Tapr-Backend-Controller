package com.Tapr.Trackpad_Controller.Controllers;

import com.Tapr.Trackpad_Controller.Entities.GestureRule;
import com.Tapr.Trackpad_Controller.ExceptionHandling.GestureRuleNotFoundException;
import com.Tapr.Trackpad_Controller.Repositories.GestureRuleRepository;

import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/gestures")
public class GestureController {

    private final GestureRuleRepository repository;

    public GestureController(GestureRuleRepository repository) {
        this.repository = repository;
    }

    @GetMapping
    List<GestureRule> allGestures() {
        return repository.findAll();
    }

    @PostMapping
    GestureRule newGesture(@RequestBody GestureRule newGesture) {
        return repository.save(newGesture);
    }

    @GetMapping("/{id}")
    GestureRule findGestureById(@PathVariable Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new GestureRuleNotFoundException(id));
    }

    @PutMapping("/{id}")
    GestureRule replaceGesture(@RequestBody GestureRule newGestureRule, @PathVariable Long id) {

        return repository.findById(id)
                .map(gestureRule -> {
                    gestureRule.setGestureName(newGestureRule.getGestureName());
                    gestureRule.setGestureType(newGestureRule.getGestureType());
                    gestureRule.setEnabled(newGestureRule.getEnabled());
                    gestureRule.setDeviceCommands(newGestureRule.getDeviceCommands());
                    return repository.save(gestureRule);
                })
                .orElseGet(() -> {
                    return repository.save(newGestureRule);
                });
    }

    @DeleteMapping("/{id}")
    public void deleteGestureById(@PathVariable Long id) {
        repository.deleteById(id);
    }
}
