package com.datatrans.ecommerce.userms.permissions;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.operationHelper.errorHandler.InvalidParameters;
import com.operationHelper.utils.HelperFunctions;

@Service
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final HelperFunctions helperFunctions;
    private final ObjectMapper objectMapper;

    public PermissionService(PermissionRepository permissionRepository,
                             HelperFunctions helperFunctions,
                             ObjectMapper objectMapper) {
        this.permissionRepository = permissionRepository;
        this.helperFunctions = helperFunctions;
        this.objectMapper = objectMapper;
    }

    /** Create new permission */
    @Transactional
    public Permission post(Map<String, Object> requestMap) {
        var defs = new Permission().fields();
        helperFunctions.validateFields(requestMap, defs);

        Permission p = objectMapper.convertValue(requestMap, Permission.class);
        p.setStatusId(1); // active

        // DB uniqueness among active rows
        if (permissionRepository.existsByCodeAndStatusId(p.getCode(), 1)) {
            throw new InvalidParameters("Permission with code [" + p.getCode() + "] already exists.");
        }

        return permissionRepository.save(p);
    }

    /** Edit / Delete (soft) */
    @Transactional
    public Permission patch(Map<String, Object> requestMap) {
        String op = String.valueOf(requestMap.get("operation")).trim();
        if (op.isBlank()) throw new InvalidParameters("operation is mandatory");

        Object idRaw = requestMap.get("id");
        if (idRaw == null || idRaw.toString().isBlank()) throw new InvalidParameters("id is mandatory");
        Long id = Long.parseLong(idRaw.toString());

        Permission p = permissionRepository.findByIdAndStatusId(id, 1)
            .orElseThrow(() -> new InvalidParameters("Permission with id [" + id + "] not found"));

        switch (op.toLowerCase()) {
            case "delete": {
                p.setStatusId(0); // soft delete
                return permissionRepository.save(p);
            }
            case "edit": {
                var allDefs = new Permission().fields();
                var updatable = allDefs.stream()
                    .map(m -> String.valueOf(m.get("fieldName")))
                    .filter(n -> n != null && !n.isBlank())
                    .filter(n -> !n.equalsIgnoreCase("id"))
                    .collect(java.util.stream.Collectors.toSet());

                Map<String,Object> updates = new java.util.HashMap<>();
                for (var e : requestMap.entrySet()) {
                    String k = e.getKey();
                    if (k == null) continue;
                    if (k.equalsIgnoreCase("id") || k.equalsIgnoreCase("operation")) continue;
                    if (!updatable.contains(k)) continue;
                    updates.put(k, e.getValue());
                }
                if (updates.isEmpty())
                    throw new InvalidParameters("No updatable fields in request.");

              

                // apply
                BeanWrapper bw = new BeanWrapperImpl(p);
                for (var e : updates.entrySet()) {
                    String name = e.getKey();
                    Object raw = e.getValue();
                    Class<?> targetType = bw.getPropertyType(name);
                    Object coerced = (raw == null ? null : objectMapper.convertValue(raw, targetType));
                    bw.setPropertyValue(name, coerced);
                }

                // DB uniqueness if 'code' changed/present
                if (updates.containsKey("code")
                        && permissionRepository.existsByCodeAndIdNotAndStatusId(p.getCode(), p.getId(), 1)) {
                    throw new InvalidParameters("Permission already exists with the same code.");
                }

                return permissionRepository.save(p);
            }
            default:
                throw new InvalidParameters("Unsupported operation: " + op + " (allowed: edit, delete)");
        }
    }

    /** Read APIs (active only) */
    public List<Permission> getAll() {
        return permissionRepository.findAllByStatusId(1);
    }

    public Optional<Permission> getById(Long id) {
        return permissionRepository.findByIdAndStatusId(id, 1);
    }
}
