package hu.elte.chaleur.controller;

import com.sipios.springsearch.anotation.SearchSpec;
import hu.elte.chaleur.model.Food;
import hu.elte.chaleur.model.Recipe;
import hu.elte.chaleur.model.User;
import hu.elte.chaleur.service.RecipeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;


@RestController
@RequestMapping("/recipes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class RecipeController {
    private final RecipeService recipeService;

    @GetMapping("/find")
    public ResponseEntity<List<Recipe>> searchForRecipes(@SearchSpec Specification<Recipe> specs){
        return recipeService.searchForRecipes(specs);
    }

    @PostMapping("/upload")
    public void addRecipe(@RequestBody Recipe recipe){
        recipeService.addRecipe(recipe);
    }

    @PostMapping("/addImage")
    public void addRecipeImg(@RequestParam("picture")MultipartFile picture,
                             @RequestParam("recipeName") String recipeName) throws InterruptedException, IOException {
        recipeService.addRecipeImg(picture, recipeName);
    }

    @DeleteMapping("/delete")
    public void deleteRecipe(@RequestParam(value = "recipeId", required = true) Integer recipeId){
        recipeService.deleteRecipe(recipeId);
    }

    @GetMapping("/recipe")
    public Recipe getRecipe(@RequestParam("id") Integer id){
        return recipeService.getRecipe(id);
    }

    @GetMapping
    public List<Recipe> getRecipesByUser(String userName){
        return recipeService.getRecipesByUser(userName);
    }

    @GetMapping("/foods")
    public List<Food> getFoods(){
        return recipeService.getFoods();
    }

    @GetMapping("/latest")
    public List<Recipe> getLatestRecipes(){
        return recipeService.getLatestRecipes();
    }

    @GetMapping("/recommendUsers")
    public List<User> recommendUsers(){
        return recipeService.recommendUsers();
    }

    @PostMapping("/favourite")
    public ResponseEntity<User> addFavourite(@RequestParam(value = "recipeId", required = true) Integer recipeId){
        return recipeService.addFavourite(recipeId);
    }

    @GetMapping("/favourites")
    public List<Recipe> findFavouritesByUser(String userName){
        return recipeService.findFavouritesByUser(userName);
    }

    @DeleteMapping("/favourite")
    public void removeFavourite(@RequestParam(value = "recipeId", required = true) Integer recipeId){
        recipeService.removeFavourite(recipeId);
    }
}
