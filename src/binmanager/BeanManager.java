package binmanager;

import annotation.Inject;
import annotation.Provides;
import provider.H2ConnectionProvider;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

public class BeanManager {
    private static BeanManager instance = new BeanManager();

    private Map<Class<?>, Supplier> registery = new HashMap<>();

    private BeanManager() {
        List<Class<?>> classes = List.of(H2ConnectionProvider.class);

        for (Class<?> aClass : classes) {

            Method[] declaredMethods = aClass.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                Provides annotation = declaredMethod.getAnnotation(Provides.class);
                if (annotation != null) {
                    Class<?> returnType = declaredMethod.getReturnType();
                    Supplier<?> supplier = () -> {
                        try {
                            if (!Modifier.isStatic(declaredMethod.getModifiers())) {
                                Object obj = aClass.getConstructor().newInstance();
                                return declaredMethod.invoke(obj);
                            }
                            return declaredMethod.invoke(null);
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                    };
                    registery.put(returnType, supplier);
                }
            }

        }


    }

    public static BeanManager getInstance() {
        return instance;
    }

    public <T> T getInstance(Class<T> clss) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
        T t = clss.getConstructor().newInstance();
        Field[] declaredFields = t.getClass().getDeclaredFields();

        for (Field declaredField : declaredFields) {
            Inject annotation = declaredField.getAnnotation(Inject.class);
            if (annotation != null) {
                Class<?> type = declaredField.getType();
                Supplier<?> supplier = registery.get(type);
                Object object = supplier.get();
                declaredField.setAccessible(true);
                declaredField.set(t, object);
            }
        }

        return t;
    }
}
