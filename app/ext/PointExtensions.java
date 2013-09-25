package ext;

import org.apache.commons.lang.StringUtils;

import play.templates.JavaExtensions;

public class PointExtensions extends JavaExtensions {

    public static String round(String point) {
        if(StringUtils.isNotEmpty(point)) {
            return String.valueOf((int)(double)Double.valueOf(point));
        }
        return "0";
    }
    
}
