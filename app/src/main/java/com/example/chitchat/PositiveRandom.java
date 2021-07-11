package com.example.chitchat;

//import android.support.v7.widget.ActivityChooserView.ActivityChooserViewAdapter;
import java.util.Random;

public class PositiveRandom
{
    //Taken from Decompiled Source

    public static final String TAG = "acarcione.PositiveRandom";
    private static Random r;

    public static int nextInt(int bound) {
        if (r == null) {
            r = new Random();
        }
        if (bound < 0)
        {
            return -1;
        }
        else
        {
            int base = r.nextInt(bound);
            return base;// & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
        }
    }

    public static int nextInt(int min, int max) {
        if (r == null) {
            r = new Random();
        }
        return (r.nextInt(max - min) + min);// & ActivityChooserViewAdapter.MAX_ACTIVITY_COUNT_UNLIMITED;
    }
}
