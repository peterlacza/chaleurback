package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.Nutrient;
import hu.elte.chaleur.model.ReferenceValue;
import hu.elte.chaleur.repository.NutrientRepository;
import hu.elte.chaleur.repository.ReferenceValueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/please")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class TempController {
    private final ReferenceValueRepository referenceValueRepository;
    private final NutrientRepository nutrientRepository;

    @GetMapping("/delete")
    public String home(){
        List<ReferenceValue> referenceValues = referenceValueRepository.findAll();
        List<Nutrient> nutrients = nutrientRepository.findAll();
        List<Nutrient> toDelete = new ArrayList<>();

        for(Nutrient nutrient : nutrients){
            Boolean van = false;
            for(ReferenceValue referenceValue : referenceValues){
                if(referenceValue.getNutrientCode().equals(nutrient.getCode())){
                    van = true;
                }
            }
            if(!van){
                toDelete.add(nutrient);
            }
        }

        for(Nutrient nutrient : toDelete){
            nutrientRepository.delete(nutrient);
        }

        return "ok";
    }
}
