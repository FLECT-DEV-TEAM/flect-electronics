package ext;

import play.templates.JavaExtensions;

public class HtmlExtensions extends JavaExtensions {
    
    public static String tobr(String html) {
        html = html.replaceAll("<br>", "");
        return html.replaceAll("\n", "<br />");
    }

}
