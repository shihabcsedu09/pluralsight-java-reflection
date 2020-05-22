package reflection;

import model.Person;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.MethodType;

public class PlayWithMethodHandles {
    public static void main(String[] args) throws Throwable {
        Lookup lookup = MethodHandles.lookup();
        MethodType emptyConstructorMethodType = MethodType.methodType(void.class);

        MethodHandle emptyConstructor = lookup.findConstructor(Person.class, emptyConstructorMethodType);
        Person p = (Person) emptyConstructor.invoke();

        MethodType constructorMethodType = MethodType.methodType(void.class, String.class, int.class);
        MethodHandle constructor = lookup.findConstructor(Person.class, constructorMethodType);

        Object james = (Person) constructor.invoke("James", 20);
        System.out.println(james);

        MethodType nameGetterMethodType = MethodType.methodType(String.class);
        MethodHandle nameGetter = lookup.findVirtual(Person.class, "getName", nameGetterMethodType);
        System.out.println(nameGetter.invoke(james));

        MethodType nameSetterMethodType = MethodType.methodType(void.class, String.class);
        MethodHandle nameSetter = lookup.findVirtual(Person.class, "setName", nameSetterMethodType);
        nameSetter.invoke(james, "linda");
        System.out.println(james);

        Lookup privateLookup = MethodHandles.privateLookupIn(Person.class, lookup);
        MethodHandle nameReader = privateLookup .findGetter(Person.class, "name", String.class);
        System.out.println(nameReader.invoke(james));


    }
}
