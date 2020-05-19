package util;

import annotation.PrimaryKey;

import java.lang.reflect.Field;

public class PrimaryKeyField {
    private Field field;

    private PrimaryKey primaryKey;

    public PrimaryKeyField(Field field) {
        this.field = field;
        this.primaryKey = this.field.getAnnotation(PrimaryKey.class);
    }

    public String getName() {
        return primaryKey.name();
    }

    public String getType() {
        return field.getType().getSimpleName();
    }

    public Field getField() {
        return this.field;
    }
}
