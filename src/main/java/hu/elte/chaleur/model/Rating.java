package hu.elte.chaleur.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(callSuper = true)

@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Recipe recipe;

    @OneToOne(fetch = FetchType.LAZY)
    @JsonIgnoreProperties(value = {"consumptions", "dailyDifferents", "password", "recipes", "referenceValues", "roles", "favourites" }, allowSetters = true)
    private User user;

    @Min(1) @Max(5)
    private Integer rate;
}
