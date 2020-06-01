package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.CategoryHierarchy;
import hu.elte.chaleur.model.CategoryType;
import hu.elte.chaleur.model.Recipe;
import hu.elte.chaleur.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;

@RestController
@RequestMapping("/browse")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class CategoryController {
    private final CategoryService categoryService;

    @GetMapping("/categoryTypes")
    public List<CategoryType> getCategoryTypes(){
        return categoryService.getCategoryTypes();
    }

    @GetMapping("/valuesbytype")
    public Object getCategoryValuesByType(@RequestParam(required = true) String categoryType){
        return categoryService.getCategoryValuesByType(categoryType);
    }

    @GetMapping()
    public List<CategoryHierarchy> getMainCategories(){
        return categoryService.getMainCategories();
    }

    @GetMapping("/categorywinner")
    public Recipe getCategoryWinner(String subCategoryCode){
        return categoryService.getCategoryWinner(subCategoryCode);
    }

    @GetMapping("/{mainCategoryCode}")
    public List<CategoryHierarchy> browseByCategory(@PathVariable String mainCategoryCode) {
        return categoryService.browseByMainCategory(mainCategoryCode);
    }

    @GetMapping(value = "/{mainCategoryCode}", params = {"diet"})
    public HashSet<CategoryHierarchy> browseByCategoryAndDiet(@PathVariable String mainCategoryCode,
                                                              String[] diet) {
        return categoryService.browseByCategoryAndDiet(mainCategoryCode, diet);
    }

    @GetMapping(value = "/{subCategoryCode}/recipes")
    public List<Recipe> getRecipesBySubCategory(@PathVariable String subCategoryCode){
        return categoryService.getRecipesBySubCategory(subCategoryCode);
    }

}
