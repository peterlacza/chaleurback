package hu.elte.chaleur.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(callSuper = true)

@Entity
public class Ingredient {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private Double amount;
    @OneToOne(fetch = FetchType.LAZY)
    private Unit unit;
    @OneToOne(fetch = FetchType.LAZY)
    private Food food;
    @ManyToOne
    @NotFound(action = NotFoundAction.IGNORE)
    @JsonIgnore
    private Recipe recipe;
    @JsonIgnore
    private Integer mass;
    @JsonIgnore
    private Integer evaporationRemainder;
}