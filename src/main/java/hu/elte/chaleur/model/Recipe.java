package hu.elte.chaleur.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(callSuper = true)

@Entity
public class Recipe {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @Column
    @CreationTimestamp
    private LocalDateTime createdAt;

    private String name;
    private String description;
    @OneToOne(fetch = FetchType.LAZY)
    private Image picture;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"roles", "recipes", "consumptions", "referenceValues", "dailyDifferents"})
    private User owner;
    private Integer madeNumber;
    private Boolean isCategoryWinner;
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    @JsonIgnoreProperties({"recipe"})
    private List<Category> categories;
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    private List<Ingredient> ingredients;
    @OneToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<NutrientValue> nutrientValues;
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    private List<Rating> ratings;
    @OneToMany(mappedBy = "recipe", fetch = FetchType.LAZY)
    private List<RecipeUnit> recipeUnits;
}
