package hu.elte.chaleur.security;

import hu.elte.chaleur.model.User;
import org.springframework.http.ResponseEntity;

public interface AuthService {
    ResponseEntity<User> register(User user);

    User getActUser();
}
