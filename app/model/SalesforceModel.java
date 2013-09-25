package model;

import helper.Salesforce;

import java.util.List;

import org.hibernate.annotations.common.reflection.ReflectionUtil;

public class SalesforceModel  {
    
    public static void save(Object entity) {
        Salesforce.save(entity).execute();
    }
    
    public static void delete(Object entity) {
        Salesforce.delete(entity);
    }
    
    public static <T> List<T> findAll(Class<T> clazz) {
        return (List<T>) Salesforce.find(clazz).getList();
    }
}
