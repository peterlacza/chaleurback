package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.ReferenceValue;
import hu.elte.chaleur.service.ReferenceService;
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
    private final ReferenceService referenceService;

    @GetMapping(value = "/getReference")
    public List<ReferenceValue> getReference(){
        return referenceService.getReference();
    }
}
