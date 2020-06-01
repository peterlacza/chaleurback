package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.DailyDifferent;
import hu.elte.chaleur.model.Recipe;
import hu.elte.chaleur.repository.DailyDifferentRepository;
import hu.elte.chaleur.security.AuthService;
import hu.elte.chaleur.service.RecommendService;
import hu.elte.chaleur.specification.DailyDifferentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
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
    private final DailyDifferentRepository dailyDifferentRepository;
    private final AuthService authService;

    @GetMapping
    public List<Recipe> recommend() {
        return recommendService.recommend();
    }

    @GetMapping("/reamining")
    public Integer daysRemainsToRecommends() {
        List<DailyDifferent> dailyDifferents = dailyDifferentRepository.findAll(Specification.where(
                DailyDifferentSpecification.findAll(authService.getActUser())
        ));

        Integer remainingDayNum = 3 - dailyDifferents.size();
        return remainingDayNum;
    }
}
