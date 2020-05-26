package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.Consumption;
import hu.elte.chaleur.model.NutrientValue;
import hu.elte.chaleur.service.ConsumptionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/consumption")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConsumptionController {
    private final ConsumptionService consumptionService;

    //TODO: melyiket használjuk?
    @PostMapping("/consumption")
    public ResponseEntity<Consumption> addConsumption(@RequestBody Consumption consumption){
        return consumptionService.addConsumption(consumption);
    }

    //TODO: melyiket használjuk?
    @GetMapping(params = {"recipeId", "amount"})
    public List<NutrientValue> consumeRecipe(Integer recipeId, Double amount){
        return consumptionService.consumeRecipe(recipeId, amount);
    }

    @GetMapping("/consumptions")
    public List<Consumption> getConsumptions(){
        return consumptionService.getConsumptions();
    }

    @GetMapping(value="/nutrients", params = "date")
    public List<NutrientValue> getNutrients(LocalDate date){
        return consumptionService.getNutrients(date);
    }

    @DeleteMapping(value = "/consumption", params = "id")
    public void deleteConsumption(Integer id){
        consumptionService.deleteConsumption(id);
    }

    @PostMapping("/rate")
    public void rateRecipe(@RequestParam(name="rateValue") Integer rateValue,
                           @RequestParam(name="recipeId") Integer recipeId){
        consumptionService.rateRecipe(rateValue, recipeId);
    }
}
