package util;

import annotation.Column;
import annotation.PrimaryKey;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class MetaModel {

    private Class<?> clss;

    public MetaModel(Class clss) {
        this.clss = clss;
    }

    public static <T> MetaModel of(Class<T> clss) {
        return new MetaModel(clss);
    }

    public PrimaryKeyField getPrimaryKey() {
        Field[] declaredFields = clss.getDeclaredFields();
        for (Field declaredField : declaredFields) {
            PrimaryKey primaryKey = declaredField.getAnnotation(PrimaryKey.class);
            if (primaryKey != null) {
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
            if (columnKey != null) {
                ColumnField columnField = new ColumnField(declaredField);
                columnFields.add(columnField);
            }
        }
        return columnFields;
    }

    public String buildInsertRequest() {
        // insert into Person(id, name, age) values(?, ?, ?)
        String columnElement = buildColumnNames();

        String questionMarkElements = buildQuestionMarksElements();

        return "insert into " + this.clss.getSimpleName() +
                " ( " + columnElement + ") values ( " + questionMarkElements + ")";
    }

    public String buildSelectRequest() {
        String columnElement = buildColumnNames();
        return "select " + columnElement + " from " + this.clss.getSimpleName() +
                " where " + getPrimaryKey().getName() + "= ?";

    }


    private String buildQuestionMarksElements() {
        int numberOfColumns = getColumns().size() + 1;
        return IntStream.range(0, numberOfColumns)
                .mapToObj(index -> "?")
                .collect(Collectors.joining(", "));
    }

    private String buildColumnNames() {
        String primaryKeyColumn = getPrimaryKey().getName();
        List<String> columnNames =
                getColumns().stream().map(ColumnField::getName).collect(Collectors.toList());
        columnNames.add(0, primaryKeyColumn);

        return String.join(", ", columnNames);
    }
}


