package hu.elte.chaleur.specification;

import hu.elte.chaleur.model.DailyDifferent;
import hu.elte.chaleur.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class DailyDifferentSpecification {
    public static Specification<DailyDifferent> findLastAvarageByUser(User user){
        LocalDate today = LocalDate.now();
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.equal(root.get("date"), today),
                criteriaBuilder.equal(root.get("user"), user),
                criteriaBuilder.equal(root.get("average"), Boolean.TRUE)
                );
    }

    public static Specification<DailyDifferent> findAll(User user){
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("user"), user);
    }

    public static Specification<DailyDifferent> findAllExceptAverage(User user){
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                    criteriaBuilder.equal(root.get("user"), user),
                    criteriaBuilder.equal(root.get("average"), Boolean.FALSE)
        );
    }

    public static Specification<DailyDifferent> getLastNDays(Integer days){
        LocalDate lastDays = LocalDate.now().minusDays(days);
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return (root, query, criteriaBuilder) -> criteriaBuilder.and(
                criteriaBuilder.between(root.get("date"), lastDays, yesterday),
                criteriaBuilder.equal(root.get("average"), Boolean.FALSE)
        );
    }
}
