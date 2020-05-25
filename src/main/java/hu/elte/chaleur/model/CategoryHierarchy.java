package hu.elte.chaleur.model;

import lombok.*;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(callSuper = true)

@Entity
public class CategoryHierarchy {
    @Id
    @GeneratedValue(strategy= GenerationType.IDENTITY)
    private Integer id;

    private String mainCategoryCode;
    private String mainCategoryDescription;
    private String subCategoryCode;
    private String subCategoryDescription;
}