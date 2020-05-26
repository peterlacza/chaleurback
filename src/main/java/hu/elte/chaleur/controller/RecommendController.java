package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.Recipe;
import hu.elte.chaleur.service.RecommendService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/recommend")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class RecommendController {
    private final RecommendService recommendService;

    @GetMapping
    public List<Recipe> recommend() {
        return recommendService.recommend();
    }

}
