package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.Category;
import hu.elte.chaleur.model.CategoryHierarchy;
import hu.elte.chaleur.model.CategoryType;
import hu.elte.chaleur.model.Recipe;
import hu.elte.chaleur.repository.*;
import hu.elte.chaleur.specification.RecipeSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/browse")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryHierarchyRepository categoryHierarchyRepository;
    private final RecipeRepository recipeRepository;
    private final DietListRepository dietListRepository;
    private final CategoryListRepository categoryListRepository;
    private final CategoryRepository categoryRepository;
    private final DishEventListRepository dishEventListRepository;
    private final DishNationListRepository dishNationListRepository;
    private final DishTypeListRepository dishTypeListRepository;

    @GetMapping("/categoryTypes")
    public List<CategoryType> getCategoryTypes(){
        List<CategoryType> enumValues = new ArrayList<>(EnumSet.allOf(CategoryType.class));
        enumValues.remove(CategoryType.SUBCATEGORY);
        enumValues.remove(CategoryType.MAINCATEGORY);
        return enumValues;
    }

    @GetMapping("/typevalue")
    public Object getCategoryValuesByType(@RequestParam(required = true) String categoryType){
        CategoryType type = CategoryType.fromValue(categoryType);

        switch(type) {
            case DIETA:
                return dietListRepository.findAll();
            case ETKEZESTIPUS:
                return dishTypeListRepository.findAll();
            case ALKALOM:
                return dishEventListRepository.findAll();
            case ORSZAG:
                return dishNationListRepository.findAll();
        }
        return null;
    }

    @GetMapping("")
    public List<CategoryHierarchy> browseHome(){
        List<CategoryHierarchy> categoryHierarchies = categoryHierarchyRepository.findAll();
        List<CategoryHierarchy> categoriesToOut = new ArrayList<>();
        HashSet<String> mainCategories = new HashSet<>();
        for(CategoryHierarchy actCategoryHierarchy : categoryHierarchies){
            if(!mainCategories.contains(actCategoryHierarchy.getMainCategoryCode())){
                mainCategories.add(actCategoryHierarchy.getMainCategoryCode());
                categoriesToOut.add(actCategoryHierarchy);
            }
        }
        return categoriesToOut;
    }

    @GetMapping("/categorywinner")
    public Recipe getCategoryWinner(String subCategoryCode){
        List<Recipe> recipes = recipeRepository.findAll(Specification.where(
                RecipeSpecification.findByCategoryValue(subCategoryCode)
                        .and(RecipeSpecification.findCategoryWinner())));
        return recipes.get(0);
    }

    @GetMapping("/{mainCategoryCode}")
    public List<CategoryHierarchy> browseByCategory(@PathVariable String mainCategoryCode) {
        List<CategoryHierarchy> subCategories =  categoryHierarchyRepository.findAllByMainCategoryCode(mainCategoryCode);

        return subCategories;
    }

    @GetMapping(value = "/{mainCategoryCode}", params = {"diet"})
    public HashSet<CategoryHierarchy> browseByCategoryAndDiet(@PathVariable String mainCategoryCode,
                                                              String[] diet) {

        List<CategoryHierarchy> subCategories =  categoryHierarchyRepository.findAllByMainCategoryCode(mainCategoryCode);

        List<Recipe> recipesByDiet = recipeRepository.findAll(Specification.where(
                RecipeSpecification.findByCategoryValue(mainCategoryCode)
                        .and(RecipeSpecification.findByCategoryValue(diet[0]))));
        HashSet<Recipe> myRecipes = new HashSet<>();
        myRecipes.addAll(recipesByDiet);

        for(int i=1; i<diet.length;i++){
            List<Recipe> recipesByDiet2 = recipeRepository.findAll(Specification.where(
                    RecipeSpecification.findByCategoryValue(mainCategoryCode)
                            .and(RecipeSpecification.findByCategoryValue(diet[i]))));
            HashSet<Recipe> myRecipes2 = new HashSet<>();
            myRecipes2.addAll(recipesByDiet2);
            myRecipes.retainAll(myRecipes2);
        }


        HashSet<String> myCategories = new HashSet<>();
        for(Recipe actRecipe : myRecipes){
            List<Category> recipeCategList = actRecipe.getCategories();
            for(Category actCateg : recipeCategList){
                if(actCateg.getCategoryType().equals(CategoryType.SUBCATEGORY)){
                    myCategories.add(actCateg.getCategoryValue());
                }
            }
        }

        HashSet<CategoryHierarchy> filteredCategories = new HashSet<>();
        for(String actSubCateg : myCategories){
            for(CategoryHierarchy actCat : subCategories){
                if(actCat.getSubCategoryCode().equals(actSubCateg)){
                    filteredCategories.add(actCat);
                }
            }
        }

        return filteredCategories;

    }

    @GetMapping(value = "/{subCategoryCode}/recipes")
    public List<Recipe> getRecipesBySubCategory(@PathVariable String subCategoryCode){
        List<Recipe> recipes = recipeRepository.findAll(Specification.where(
                RecipeSpecification.findByCategoryValue(subCategoryCode))
        );
        return recipes;
    }

}
