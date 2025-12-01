package com.vizako.anonphobease.v1.controller;

import com.vizako.anonphobease.model.Role;
import com.vizako.anonphobease.v1.dto.UserDTO;
import com.vizako.anonphobease.v1.mapper.UserMapper;
import com.vizako.anonphobease.model.User;
import com.vizako.anonphobease.service.UserService;
import jakarta.validation.Valid;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;


@RestController
@RequestMapping("/v1/users")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public List<UserDTO> getAll() {
        return userService.findAll();
    }

    @GetMapping("/{id}")
    public UserDTO getById(@PathVariable String id) {
        return userService.findById(id).orElse(null);
    }

    @PostMapping
    public UserDTO create(@Valid @RequestBody UserDTO dto) {
        return userService.save(dto);
    }

    @PutMapping("/{id}")
    public UserDTO update(@PathVariable String id, @Valid @RequestBody UserDTO dto) {
        return userService.update(id, dto).orElse(null);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        userService.deleteById(new ObjectId(id));
        return ResponseEntity.noContent().build();
    }
}
