package hu.elte.chaleur.datastore;

import hu.elte.chaleur.model.Unit;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter

@Entity
public class EFSAMineralVitamin {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String category;
    private String nutrient;
    private String code;
    private String population;
    private Integer ageFrom;
    private Integer ageTo;
    private String gender;
    private Double ai;
    private Double ar;
    private Double pri;
    private Double ri;
    private Double ul;
    private Double adequate;
    @OneToOne(fetch = FetchType.LAZY)
    private Unit unit;
}
