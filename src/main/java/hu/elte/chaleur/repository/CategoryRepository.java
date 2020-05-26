package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Category;
import hu.elte.chaleur.model.CategoryType;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryRepository extends CrudRepository<Category, Integer> {
    List<Category> findAll();
}
