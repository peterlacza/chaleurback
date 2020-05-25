package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Ingredient;
import org.springframework.data.repository.CrudRepository;

public interface IngredientRepository extends CrudRepository<Ingredient, Integer> {

}
