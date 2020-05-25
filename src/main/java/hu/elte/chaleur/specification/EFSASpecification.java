package hu.elte.chaleur.specification;

import hu.elte.chaleur.efsa.EFSAEnergy;
import hu.elte.chaleur.efsa.EFSAMineralVitamin;
import org.springframework.data.jpa.domain.Specification;

public class EFSASpecification {
    public static Specification<EFSAMineralVitamin> findByAgeAndGender(Integer age, String gender){
        return (root, query, criteriaBuilder) -> criteriaBuilder
                .and((criteriaBuilder.lessThanOrEqualTo(root.get("ageFrom"), age)),
                        (criteriaBuilder.greaterThanOrEqualTo(root.get("ageTo"), age)),
                        criteriaBuilder.equal(root.get("gender"), gender));
    }

    public static Specification<EFSAEnergy> findByAgeAndGenderAndPal(Integer age, String gender, String pal){
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.lessThanOrEqualTo(root.get("ageFrom"), age),
                criteriaBuilder.greaterThanOrEqualTo(root.get("ageTo"), age),
                criteriaBuilder.equal(root.get("gender"), gender),
                criteriaBuilder.equal(root.get("pal"), pal)
        );
    }
}
