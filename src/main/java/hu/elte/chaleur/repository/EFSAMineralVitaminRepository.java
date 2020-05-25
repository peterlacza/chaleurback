package hu.elte.chaleur.repository;

import hu.elte.chaleur.efsa.EFSAMineralVitamin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EFSAMineralVitaminRepository extends JpaRepository<EFSAMineralVitamin, Integer>, JpaSpecificationExecutor<EFSAMineralVitamin> {
}
