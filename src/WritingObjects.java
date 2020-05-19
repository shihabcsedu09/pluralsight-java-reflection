import model.Person;
import orm.EntityManager;

import java.sql.SQLException;

public class WritingObjects {

    public static void main(String[] args) throws SQLException, IllegalAccessException {
        EntityManager<Person> entityManager = EntityManager.of(Person.class);

        Person linda = new Person("linda", 31);
        Person james = new Person("james", 24);
        Person susan = new Person("susan", 34);
        Person john = new Person("john", 33);

        entityManager.persist(linda);
        entityManager.persist(james);
        entityManager.persist(susan);
        entityManager.persist(john);
    }
}
