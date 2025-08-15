package com.example.taskflow.conrollers;

import com.example.taskflow.dtos.UserDTO;
import com.example.taskflow.model.enums.UserRole;
import com.example.taskflow.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/users")
@CrossOrigin(origins = "http://localhost:4200")
public class UserController {

    @Autowired
    private UserService userService;

    @GetMapping
    public ResponseEntity<List<UserDTO>> getAllUsers() {
        List<UserDTO> users = userService.getAllUsers();
        return ResponseEntity.ok(users);
    }

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@RequestBody UserDTO userDTO) {
        UserDTO createdUser = userService.createUser(userDTO);
        return ResponseEntity.ok(createdUser);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO> updateUser(@PathVariable Long id, @RequestBody UserDTO userDTO) {
        UserDTO updatedUser = userService.updateUser(id, userDTO);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{id}/role")
    public ResponseEntity<UserDTO> updateUserRole(@PathVariable Long id, @RequestParam UserRole role) {
        UserDTO updatedUser = userService.updateUserRole(id, role);
        if (updatedUser != null) {
            return ResponseEntity.ok(updatedUser);
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/hide")
    public ResponseEntity<Void> hideUser(@PathVariable Long id) {
        boolean success = userService.hideUser(id);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @PutMapping("/{id}/unhide")
    public ResponseEntity<Void> unhideUser(@PathVariable Long id) {
        boolean success = userService.unhideUser(id);
        if (success) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}/hidden-status")
    public ResponseEntity<Boolean> isUserHidden(@PathVariable Long id) {
        boolean isHidden = userService.isUserHidden(id);
        return ResponseEntity.ok(isHidden);
    }

    @GetMapping("/online-status")
    public ResponseEntity<Map<String, Object>> getOnlineUsersStatus() {
        // This endpoint can be used to get initial online status
        // Real-time updates will come through WebSocket
        return ResponseEntity.ok(Map.of(
            "message", "Use WebSocket /topic/user-status for real-time updates",
            "endpoint", "/ws"
        ));
    }
}
