package hu.elte.chaleur.specification;

import hu.elte.chaleur.model.User;
import org.springframework.data.jpa.domain.Specification;

public class UserSpecification {
    public static Specification<User> findMyFollowers(User user){
        return (root, query, criteriaBuilder) -> {
            if(user == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.joinList("following"), user);
        };
    }

    public static Specification<User> findMyFollowing(User user){
        return (root, query, criteriaBuilder) -> {
            if(user == null){
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.joinList("followers"), user);
        };
    }



}
