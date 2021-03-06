package hu.elte.chaleur.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter

@Entity
public class Consumption {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;
    private LocalDate date;
    private Double amount;
    @OneToOne(fetch = FetchType.LAZY)
    private RecipeUnit recipeUnit;
    @OneToOne(fetch = FetchType.LAZY)
    private Recipe recipe;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutrientValue> nutrientValues;
}
