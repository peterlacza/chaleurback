package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.datastore.CategoryList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryListRepository extends CrudRepository<CategoryList, Integer> {
    List<CategoryList> findAll();
}
