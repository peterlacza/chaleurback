package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.User;
import hu.elte.chaleur.repository.UserRepository;
import hu.elte.chaleur.security.AuthServiceImpl;
import hu.elte.chaleur.service.ReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthServiceImpl authService;
    private final ReferenceService referenceService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {

        ResponseEntity<User> response = authService.register(user);
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login() {
        return ResponseEntity.ok(authService.getActUser());
    }
}
