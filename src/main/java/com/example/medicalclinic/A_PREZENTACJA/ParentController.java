package com.example.medicalclinic.A_PREZENTACJA;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/parents")
@RequiredArgsConstructor
public class ParentController {
    private final ParentRepository parentRepository;

    @GetMapping
    public List<Parent> getAllParents() {
        return parentRepository.findAll();
    }

    @PostMapping
    public Parent addParent(@RequestBody Parent parent) {
        parent.getChildren().forEach(child -> child.setParent(parent));
        return parentRepository.save(parent);
    }
}
