package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.datastore.DishEventList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DishEventListRepository extends CrudRepository<DishEventList, Integer> {
    List<DishEventList> findAll();
}

