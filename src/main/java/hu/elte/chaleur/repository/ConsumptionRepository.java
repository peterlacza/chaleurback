package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.Consumption;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ConsumptionRepository extends JpaRepository<Consumption, Integer>, JpaSpecificationExecutor<Consumption> {

}
