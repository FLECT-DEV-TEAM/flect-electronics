package util;

import java.lang.reflect.Field;

import exception.PlayRuntimeException;

public class ReflectionUtil {
    
    private ReflectionUtil(){}
    
    public static Object newInstance(Class<?> clazz) {
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            throw new PlayRuntimeException(e);
        }
    }
    
    public static void setField(Field f, Object obj, Object value) {
        try {
            f.set(obj, value);
        } catch (Exception e) {
            throw new PlayRuntimeException(e);
        }
    }
    
    public static Field getField(Class clazz, String fieldName) {
        try {
            return clazz.getField(fieldName);
        } catch (Exception e) {
            throw new PlayRuntimeException(e);
        }
    }
    
    public static Object getFieldValue(Object obj, Field f) {
        try {
            return f.get(obj);
        } catch (Exception e) {
            throw new PlayRuntimeException(e);
        }
    }
 
}
