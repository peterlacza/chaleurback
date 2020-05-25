package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.DailyDifferent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface DailyDifferentRepository extends  JpaRepository<DailyDifferent, Integer>, JpaSpecificationExecutor<DailyDifferent> {

}
