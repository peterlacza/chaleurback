package hu.elte.chaleur.model.metamodel;

import hu.elte.chaleur.model.Recipe;

import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;
import java.time.LocalDateTime;

@StaticMetamodel(Recipe.class)
public class Recipe_ {
    public static volatile SingularAttribute<Recipe, Integer> id;
    public static volatile SingularAttribute<Recipe, LocalDateTime> createdAt;
}
