package hu.elte.chaleur.repository;

import hu.elte.chaleur.datastore.CategoryList;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryListRepository extends CrudRepository<CategoryList, Integer> {
    CategoryList findByCode(String code);
    List<CategoryList> findAll();
}
