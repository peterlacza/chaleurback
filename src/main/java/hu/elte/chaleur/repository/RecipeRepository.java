package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Recipe;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface RecipeRepository extends JpaRepository<Recipe, Integer>, JpaSpecificationExecutor<Recipe> {
    @EntityGraph(attributePaths = {"nutrientValues"})
    List<Recipe> findAll();
}
