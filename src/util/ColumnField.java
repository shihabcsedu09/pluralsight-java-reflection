package util;

import annotation.Column;

import java.lang.reflect.Field;

public class ColumnField {
    private Field columnField;

    private Column column;

    public ColumnField(Field columnField) {
        this.columnField = columnField;
        this.column = this.columnField.getAnnotation(Column.class);
    }

    public String getName() {
        return column.name()    ;
    }

    public String getType() {
        return columnField.getType().getSimpleName();
    }

    public Field getField() {
        return this.columnField;
    }
}
