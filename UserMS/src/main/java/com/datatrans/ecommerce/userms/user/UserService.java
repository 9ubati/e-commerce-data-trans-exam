package com.datatrans.ecommerce.userms.user;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.operationHelper.errorHandler.InvalidParameters;
import com.operationHelper.utils.HelperFunctions;

@Service
public class UserService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private HelperFunctions helperFunctions;

	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private PasswordEncoder passwordEncoder;

	// Create a new user
	public User post(Map<String, Object> requestMap) {

		helperFunctions.validateFields(requestMap, new User().fields());

		User user = objectMapper.convertValue(requestMap, User.class);

		// approved status
		user.setStatusId(1);

		user.setPassword(passwordEncoder.encode(user.getPassword()));

		// validation
		if (userRepository.existsByUsernameAndStatusId(user.getUsername() ,1)) {
			throw new InvalidParameters("user with username [" + user.getUsername() + "] is already exist");
		}

		return userRepository.save(user);
	}

	public User patch(Map<String, Object> requestMap) {
	    // 0) guards
	    String op = String.valueOf(requestMap.get("operation")).trim();
	    if (op.isBlank()) throw new InvalidParameters("operation is mandatory");

	    Object idRaw = requestMap.get("id");
	    if (idRaw == null || idRaw.toString().isBlank()) throw new InvalidParameters("id is mandatory");
	    Long id = Long.parseLong(idRaw.toString());

	    User user = userRepository.findByIdAndStatusId(id, 1)
	        .orElseThrow(() -> new InvalidParameters("User with id [" + id + "] not found"));

	    switch (op.toLowerCase()) {
	        case "delete": {
	            // soft delete
	            user.setStatusId(0);
	            return userRepository.save(user);
	        }
	        case "edit": {
	            // ---- build updates (ignore control fields)
	            var allDefs = new User().fields();
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
	                if (!updatable.contains(k)) continue; // or collect error if you prefer
	                updates.put(k, e.getValue());
	            }
	            if (updates.isEmpty()) throw new InvalidParameters("No updatable fields in request.");

	            // ---- validate types ONLY for provided fields (make them non-mandatory for PATCH)
//	            var defsForPresent = allDefs.stream()
//	                .filter(m -> updates.containsKey(String.valueOf(m.get("fieldName"))))
//	                .map(m -> { var c = new java.util.HashMap<>(m); c.put("mandatory", false); return c; })
//	                .toList();
	            
//	            helperFunctions.validateFields(updates, defsForPresent);

	            // ---- apply changes (coercion via ObjectMapper), hash password if present
	            BeanWrapper bw = new BeanWrapperImpl(user);
	            for (var e : updates.entrySet()) {
	                String name = e.getKey();
	                Object raw = e.getValue();

	                if (name.equals("password")) {
	                    if (raw == null || raw.toString().isBlank())
	                        throw new InvalidParameters("password cannot be blank in PATCH");
	                    user.setPassword(passwordEncoder.encode(raw.toString()));
	                    continue;
	                }

	                Class<?> targetType = bw.getPropertyType(name);
	                Object coerced = (raw == null ? null : objectMapper.convertValue(raw, targetType));
	                bw.setPropertyValue(name, coerced);
	            }

	            // ---- DB uniqueness (only if those keys changed/present)
	            if (updates.containsKey("username") && userRepository.existsByUsernameAndIdNotAndStatusId(user.getUsername(), user.getId(), 1))
	                throw new InvalidParameters("User already exists with the same username.");
	            if (updates.containsKey("email") && user.getEmail() != null
	                    && userRepository.existsByEmailAndIdNotAndStatusId(user.getEmail(), user.getId(), 1))
	                throw new InvalidParameters("User already exists with the same email.");
	            if (updates.containsKey("mobile") && user.getMobile() != null
	                    && userRepository.existsByMobileAndIdNotAndStatusId(user.getMobile(), user.getId(), 1))
	                throw new InvalidParameters("User already exists with the same mobile.");

	            return userRepository.save(user);
	        }
	        default:
	            throw new InvalidParameters("Unsupported operation: " + op + " (allowed: edit, delete)");
	    }
	}

	// Get all users
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	// Get a user by ID
	public Optional<User> getUserById(Long id) {
		return userRepository.findById(id);
	}

	// Update a user
//    public User updateUser(Long id, User userDetails) {
//        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
//        user.setName(userDetails.getName());
//        user.setEmail(userDetails.getEmail());
//        return userRepository.save(user);
//    }
//
//    // Delete a user
//    public void deleteUser(Long id) {
//        userRepository.deleteById(id);
//    }

}
