package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Food;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface FoodRepository extends CrudRepository<Food, Integer> {
    List<Food> findAll();
}
