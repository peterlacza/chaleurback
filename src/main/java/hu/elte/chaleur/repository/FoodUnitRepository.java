package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Food;
import hu.elte.chaleur.model.FoodUnit;
import hu.elte.chaleur.model.Unit;
import org.springframework.data.repository.CrudRepository;

public interface FoodUnitRepository extends CrudRepository<FoodUnit, Integer> {
    FoodUnit findByFoodAndUnit(Food food, Unit unit);
}
