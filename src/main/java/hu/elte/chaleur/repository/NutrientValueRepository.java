package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.NutrientValue;
import org.springframework.data.repository.CrudRepository;

public interface NutrientValueRepository extends CrudRepository<NutrientValue, Integer> {
}
