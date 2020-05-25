package hu.elte.chaleur.specification;

import hu.elte.chaleur.model.NutrientValue;
import hu.elte.chaleur.model.Recipe;
import hu.elte.chaleur.model.User;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import java.time.LocalDateTime;

public class RecipeSpecification {

    public static Specification<Recipe> findByCategoryValue(String categoryValue){
        return (root, query, criteriaBuilder) -> {
            if(categoryValue == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.joinList("categories").get("categoryValue"), categoryValue);
        };
    }


    public static Specification<Recipe> findLatest(){
        return (root, query, criteriaBuilder) -> {
            Subquery<LocalDateTime> getLocalDates = query.subquery(LocalDateTime.class);
            Root<Recipe> myRoot = getLocalDates.from(Recipe.class);

            getLocalDates.select(criteriaBuilder.greatest(myRoot.<LocalDateTime>get("createdAt")));

            return criteriaBuilder.equal(root.get("createdAt"),getLocalDates);
        };
    }



    public static Specification<Recipe> findByName(String recipeName){
        return (root, query, criteriaBuilder) -> {
            if(recipeName == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("name"), recipeName);
        };
    }


    public static Specification<Recipe> findByUser(User user){
        return (root, query, criteriaBuilder) -> {
            if(user == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("owner"), user);
        };
    }

    public static Specification<Recipe> findCategoryWinner(){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isCategoryWinner"), Boolean.TRUE);
    }


    public static Specification<NutrientValue> findAll(Recipe recipe) {
        return (root, query, criteriaBuilder) -> {
            return criteriaBuilder.equal(root.joinList("nutrientValues"), recipe);
        };
    }
}