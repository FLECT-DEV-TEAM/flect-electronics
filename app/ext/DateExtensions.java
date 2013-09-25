package ext;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.time.DateUtils;

import play.templates.JavaExtensions;

public class DateExtensions extends JavaExtensions  {
    
    private static final Pattern datePattern;
    private static final Pattern dateTimePattern;
    private static final DateFormat df;
    
    static {
        datePattern = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}");
        dateTimePattern = Pattern.compile("\\d{4}-\\d{1,2}-\\d{1,2}T\\d{2}:\\d{2}");
        df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    }

    public static String localeJa(String date) {
        Matcher matcher = dateTimePattern.matcher(date);
        if(matcher.find()) {
            date = matcher.group(0);
            date = date.replaceAll("T", " ");
            Date jaDate = new Date();
            try {
                jaDate = df.parse(date);
                jaDate = DateUtils.addHours(jaDate, 9);
            } catch (ParseException e) {
                return date;
            }
            return df.format(jaDate).replaceAll(" ", "T");
        } else {
            return date;
        }
    }
    
    public static String formatDate(String date) {
        Matcher matcher = datePattern.matcher(date);
        if(matcher.find()) {
            date = matcher.group(0);
            date = date.replaceAll("-", "/");
        }
        return date;
    }
    
    public static String formatDateTime(String date) {
        Matcher matcher = dateTimePattern.matcher(date);
        if(matcher.find()) {
            date = matcher.group(0);
            date = date.replaceAll("-", "/").replaceAll("T", " ");
        }
        return date;
    }
}
