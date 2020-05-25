package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Rating;
import org.springframework.data.repository.CrudRepository;

public interface RatingRepository extends CrudRepository<Rating, Integer> {

}
