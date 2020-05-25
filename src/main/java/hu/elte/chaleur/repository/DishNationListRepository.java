package hu.elte.chaleur.repository;

import hu.elte.chaleur.efsa.DishNationList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DishNationListRepository extends CrudRepository<DishNationList, Integer> {
    List<DishNationList> findAll();
}
