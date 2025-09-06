package com.datatrans.ecommerce.userms.permissions;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;
    

    // Create a new user
    @PostMapping
    public Permission post(@RequestBody Map<String, Object> request) {
    	System.out.println("arrive");
        return permissionService.post(request);
    }

    @GetMapping
    public List<Permission> getAll() {
        return permissionService.getAll();
    }

    @GetMapping("/{id}")
    public Optional<Permission> getById(@PathVariable Long id) {
        return permissionService.getById(id);
    }

    @PatchMapping
    public Permission patch(@RequestBody Map<String, Object> request) {
        return permissionService.patch(request);
    }

}

