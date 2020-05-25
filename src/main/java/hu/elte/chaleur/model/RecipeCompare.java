package hu.elte.chaleur.model;

import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@ToString(callSuper = true)
public class RecipeCompare{
    Integer recipeId;
    Double negativeChange;
    Double positiveChange;
}
