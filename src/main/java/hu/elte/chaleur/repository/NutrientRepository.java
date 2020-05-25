package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Nutrient;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface NutrientRepository extends CrudRepository<Nutrient, Integer> {
    Nutrient findByCode(String code);
    List<Nutrient> findAll();
}
