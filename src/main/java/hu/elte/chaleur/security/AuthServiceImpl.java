package hu.elte.chaleur.security;

import hu.elte.chaleur.model.User;
import hu.elte.chaleur.repository.UserRepository;
import hu.elte.chaleur.service.ReferenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ReferenceService referenceService;

    @Override
    public ResponseEntity<User> register(User user) {
        Optional<User> oUser = userRepository.findByUsername(user.getUsername());
        if (oUser.isPresent()) {
            return ResponseEntity.badRequest().build();
        }
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.addRole("ADMIN");

        ResponseEntity<User> response = ResponseEntity.ok(userRepository.save(user));

        return response;
    }


    @Override
    public User getActUser() throws UsernameNotFoundException {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String userName = ((UserDetails) principal).getUsername();
        Optional<User> oUser = userRepository.findByUsername(userName);
        if (!oUser.isPresent()) {
            throw new UsernameNotFoundException(userName);
        }
        User actUser = oUser.get();
        if(actUser.getReferenceValues().size() == 0){
            referenceService.setNutrientsRef(actUser);
        }
        return actUser;
    }
}

