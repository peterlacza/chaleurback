package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.ReferenceValue;
import hu.elte.chaleur.repository.ReferenceValueRepository;
import hu.elte.chaleur.security.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/reference")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ReferenceController {
    private final AuthService authService;
    private final ReferenceValueRepository referenceValueRepository;

    @GetMapping(value = "/getReference")
    public List<ReferenceValue> getReference(){
        return referenceValueRepository.findAllByUser(authService.getActUser());
    }
}
