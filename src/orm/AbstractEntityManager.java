package orm;

import util.ColumnField;
import util.MetaModel;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicLong;

public abstract class AbstractEntityManager<T> implements EntityManager<T> {
    private AtomicLong idGenerator = new AtomicLong(0L);

    @Override
    public void persist(T t) throws SQLException, IllegalAccessException {
        MetaModel metaModel = MetaModel.of(t.getClass());
        String sql = metaModel.buildInsertRequest();
        try (PreparedStatement statement = prepareStatementWith(sql).andParameters(t);) {
            statement.executeUpdate();
        }
    }


    @Override
    public T find(Class<T> clss, Object primaryKey) throws SQLException, InvocationTargetException, NoSuchMethodException, InstantiationException, IllegalAccessException {
        MetaModel metaModel = MetaModel.of(clss);
        String sql = metaModel.buildSelectRequest();
        try (PreparedStatement statement = prepareStatementWith(sql).andPrimaryKey(primaryKey);
             ResultSet resultSet = statement.executeQuery();) {
            return buildInstanceFrom(clss, resultSet);
        }
    }

    private T buildInstanceFrom(Class<T> clss, ResultSet resultSet) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException, SQLException {
        MetaModel metaModel = MetaModel.of(clss);
        T t = clss.getConstructor().newInstance();
        Field primaryKeyField = metaModel.getPrimaryKey().getField();
        String primaryKeyFieldName = metaModel.getPrimaryKey().getName();
        Class<?> primaryKeyType = primaryKeyField.getType();

        resultSet.next();
        if (primaryKeyType == long.class) {
            long primaryKeyValue = resultSet.getInt(primaryKeyFieldName);
            primaryKeyField.setAccessible(true);
            primaryKeyField.set(t, primaryKeyValue);
        }

        for (ColumnField columnField : metaModel.getColumns()) {
            Field field = columnField.getField();
            field.setAccessible(true);
            String columnType = columnField.getType();
            String columnName = columnField.getName();
            if (columnType.equals("int")) {
                int value = resultSet.getInt(columnName);
                field.set(t, value);
            } else if (columnType.equals("String")) {
                String value = resultSet.getString(columnName);
                field.set(t, value);
            }
        }
        return t;
    }

    private PreparedStatementWrapper prepareStatementWith(String sql) throws SQLException {
        Connection connection = buildConnection();
        PreparedStatement statement = connection.prepareStatement(sql);
        return new PreparedStatementWrapper(statement);
    }

    public abstract Connection buildConnection() throws SQLException;

    private class PreparedStatementWrapper {

        private final PreparedStatement statement;

        public PreparedStatementWrapper(PreparedStatement statement) {
            this.statement = statement;
        }

        public PreparedStatement andParameters(T t) throws SQLException, IllegalAccessException {
            MetaModel metaModel = MetaModel.of(t.getClass());
            String primaryKeyType = metaModel.getPrimaryKey().getType();

            if (primaryKeyType.equals("long")) {
                final long id = idGenerator.incrementAndGet();
                statement.setLong(1, id);
                Field primaryKey = metaModel.getPrimaryKey().getField();
                primaryKey.setAccessible(true);
                primaryKey.set(t, id);
            }

            for (int columnIndex = 0; columnIndex < metaModel.getColumns().size(); columnIndex++) {
                ColumnField columnField = metaModel.getColumns().get(columnIndex);
                String columnFieldType = columnField.getType();

                Field field = columnField.getField();
                field.setAccessible(true);

                Object value = field.get(t);
                if (columnFieldType.equals("int")) {
                    statement.setInt(columnIndex + 2, (Integer) value);
                } else if (columnFieldType.equals("String")) {
                    statement.setString(columnIndex + 2, (String) value);
                }

            }
            return statement;
        }

        public PreparedStatement andPrimaryKey(Object primaryKey) throws SQLException {
            if (primaryKey.getClass() == Long.class) {
                statement.setLong(1, (Long) primaryKey);
            }
            return statement;
        }
    }
}
