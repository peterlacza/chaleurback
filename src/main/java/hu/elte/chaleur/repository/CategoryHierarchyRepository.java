package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.CategoryHierarchy;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface CategoryHierarchyRepository extends CrudRepository<CategoryHierarchy, Integer> {

    List<CategoryHierarchy> findAllByMainCategoryCode(String mainCategoryCode);
    List<CategoryHierarchy> findAll();
}
