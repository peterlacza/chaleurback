package hu.elte.chaleur.repository;

import hu.elte.chaleur.efsa.DishTypeList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DishTypeListRepository extends CrudRepository<DishTypeList, Integer> {
    List<DishTypeList> findAll();
}
