package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Unit;
import org.springframework.data.repository.CrudRepository;

public interface UnitRepository extends CrudRepository<Unit, Integer> {
    Unit findByCode(String code);
}
