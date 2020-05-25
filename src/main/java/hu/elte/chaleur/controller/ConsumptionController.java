package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.*;
import hu.elte.chaleur.repository.*;
import hu.elte.chaleur.security.AuthService;
import hu.elte.chaleur.specification.ConsumptionSpecification;
import hu.elte.chaleur.specification.DailyDifferentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/eat")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class ConsumptionController {
    private final RecipeRepository recipeRepository;
    private final ConsumptionRepository consumptionRepository;
    private final NutrientValueRepository nutrientValueRepository;
    private final ReferenceValueRepository referenceValueRepository;
    private final DailyDifferentRepository dailyDifferentRepository;
    private final DailyDifferentNutrientRepository dailyDifferentNutrientRepository;
    private final NutrientRepository nutrientRepository;
    private final AuthService authService;
    private final RatingRepository ratingRepository;
    private final UnitRepository unitRepository;

    @PostMapping("/consumption")
    public ResponseEntity<Consumption> addConsumption(@RequestBody Consumption consumption){
        Double consumed = consumption.getAmount() * consumption.getRecipeUnit().getMass();
        Double amount = consumed / 100;
        List<NutrientValue> nutrientValues = new ArrayList<>();
        for(NutrientValue actNutrient : consumption.getRecipe().getNutrientValues()){
            NutrientValue nutrientValue = new NutrientValue();
            nutrientValue.setNutrient(actNutrient.getNutrient());
            nutrientValue.setValue(actNutrient.getValue()*amount);
            //nutrientValueRepository.save(nutrientValue);
            nutrientValues.add(nutrientValue);
        }
        consumption.setNutrientValues(nutrientValues);
        consumption.setUser(authService.getActUser());
        consumption.setDate(LocalDate.now());
        return ResponseEntity.ok(consumptionRepository.save(consumption));
    }

    @GetMapping(value = "dailyDiff")
    public DailyDifferent dailyDiff(){
        List<Consumption> todayConsumptions = consumptionRepository.findAll(Specification.where(
                ConsumptionSpecification.findByUser(authService.getActUser())
                        .and(ConsumptionSpecification.findTodayConsumption())));

        List<ReferenceValue> referenceValues = referenceValueRepository.findAllByUser(authService.getActUser());

        List<DailyDifferentNutrient> dailyDifferents = new ArrayList<>();

        //Készítünk egy listát a tápértékekről, alapértelmezetten mindegyik értéke nulla.
        List<NutrientValue> tempNutrients = new ArrayList<>();
        for(ReferenceValue actReferenceValue : referenceValues) {
            NutrientValue nutrientValue = new NutrientValue();
            nutrientValue.setNutrient(nutrientRepository.findByCode(actReferenceValue.getNutrientCode()));
            nutrientValue.setValue(0.0);
            tempNutrients.add(nutrientValue);
        }

        DailyDifferent dailyDifferent = new DailyDifferent();
        dailyDifferent.setUser(authService.getActUser());
        dailyDifferent.setAverage(Boolean.FALSE);
        dailyDifferent.setDate(LocalDate.now());

        for(Consumption actConsumption : todayConsumptions){
            for(NutrientValue actNutrientValue : actConsumption.getNutrientValues()){
                for(NutrientValue tempNutrientValue : tempNutrients){
                    if(actNutrientValue.getNutrient().equals(tempNutrientValue.getNutrient())){
                        tempNutrientValue.setValue(tempNutrientValue.getValue()+actNutrientValue.getValue());
                        break;
                    }
                }
            }
        }

        for(NutrientValue tempNutrientValue : tempNutrients){
            for(ReferenceValue actReferenceValue : referenceValues){
                if(tempNutrientValue.getNutrient().getCode().equals(actReferenceValue.getNutrientCode())){
                    DailyDifferentNutrient dailyDifferentNutrient = new DailyDifferentNutrient();
                    dailyDifferentNutrient.setDailyDifferent(dailyDifferent);
                    dailyDifferentNutrient.setNutrient(tempNutrientValue.getNutrient());
                    dailyDifferentNutrient.setDifferent(
                                    ((tempNutrientValue.getValue() - actReferenceValue.getDailyRecommend()) / actReferenceValue.getDailyRecommend())* 100.0
                    );
                    dailyDifferents.add(dailyDifferentNutrient);
                }
            }
        }


        dailyDifferent.setDailyDifferentNutrients(dailyDifferents);
        dailyDifferentRepository.save(dailyDifferent);

        //lementjük a nutrienteket is az adatbázisba
        for(DailyDifferentNutrient dailyDifferentNutrient : dailyDifferents){
            dailyDifferentNutrientRepository.save(dailyDifferentNutrient);
        }

        return dailyDifferent;
    }

    @GetMapping("/atlag")
    public List<DailyDifferentNutrient> average(){
        //elkészítjük az átlagot is
        List<DailyDifferent> dailyDifferents = dailyDifferentRepository.findAll(
                Specification.where(DailyDifferentSpecification.findAllExceptAverage(authService.getActUser()))
        );

        List<ReferenceValue> referenceValues = referenceValueRepository.findAllByUser(authService.getActUser());

        if(dailyDifferents.size() >= 3){
            DailyDifferent averageDailyDifferent = new DailyDifferent();
            averageDailyDifferent.setDate(LocalDate.now());
            averageDailyDifferent.setUser(authService.getActUser());
            averageDailyDifferent.setAverage(Boolean.TRUE);

            //Feltöltünk egy listát a lehetséges tápértékekkel, alapértelmezetten nullára állítjuk a különbséget
            List<DailyDifferentNutrient> dailyDifferentNutrientsAverage = new ArrayList<>();
            for(ReferenceValue actReferenceValue : referenceValues) {
                DailyDifferentNutrient dailyDifferentNutrient = new DailyDifferentNutrient();
                dailyDifferentNutrient.setNutrient(nutrientRepository.findByCode(actReferenceValue.getNutrientCode()));
                dailyDifferentNutrient.setDailyDifferent(averageDailyDifferent);
                dailyDifferentNutrient.setDifferent(0.0);
                dailyDifferentNutrientsAverage.add(dailyDifferentNutrient);
            }


            Integer lastDaysNumber = 3;
            dailyDifferents = dailyDifferentRepository.findAll(
                    Specification.where(DailyDifferentSpecification.getLastDaysAndAverage(lastDaysNumber))
            );

            //Összeadogatjuk az elmúlt napok százalékos eltéréseit
            for(DailyDifferent actDifferent : dailyDifferents){
                for(DailyDifferentNutrient actNutrient : actDifferent.getDailyDifferentNutrients()){
                    for(DailyDifferentNutrient actAverageNutrient : dailyDifferentNutrientsAverage){
                        if(actNutrient.getNutrient().equals(actAverageNutrient.getNutrient())){
                            actAverageNutrient.setDifferent(actAverageNutrient.getDifferent() + actNutrient.getDifferent());
                        }
                    }
                }
            }

            //Vesszük az átlagot minden tápérték százalékos eltérésére
            for(DailyDifferentNutrient actNutrient : dailyDifferentNutrientsAverage){
                actNutrient.setDifferent(actNutrient.getDifferent() / lastDaysNumber);
            }
            averageDailyDifferent.setDailyDifferentNutrients(dailyDifferentNutrientsAverage);
            dailyDifferentRepository.save(averageDailyDifferent);
            for(DailyDifferentNutrient dailyDifferentNutrient :averageDailyDifferent.getDailyDifferentNutrients()){
                dailyDifferentNutrientRepository.save(dailyDifferentNutrient);
            }
            return dailyDifferentNutrientsAverage;
        }
        return null;
    }

    @GetMapping(params = {"recipeId", "amount"})
    public List<NutrientValue> consumeRecipe(Integer recipeId, Double amount){
        Recipe recipe = recipeRepository.findById(recipeId).get();
        //-> bekerül a Consumption-be
        Consumption myConsumption = new Consumption();
        myConsumption.setDate(LocalDate.now());
        myConsumption.setRecipe(recipe);
        myConsumption.setAmount(amount);
        myConsumption.setUser(authService.getActUser());

        //-> az elfogyasztott étel (fél-adag, stb..) megy a ConsumptionNutrient-be
        List<NutrientValue> nutrientValues = new ArrayList<>();
        for(NutrientValue actNutrient : recipe.getNutrientValues()){
            NutrientValue nutrientValue = new NutrientValue();
            nutrientValue.setNutrient(actNutrient.getNutrient());
            nutrientValue.setValue(actNutrient.getValue()*amount);
            nutrientValueRepository.save(nutrientValue);
            nutrientValues.add(nutrientValue);
        }
        myConsumption.setNutrientValues(nutrientValues);
        consumptionRepository.save(myConsumption);
        return nutrientValues;
    }

    @GetMapping("/consumptions")
    public List<Consumption> getConsumptions(){
        return consumptionRepository.findAll(Specification.where(
                ConsumptionSpecification.findByUser(authService.getActUser())
        ));
    }

    @GetMapping(value="/nutrients", params = "date")
    public List<NutrientValue> getNutrients(LocalDate date){
        List<Consumption> todayConsumptions = consumptionRepository.findAll(Specification.where(
                ConsumptionSpecification.findByUser(authService.getActUser())
                        .and(ConsumptionSpecification.findConsumptionByDate(date))));

        List<Nutrient> nutrients = nutrientRepository.findAll();

        //Készítünk egy listát a tápértékekről, alapértelmezetten mindegyik értéke nulla.
        List<NutrientValue> todayNutrients = new ArrayList<>();
        for(Nutrient actNutrient : nutrients) {
            NutrientValue nutrientValue = new NutrientValue();
            nutrientValue.setNutrient(actNutrient);
            nutrientValue.setValue(0.0);
            todayNutrients.add(nutrientValue);
        }

        for(Consumption actConsumption : todayConsumptions){
            for(NutrientValue actNutrientValue : actConsumption.getNutrientValues()){
                for(NutrientValue todayNutrientValue : todayNutrients){
                    if(actNutrientValue.getNutrient().equals(todayNutrientValue.getNutrient())){
                        todayNutrientValue.setValue(todayNutrientValue.getValue()+actNutrientValue.getValue());
                        break;
                    }
                }
            }
        }

        return todayNutrients;
    };

    @DeleteMapping(value = "/consumption", params = "id")
    public void deleteConsumption(Integer id){
        if(authService.getActUser().equals(consumptionRepository.findById(id).get().getUser())){
            consumptionRepository.deleteById(id);
        }
    }

    @PostMapping("/rate")
    public void rateRecipe(@RequestParam(name="rateValue") Integer rateValue,
                           @RequestParam(name="recipeId") Integer recipeId){
        Rating userRate = new Rating();
        userRate.setRate(rateValue);
        userRate.setUser(authService.getActUser());
        userRate.setRecipe(recipeRepository.findById(recipeId).get());
        ratingRepository.save(userRate);
    }
}
