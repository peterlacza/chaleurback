package hu.elte.chaleur.service;

import hu.elte.chaleur.model.*;
import hu.elte.chaleur.repository.*;
import hu.elte.chaleur.security.AuthService;
import hu.elte.chaleur.specification.ConsumptionSpecification;
import hu.elte.chaleur.specification.DailyDifferentSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecommendService {
    private final RecipeRepository recipeRepository;
    private final DailyDifferentRepository dailyDifferentRepository;
    private final ReferenceValueRepository referenceValueRepository;
    private final NutrientRepository nutrientRepository;
    private final ConsumptionRepository consumptionRepository;
    private final AuthService authService;

    public List<Recipe> recommend(){
        //Kérem a saját referencia értékeimet
        List<ReferenceValue> referenceValues = referenceValueRepository.findAllByUser(authService.getActUser());

        //Készítünk egy listát a tápértékekről, alapértelmezetten minden nulla
        List<NutrientValue> dailyNutrientValues = new ArrayList<>();
        for(ReferenceValue actReferenceValue : referenceValues){
            NutrientValue nutrientValue = new NutrientValue();
            nutrientValue.setNutrient(nutrientRepository.findByCode(actReferenceValue.getNutrientCode()));
            nutrientValue.setValue(0.0);
            dailyNutrientValues.add(nutrientValue);
        }

        //Ha volt ma már fogyasztás, azt figyelembe vesszük az új recept ajánlásánál.
        List<Consumption> todayConsumptions = consumptionRepository.findAll(Specification.where(ConsumptionSpecification.findTodayConsumption()));
        for(Consumption actConsumption : todayConsumptions){
            for(NutrientValue consumptioNutrient : actConsumption.getNutrientValues()){
                for(NutrientValue dailyNutrient : dailyNutrientValues){
                    if(dailyNutrient.getNutrient().equals(consumptioNutrient.getNutrient())){
                        dailyNutrient.setValue(dailyNutrient.getValue() + consumptioNutrient.getValue());
                    }
                }
            }
        }

        //Kérem a mai DailyDifferent átlagot (ez az elmúlt 3 nap összesített ELTÉRÉSEINEK átlaga)
        DailyDifferent dailyDifferent = dailyDifferentRepository.findAll(Specification.where(
                DailyDifferentSpecification.findLastAvarageByUser(authService.getActUser())
        )).get(0);

        Double lastDaysNumber = 3.0;

        List<Recipe> recipes = recipeRepository.findAll();

        //Eltávolítjuk a listából azokat a recepteket, melyeket fogyasztott a user az utolsó néhány (3) napban.
        List<Consumption> lastDaysConsumptions = consumptionRepository.findAll(Specification.where(ConsumptionSpecification.getLastDays((int)Math.round(lastDaysNumber))));
        for(int i=0;i<recipes.size();i++){
            for(Consumption consumption : lastDaysConsumptions){
                if(recipes.get(i).getId().equals(consumption.getRecipe().getId())){
                    recipes.remove(recipes.get(i));
                }
            }
        }

        //Ajánlás algoritmus
        List<RecipeCompare> recipeCompares = new ArrayList<>();
        for(Recipe recipe : recipes){
            RecipeCompare recipeCompare = new RecipeCompare();
            recipeCompare.setRecipeId(recipe.getId());
            recipeCompare.setNegativeChange(0.0);
            recipeCompare.setPositiveChange(0.0);

            for(NutrientValue recipeNutrientValue : recipe.getNutrientValues()){
                Double calculatedNutrientValue = 0.0;
                Double newDifferentValue = null;

                //Itt hozzáadjuk az esetleges mai fogyasztást a recept aktuális tápanyagához
                for(NutrientValue dailyNutrientValue : dailyNutrientValues){
                    if(recipeNutrientValue.getNutrient().equals(dailyNutrientValue.getNutrient())){
                        calculatedNutrientValue = recipeNutrientValue.getValue() + dailyNutrientValue.getValue();
                        break;
                    }
                }
                //Itt meghatározzuk, hogy ha megennénk ezt a receptet, akkor mekkora lenne az új napi eltérés a referenciához képest
                for(ReferenceValue referenceValue : referenceValues){
                    if(referenceValue.getNutrientCode().equals(recipeNutrientValue.getNutrient().getCode())){
                        newDifferentValue = ((calculatedNutrientValue - referenceValue.getDailyRecommend())/referenceValue.getDailyRecommend())*100.0;
                        break;
                    }
                }

                //Itt megnézzük a JAVULÁST a negatívokra, és a pozitívokra
                for(DailyDifferentNutrient lastAverageDifferent : dailyDifferent.getDailyDifferentNutrients()){
                    if(lastAverageDifferent.getNutrient().equals(recipeNutrientValue.getNutrient())){
                        Double newAverage = ((lastAverageDifferent.getDifferent() * lastDaysNumber) + newDifferentValue) / (lastDaysNumber+1);
                        Double changeValue;

                        if(lastAverageDifferent.getDifferent() < 0.0){
                            //az eredetileg negativok közül mennyit fejlődtünk (-99 -> +100, minél magasabb az érték annál jobb)
                            changeValue = Math.abs(lastAverageDifferent.getDifferent());
                            if(newAverage < 0){
                                changeValue = newAverage - lastAverageDifferent.getDifferent();
                            }
                            recipeCompare.setNegativeChange(recipeCompare.getNegativeChange() + changeValue);
                        } else{
                            //az eredetileg pozitívok közül mennyit fejlődtünk (mennyit közeledtünk a nullához, minél alacsonyabb érték annál jobb)
                            changeValue = lastAverageDifferent.getDifferent() * -1.0;
                            if(newAverage > 0){
                                changeValue = newAverage - lastAverageDifferent.getDifferent();
                            }
                            recipeCompare.setPositiveChange(recipeCompare.getPositiveChange() + changeValue);
                        }
                        break;
                    }
                }
            }
            recipeCompares.add(recipeCompare);
        }

        //Rendezzük először a negatívok szerint
        Collections.sort(recipeCompares, (r1, r2) -> {
            if (r1.getNegativeChange() == r2.getNegativeChange())
                return 0;
            return r1.getNegativeChange() > r2.getNegativeChange() ? -1 : 1;
        });

        //Majd a top10 legjobb negatív fejlődés közül rendezzük a pozitívak szerint
        Collections.sort(recipeCompares.subList(0,10), (r1, r2) -> {
            if (r1.getPositiveChange() == r2.getPositiveChange())
                return 0;
            return r1.getPositiveChange() < r2.getPositiveChange() ? -1 : 1;
        });

        //Kiíratás
        List<RecipeCompare> recommendedRecipes = recipeCompares.subList(0,5);
        List<Recipe> recipesToOut = new ArrayList<>();
        for(RecipeCompare filteredRecipe :  recommendedRecipes){
            for(Recipe recipe : recipes){
                if(filteredRecipe.getRecipeId().equals(recipe.getId())){
                    recipesToOut.add(recipe);
                    break;
                }
            }
        }
        return recipesToOut;
    }

}
