package hu.elte.chaleur.service;

import hu.elte.chaleur.model.*;
import hu.elte.chaleur.repository.*;
import hu.elte.chaleur.security.AuthService;
import hu.elte.chaleur.specification.RecipeSpecification;
import hu.elte.chaleur.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final FoodRepository foodRepository;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final ImageRepository imageRepository;
    private final IngredientRepository ingredientRepository;
    private final CategoryRepository categoryRepository;
    private final NutrientRepository nutrientRepository;
    private final NutrientValueRepository nutrientValueRepository;
    private final FoodUnitRepository foodUnitRepository;
    private final UnitRepository unitRepository;
    private final RecipeUnitRepository recipeUnitRepository;

    public ResponseEntity<List<Recipe>> searchForRecipes(Specification<Recipe> specs){
        return new ResponseEntity<>(recipeRepository.findAll(Specification.where(specs)), HttpStatus.OK);
    }

    public void addRecipe(Recipe recipe){
        Recipe actRecipe = new Recipe();
        actRecipe.setName(recipe.getName());
        actRecipe.setOwner(authService.getActUser());
        actRecipe.setDescription(recipe.getDescription());
        recipeRepository.save(actRecipe);
        List<Ingredient> ingredients = new ArrayList<>();
        for(Ingredient ingredientData : recipe.getIngredients()){
            Ingredient ingredient = new Ingredient();
            ingredient.setRecipe(actRecipe);
            ingredient.setAmount(ingredientData.getAmount());
            Food food = foodRepository.findById(ingredientData.getFood().getId()).get();
            ingredient.setFood(food);
            ingredient.setUnit(ingredientData.getUnit());
            ingredientRepository.save(ingredient);
            ingredients.add(ingredient);
        }
        actRecipe.setIngredients(ingredients);

        List<Category> categories = new ArrayList<>();
        for(Category categoryData: recipe.getCategories()){
            Category category = new Category();
            category.setCategoryValue(categoryData.getCategoryValue());
            category.setCategoryType(categoryData.getCategoryType());
            category.setRecipe(actRecipe);
            categoryRepository.save(category);
            categories.add(category);
        }
        actRecipe.setCategories(categories);
        recipeRepository.save(actRecipe);

        Recipe savedRecipe = recipeRepository.findAll(RecipeSpecification.findByName(actRecipe.getName()).and(RecipeSpecification.findByUser(authService.getActUser()))).get(0);

        List<Nutrient> nutrients = nutrientRepository.findAll();
        List<NutrientValue> nutrientValues = new ArrayList<>();
        for(Nutrient nutrient : nutrients){
            NutrientValue nutrientValue =  new NutrientValue();
            nutrientValue.setNutrient(nutrient);
            nutrientValue.setValue(0.0);
            for(Ingredient ingredient : savedRecipe.getIngredients()){
                Integer mass = foodUnitRepository.findByFoodAndUnit(ingredient.getFood(), ingredient.getUnit()).getMass();
                Double multiply = (ingredient.getAmount() * mass) / 100.0;
                for(NutrientValue recipeNutrientValue : ingredient.getFood().getNutrientValues()){
                    if(recipeNutrientValue.getNutrient().getCode().equals(nutrient.getCode())){
                        nutrientValue.setValue(nutrientValue.getValue() + (recipeNutrientValue.getValue() * multiply));
                        break;
                    }
                }
            }
            nutrientValueRepository.save(nutrientValue);
            nutrientValues.add(nutrientValue);
        }
        actRecipe.setNutrientValues(nutrientValues);
        recipeRepository.save(actRecipe);

        List<RecipeUnit> recipeUnits = new ArrayList<>();

        RecipeUnit portL = new RecipeUnit();
        portL.setRecipe(actRecipe);
        portL.setUnit(unitRepository.findByCode("PORTL"));
        portL.setMass(200);
        recipeUnitRepository.save(portL);
        recipeUnits.add(portL);

        RecipeUnit portM = new RecipeUnit();
        portM.setRecipe(actRecipe);
        portM.setUnit(unitRepository.findByCode("PORTM"));
        portM.setMass(100);
        recipeUnitRepository.save(portM);
        recipeUnits.add(portM);

        RecipeUnit portS = new RecipeUnit();
        portS.setRecipe(actRecipe);
        portS.setUnit(unitRepository.findByCode("PORTS"));
        portS.setMass(40);
        recipeUnitRepository.save(portS);
        recipeUnits.add(portS);

        actRecipe.setRecipeUnits(recipeUnits);
        recipeRepository.save(actRecipe);

    }

    public void addRecipeImg(MultipartFile picture, String recipeName) throws InterruptedException, IOException {
        Thread.sleep(2000);
        Recipe recipe = recipeRepository.findAll(Specification.where(
                RecipeSpecification.findByUser(authService.getActUser())
                        .and(RecipeSpecification.findByName(recipeName)))).get(0);
        Image image = new Image();
        image.setName(picture.getName());
        image.setType(picture.getContentType());
        image.setPicByte(picture.getBytes());
        image = imageRepository.save(image);
        recipe.setPicture(image);
        recipeRepository.save(recipe);
    }

    public void deleteRecipe(Integer recipeId){
        if(recipeRepository.findById(recipeId).get().getOwner().equals(authService.getActUser())){
            recipeRepository.deleteById(recipeId);
        }
    }

    public Recipe getRecipe(Integer id){
        return recipeRepository.findById(id).get();
    }

    public List<Recipe> getRecipesByUser(String userName){
        return recipeRepository.findAll(Specification.where(
                RecipeSpecification.findByUser(userRepository.findByUsername(userName).get())
        ));
    }

    public List<Food> getFoods(){
        return foodRepository.findAll();
    }

    public List<Recipe> getLatestRecipes(){
        List<User> myFollowings = userRepository.findAll(Specification.where(
                UserSpecification.findMyFollowings(authService.getActUser())
        ));

        List<Recipe> latestRecipes = new ArrayList<>();
        for(User user : myFollowings){
            List<Recipe> recipes = user.getRecipes();

            if(recipes.size() > 0){
                Collections.sort(recipes, (r1, r2) -> {
                    if (r1.getId() == r2.getId())
                        return 0;
                    return r1.getId() > r2.getId() ? -1 : 1;
                });

                latestRecipes.add(recipes.get(0));
            }
        }

        return latestRecipes;
    }

    public List<User> recommendUsers(){
        List<User> myFollowings = userRepository.findAll(Specification.where(
                UserSpecification.findMyFollowings(authService.getActUser())
        ));

        List<User> users = userRepository.findAll();

        Collections.sort(users, (u1, u2) -> {
            if (u1.getRecipes().size() == u2.getRecipes().size())
                return 0;
            return u1.getRecipes().size() > u2.getRecipes().size() ? -1 : 1;
        });

        Integer deletedNumber = 0;
        for(int i=0;i<10+deletedNumber;i++){
            for(User following : myFollowings){
                if(users.get(i).equals(following)){
                    users.remove(i);
                    deletedNumber++;
                    break;
                }
            }
        }

        for(int i=0;i<10;i++){
            if(users.get(i).equals(authService.getActUser())){
                users.remove(i);
            }
        }

        return users.subList(0,10);
    }

    public List<Recipe> findFavouritesByUser(String userName){
        return userRepository.findByUsername(userName).get().getFavourites();
    }

    public ResponseEntity<User> addFavourite(Integer recipeId){
        User user = authService.getActUser();
        List<Recipe> favourites = user.getFavourites();
        favourites.add(recipeRepository.findById(recipeId).get());
        user.setFavourites(favourites);
        return ResponseEntity.ok(userRepository.save(user));
    }

    public void removeFavourite(Integer recipeId){
        User user = authService.getActUser();
        List<Recipe> favourites = user.getFavourites();
        favourites.remove(recipeRepository.findById(recipeId).get());
        user.setFavourites(favourites);
        userRepository.save(user);
    }

}
