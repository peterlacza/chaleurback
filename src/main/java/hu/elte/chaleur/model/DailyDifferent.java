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
@ToString(callSuper = true)

@Entity
public class DailyDifferent {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private User user;
    private LocalDate date;
    private Boolean average;

    @OneToMany(mappedBy = "dailyDifferent", fetch = FetchType.LAZY)
    private List<DailyDifferentNutrient> dailyDifferentNutrients;
}
