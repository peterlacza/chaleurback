package hu.elte.chaleur.efsa;

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
public class EFSAEnergy {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;
    private String nutrient;
    private String code;
    private String population;
    private Integer ageFrom;
    private Integer ageTo;
    private String pal;
    private String gender;
    private Float ar;
    @OneToOne(fetch = FetchType.LAZY)
    private Unit unit;
}