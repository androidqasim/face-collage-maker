package dauroi.photoeditor.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

public class DateTimeUtils {

    private static final String DATE_TIME_FORMAT_GMT = "yyyy-MM-dd HH:mm:ss 'GMT'";
    public static final String DATE_TIME_FORMAT_STANDARDIZED_UTC = "yyyy-MM-dd HH:mm:ss";
    public static final String DATE_FORMAT_STANDARDIZED_UTC = "yyyy-MM-dd";
    private static final String DATE_FORMAT_MM_DD_YYYY = "MM/dd/yyyy";
    private static final String DATE_FORMAT_DD_MM_YYYY = "dd/MM/yyyy";

    public static String getCurrentDateTimeGMT() {
        SimpleDateFormat dateFormatGmt = new SimpleDateFormat(DATE_TIME_FORMAT_GMT, Locale.US);
        dateFormatGmt.setTimeZone(TimeZone.getTimeZone("GMT"));
        return dateFormatGmt.format(new Date());
    }

    public static String getCurrentDateTime() {
        return new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC).format(new Date());
    }

    public static String getCurrentDate() {
        return new SimpleDateFormat(DATE_FORMAT_STANDARDIZED_UTC).format(new Date());
    }

    public static Date convertGMTString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_GMT);
        try {
            return sdf.parse(dateString);
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return null;
    }

    public static Date convertUTCString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC);
        try {
            return sdf.parse(dateString);
        } catch (ParseException pe) {
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC);
            try {
                return sdf.parse(dateString);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    public static boolean isGMTString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_GMT);
        try {
            sdf.parse(dateString);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    public static boolean isUTCString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC);
        try {
            sdf.parse(dateString);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    public static boolean isUTCDateString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_STANDARDIZED_UTC);
        try {
            sdf.parse(dateString);
            return true;
        } catch (ParseException pe) {
            return false;
        }
    }

    public static String toUTCDateString(String dateString) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_DD_MM_YYYY);
        try {
            Date date = sdf.parse(dateString);
            sdf = new SimpleDateFormat(DATE_FORMAT_STANDARDIZED_UTC);
            return sdf.format(date);
        } catch (ParseException pe) {
            sdf = new SimpleDateFormat(DATE_FORMAT_MM_DD_YYYY);
            try {
                Date date = sdf.parse(dateString);
                sdf = new SimpleDateFormat(DATE_FORMAT_STANDARDIZED_UTC);
                return sdf.format(date);
            } catch (ParseException pe2) {
                return getCurrentDate();
            }
        }
    }

    /*
     * 28 Nov 2012 03:08:45 GMT -> 2012-11-28 03:08:45
     */
    public static String toStandardizedUTCFromGMT(String dateStringInGMT) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_GMT);
        try {
            Date date = sdf.parse(dateStringInGMT);
            sdf = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC);
            return sdf.format(date);
        } catch (ParseException e) {
            // e.printStackTrace();
            return null;
        }
    }

    public static int compare(String backupDbDateTimeStr, String localDbDateTimeStr) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC);
        Date backupDbDate = null;
        try {
            backupDbDate = sdf.parse(backupDbDateTimeStr);
        } catch (ParseException pe) {
            throw new Exception("backupDbDateTimeStr '" + backupDbDateTimeStr + "' Is NOT In the UTC Format '"
                    + DATE_TIME_FORMAT_STANDARDIZED_UTC + "'");
        }
        Date localDbDate = null;
        try {
            localDbDate = sdf.parse(localDbDateTimeStr);
        } catch (ParseException pe) {
            throw new Exception("localDbDateTimeStr '" + localDbDateTimeStr + "' Is NOT In the UTC Format '"
                    + DATE_TIME_FORMAT_STANDARDIZED_UTC + "'");
        }
        return backupDbDate.compareTo(localDbDate);
    }

    public static Date dateTimeStringInUtcFormat2Date(String dateTimeStringInUtcFormat) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC);
        return sdf.parse(dateTimeStringInUtcFormat);
    }

    public static String getCurrentLocalDateTime() {
        String mydate = null;
        try {
            mydate = DateFormat.getDateTimeInstance().format(Calendar.getInstance().getTime());
        } catch (Exception e) {
        }
        return mydate;
    }

    public static String millis2LocalDateString(long dateTimeMillis) {
        return DateFormat.getDateInstance().format(new Date(dateTimeMillis));
    }

    public static String toUTCDateTimeString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_TIME_FORMAT_STANDARDIZED_UTC);
        return sdf.format(date);
    }

}
