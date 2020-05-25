package hu.elte.chaleur.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(callSuper = true)

@Entity
public class DailyDifferentNutrient {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JsonIgnore
    private DailyDifferent dailyDifferent;

    @OneToOne
    private Nutrient nutrient;
    private Double different;
}
