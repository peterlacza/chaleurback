package hu.elte.chaleur.repository;

import hu.elte.chaleur.datastore.DietList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface DietListRepository extends CrudRepository<DietList, Integer> {
    List<DietList> findAll();
}
