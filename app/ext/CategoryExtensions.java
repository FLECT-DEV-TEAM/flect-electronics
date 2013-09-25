package ext;

import model.Product2.Family;
import model.Question;
import model.Question.Community;
import play.templates.JavaExtensions;

public class CategoryExtensions extends JavaExtensions {
    
    public static String camelize(String community) {
        return community.substring(0, 1).toUpperCase() + community.substring(1);
    }
    
    public static String convertJpLabel(String community) {
        for(Family family : Family.values()) {
            if(family.getEnName().equals(community)) {
                return family.getName();
            }
        }
        return community;
    }
    
    public static String getNameById(String id) {
        return Question.Community.getNameById(id);
    }
}
