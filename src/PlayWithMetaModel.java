import model.Person;
import util.ColumnField;
import util.MetaModel;
import util.PrimaryKeyField;

import java.util.List;

public class PlayWithMetaModel {
     public static void main(String[] args) {
         MetaModel metaModel = MetaModel.of(Person.class);

         PrimaryKeyField primaryKeyField = metaModel.getPrimaryKey();
         List<ColumnField> columnFields = metaModel.getColumns();

         System.out.println("Primary Key Name = " + primaryKeyField.getName() +
                 ", type = " + primaryKeyField.getType());

         for (ColumnField columnField : columnFields) {
             System.out.println("Column Key Name = " + columnField.getName() +
                     ", type = " + columnField.getType());
         }


     }
}
