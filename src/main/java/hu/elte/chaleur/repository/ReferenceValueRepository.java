package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.ReferenceValue;
import hu.elte.chaleur.model.User;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface ReferenceValueRepository extends CrudRepository<ReferenceValue, Integer> {
    List<ReferenceValue> findAllByUser(User user);
    List<ReferenceValue> findAll();
}