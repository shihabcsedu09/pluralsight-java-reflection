package util;

import annotation.Column;
import annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MetaModel<T> {

    private Class<T> clss;

    public static <T> MetaModel<T> of(Class<T> clss) {
       return new MetaModel<T>(clss);
    }

    public MetaModel(Class<T> clss) {

        this.clss = clss;
    }

    public PrimaryKeyField getPrimaryKey() {
        Field[] declaredFields = clss.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            PrimaryKey primaryKey = declaredField.getAnnotation(PrimaryKey.class);
            if(primaryKey!= null) {
                PrimaryKeyField primaryKeyField = new PrimaryKeyField(declaredField);
                return primaryKeyField;
            }
        }

        throw new IllegalArgumentException("No Primary Key found for the class - " + clss.getSimpleName());
    }

    public List<ColumnField> getColumns() {
        List<ColumnField> columnFields = new ArrayList<ColumnField>();
        Field[] declaredFields = clss.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            Column columnKey = declaredField.getAnnotation(Column.class);
            if(columnKey!= null) {
                ColumnField columnField = new ColumnField(declaredField);
                columnFields.add(columnField);
            }
        }
        return columnFields;
    }
}
