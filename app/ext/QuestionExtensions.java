package ext;

import play.templates.JavaExtensions;

public class QuestionExtensions extends JavaExtensions {
    
    public static String shorten(String title, int point) {
        if(title != null && title.length() > point) {
            title = title.substring(0, point) + "...";
        }
        return title;
    }

}
