package com.fravier.travel.utilities;

import com.fravier.travel.textdrawable.TextDrawable;
import com.fravier.travel.textdrawable.util.ColorGenerator;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by francis on 07/07/2016.
 */
public class Utilities {
    public static String getDateCurrentTimeZone(long timestamp) {
        try {
            Calendar calendar = Calendar.getInstance();
            TimeZone tz = TimeZone.getDefault();
            calendar.setTimeInMillis(timestamp * 1000);
            calendar.add(Calendar.MILLISECOND, tz.getOffset(calendar.getTimeInMillis()));
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date currenTimeZone = (Date) calendar.getTime();
            return sdf.format(currenTimeZone);
        } catch (Exception e) {
        }
        return "";
    }

    public static TextDrawable generateNameImage(String fullname) {
        ColorGenerator generator = ColorGenerator.DEFAULT;
        int color = generator.getRandomColor();

        TextDrawable.IBuilder builder = TextDrawable.builder()
                .beginConfig()
                .withBorder(1)
                .endConfig()
                .rect();
        try {
            String firstname = fullname.split(" ")[0];
            String secondname = fullname.split(" ")[1];

            char letter1 = firstname.toUpperCase().charAt(0);
            char letter2 = secondname.toUpperCase().charAt(0);

            TextDrawable drawable = builder.build(letter1 + "" + letter2, color);
            return drawable;
        } catch (Exception ex) {
            try {
                char letter1 = fullname.toUpperCase().charAt(0);
                TextDrawable drawable = builder.build(letter1 + "", color);
                return drawable;
            }catch(Exception ex2){
                TextDrawable drawable = builder.build("X", color);
                return drawable;
            }
        }
    }

}
