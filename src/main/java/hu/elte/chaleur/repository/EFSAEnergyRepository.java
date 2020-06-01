package hu.elte.chaleur.repository;

import hu.elte.chaleur.model.datastore.EFSAEnergy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EFSAEnergyRepository extends JpaRepository<EFSAEnergy, Integer>, JpaSpecificationExecutor<EFSAEnergy> {

}
