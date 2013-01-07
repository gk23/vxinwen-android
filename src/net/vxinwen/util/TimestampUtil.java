package net.vxinwen.util;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;

public class TimestampUtil {
    public static String timeStampToString(Timestamp ts) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return sdf.format(ts);
    }

    public static Timestamp stringToTimeStamp(String s) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        try {
            return new Timestamp(sdf.parse(s).getTime());
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }
}
