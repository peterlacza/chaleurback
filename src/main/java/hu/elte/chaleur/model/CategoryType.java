package hu.elte.chaleur.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

public enum CategoryType {

    ETKEZESTIPUS("ETKEZESTIPUS"),

    DIETA("DIET"),

    ALKALOM("ALKALOM"),

    SUBCATEGORY("SUBCATEGORY"),

    MAINCATEGORY("MAINCATEGORY"),

    ORSZAG("ORSZAG");

    private String value;

    CategoryType(String value) {
        this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
        return String.valueOf(value);
    }

    @JsonCreator
    public static CategoryType fromValue(String text) {
        for (CategoryType b : CategoryType.values()) {
            if (String.valueOf(b.value).equals(text)) {
                return b;
            }
        }
        throw new IllegalArgumentException("Unexpected value '" + text + "'");
    }
}
