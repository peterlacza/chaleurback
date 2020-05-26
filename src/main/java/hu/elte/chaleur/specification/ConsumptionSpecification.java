package hu.elte.chaleur.specification;

import hu.elte.chaleur.model.Consumption;
import hu.elte.chaleur.model.User;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;

public class ConsumptionSpecification {
    public static Specification<Consumption> findByUser(User user){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("user"), user);
    }
    public static Specification<Consumption> findYesterdayConsumption(){
        LocalDate yesterday = LocalDate.now().minusDays(1);
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), yesterday);
    }

    public static Specification<Consumption> findTodayConsumption(){
        LocalDate today = LocalDate.now();
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), today);
    }

    public static Specification<Consumption> findConsumptionByDate(LocalDate actDate){
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("date"), actDate);
    }

    public static Specification<Consumption> getLastDays(Integer dayNumber){
        LocalDate today = LocalDate.now();
        LocalDate daysBefore = LocalDate.now().minusDays(dayNumber);
        return (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("date"), daysBefore, today);
    }
}
