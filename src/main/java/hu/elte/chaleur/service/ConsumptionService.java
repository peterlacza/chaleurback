package hu.elte.chaleur.service;

import hu.elte.chaleur.model.*;
import hu.elte.chaleur.repository.*;
import hu.elte.chaleur.security.AuthService;
import hu.elte.chaleur.specification.ConsumptionSpecification;
import hu.elte.chaleur.specification.DailyDifferentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ConsumptionService {
    private final AuthService authService;
    private final ConsumptionRepository consumptionRepository;
    private final NutrientRepository nutrientRepository;
    private final ReferenceValueRepository referenceValueRepository;
    private final DailyDifferentRepository dailyDifferentRepository;
    private final DailyDifferentNutrientRepository dailyDifferentNutrientRepository;
    private final UserRepository userRepository;
    private final RecipeRepository recipeRepository;
    private final NutrientValueRepository nutrientValueRepository;
    private final RatingRepository ratingRepository;

    //minden nap 0 óra 1 perckor lefut a függvény minden egyes felhasználóra
    @Scheduled(cron="0 1 0 * * *", zone="Europe/Budapest")
    public void setDailyDiff(){
        for(User user : userRepository.findAll()){
            //kérem a tegnapi fogyasztásaimat
            List<Consumption> todayConsumptions = consumptionRepository.findAll(Specification.where(
                    ConsumptionSpecification.findByUser(user)
                            .and(ConsumptionSpecification.findYesterdayConsumption())));

            List<ReferenceValue> referenceValues = referenceValueRepository.findAllByUser(user);

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
            dailyDifferent.setUser(user);
            dailyDifferent.setAverage(Boolean.FALSE);
            dailyDifferent.setDate(LocalDate.now());


            //összeadjuk tápértékenként a mai össz-fogyasztást
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

            //meghatározzuk az adott tápérték százalékos ELTÉRÉSÉT a napi ajánlott mennyiséghez képest (-100 -> ..)
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

            //lementjük a napi különbség rekordot
            dailyDifferent.setDailyDifferentNutrients(dailyDifferents);
            dailyDifferentRepository.save(dailyDifferent);

            //...és a hozzá tartozó nutrienteket is az adatbázisba
            for(DailyDifferentNutrient dailyDifferentNutrient : dailyDifferents){
                dailyDifferentNutrientRepository.save(dailyDifferentNutrient);
            }
        }
    }

    //minden nap 0 óra 15 perckor lefut a függvény minden egyes felhasználóra
    @Scheduled(cron="0 15 0 * * *", zone="Europe/Budapest")
    public void setAverageDailyDiff(){
        //az előző hány napot vegyük figyelembe?
        Integer lastDaysNumber = 3;

        //amennyiben van már legalább 3 napi fogyasztás, elkészítjük ezen százalékos értékek átlagát
        List<DailyDifferent> dailyDifferents = dailyDifferentRepository.findAll(
                Specification.where(DailyDifferentSpecification.findAllExceptAverage(authService.getActUser()))
        );

        List<ReferenceValue> referenceValues = referenceValueRepository.findAllByUser(authService.getActUser());

        if(dailyDifferents.size() >= 3){
            DailyDifferent averageDailyDifferent = new DailyDifferent();
            averageDailyDifferent.setDate(LocalDate.now());
            averageDailyDifferent.setUser(authService.getActUser());
            averageDailyDifferent.setAverage(Boolean.TRUE);

            //Feltöltünk egy listát a lehetséges tápértékekkel, alapértelmezetten nullára állítjuk a különbségeket
            List<DailyDifferentNutrient> dailyDifferentNutrientsAverage = new ArrayList<>();
            for(ReferenceValue actReferenceValue : referenceValues) {
                DailyDifferentNutrient dailyDifferentNutrient = new DailyDifferentNutrient();
                dailyDifferentNutrient.setNutrient(nutrientRepository.findByCode(actReferenceValue.getNutrientCode()));
                dailyDifferentNutrient.setDailyDifferent(averageDailyDifferent);
                dailyDifferentNutrient.setDifferent(0.0);
                dailyDifferentNutrientsAverage.add(dailyDifferentNutrient);
            }

            dailyDifferents = dailyDifferentRepository.findAll(
                    Specification.where(DailyDifferentSpecification.getLastNDays(lastDaysNumber))
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
        }
    }

    public ResponseEntity<Consumption> addConsumption(Consumption consumption){
        Double consumed = consumption.getAmount() * consumption.getRecipeUnit().getMass();
        Double multiply = consumed / 100;
        List<NutrientValue> nutrientValues = new ArrayList<>();
        for(NutrientValue actNutrient : consumption.getRecipe().getNutrientValues()){
            NutrientValue nutrientValue = new NutrientValue();
            nutrientValue.setNutrient(actNutrient.getNutrient());
            nutrientValue.setValue(actNutrient.getValue()*multiply);
            nutrientValues.add(nutrientValue);
        }
        consumption.setNutrientValues(nutrientValues);
        consumption.setUser(authService.getActUser());
        consumption.setDate(LocalDate.now());
        return ResponseEntity.ok(consumptionRepository.save(consumption));
    }

    public List<NutrientValue> consumeRecipe(Integer recipeId, Double amount){
        Recipe recipe = recipeRepository.findById(recipeId).get();
        //-> bekerül a Consumption-be
        Consumption myConsumption = new Consumption();
        myConsumption.setDate(LocalDate.now());
        myConsumption.setRecipe(recipe);
        myConsumption.setAmount(amount);
        myConsumption.setUser(authService.getActUser());

        //-> az elfogyasztott étel (fél-adag, stb..) tápértékei megy a ConsumptionNutrient-be
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

    public List<Consumption> getConsumptions(){
        return consumptionRepository.findAll(Specification.where(
                ConsumptionSpecification.findByUser(authService.getActUser())
        ));
    }

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

    public void deleteConsumption(Integer id){
        if(authService.getActUser().equals(consumptionRepository.findById(id).get().getUser())){
            consumptionRepository.deleteById(id);
        }
    }

    public void rateRecipe(Integer rateValue,
                           Integer recipeId){
        Rating userRate = new Rating();
        userRate.setRate(rateValue);
        userRate.setUser(authService.getActUser());
        userRate.setRecipe(recipeRepository.findById(recipeId).get());
        ratingRepository.save(userRate);
    }
}
