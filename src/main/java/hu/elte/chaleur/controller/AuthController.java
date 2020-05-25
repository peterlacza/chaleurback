package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.User;
import hu.elte.chaleur.repository.UserRepository;
import hu.elte.chaleur.security.AuthServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
@RestController
@RequestMapping("/auth")
public class AuthController {
    private final ReferenceController referenceController;
    private final AuthServiceImpl authService;
    private final UserRepository userRepository;

    @PostMapping("/register")
    public ResponseEntity<User> register(@RequestBody User user) {
        ResponseEntity<User> response = authService.register(user);
        if(response.getStatusCode().is2xxSuccessful()){
            referenceController.setNutrientsRef(userRepository.findByUsername(user.getUsername()).get());
        }
        return response;
    }

    @PostMapping("/login")
    public ResponseEntity<User> login() {
        return ResponseEntity.ok(authService.getActUser());
    }
}
