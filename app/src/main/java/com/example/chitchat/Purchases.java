package com.example.chitchat;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

import static android.content.Context.MODE_PRIVATE;

public class Purchases
{
    //Keep Track of mtx user has bought; "Next-Word Reveals", background color options, font color options
    //Save info to local file

    //On first run, class should read the file and set values to an appropriate amount.

    private static Purchases instance;
    public static final String TAG = "Purchases";
    public int numWordReveals = 0;
    HashMap<ElementColor, Boolean> unlockedFontColors = new HashMap<ElementColor, Boolean>();
    HashMap<ElementColor, Boolean> unlockedBackgroundColors = new HashMap<ElementColor, Boolean>();

    private Purchases() {}

    public static Purchases getInstance()
    {
        if (instance == null)
        {
            instance = new Purchases();
        }

        return instance;
    }

    enum ElementColor
    {
        Yellow,
        Green,
        Red,
        Black,
        White
    }

    enum ItemType
    {
        Background,
        Font
    }

    //Set value of background/font color key in hashmap to true upon purchase
    public void purchaseColor(ElementColor color, ItemType type, Context ct)
    {

        SharedPreferences SaveData = ct.getSharedPreferences("Files", MODE_PRIVATE);
        SharedPreferences.Editor editor = SaveData.edit();


        if (type.equals(ItemType.Background)){
            //Set background color
            unlockedBackgroundColors.put(color, true);
            editor.putBoolean("BACKGROUND " + color.toString(), unlockedBackgroundColors.get(color));
        }
        if (type.equals(ItemType.Font)){
            //Set font color
            unlockedFontColors.put(color, true);
            editor.putBoolean("FONT " + color.toString(), unlockedFontColors.get(color));
        }

        editor.apply();
    }

    public void readPurchasesFromSaveData(Context ct)
    {
        SharedPreferences saveData = ct.getSharedPreferences("Files", MODE_PRIVATE);

        for (ElementColor color : ElementColor.values())
        {
            //Update backgrounds
            String key = "BACKGROUND " + color.toString();
            Boolean val = saveData.getBoolean(key, false);
            unlockedBackgroundColors.put(color, val);

            //Update fonts
            key = "FONT " + color.toString();
            val = saveData.getBoolean(key, false);
            unlockedFontColors.put(color, val);
        }

        //Reveals
        numWordReveals = saveData.getInt("numWordReveals", 0);
    }

    //Decrement numWordReveals upon use
    public void useWordReveal(Context ct)
    {
        if (numWordReveals <= 0)
        {
            throw new IllegalStateException("Don't have any word reveals to use");
        }
        numWordReveals--;
        updateRevealCount(ct);
    }

    //Increment numWordReveals upon purchase
    public void purchaseWordReveal(Context ct)
    {
        numWordReveals++;
        updateRevealCount(ct);
    }

    public int getNumWordReveals()
    {
        return numWordReveals;
    }

    public boolean isUnlocked(ItemType type, ElementColor color)
    {
        switch (type)
        {
            case Font:
                return unlockedFontColors.get(color);
            case Background:
                return unlockedBackgroundColors.get(color);
        }

        return false;
    }
    private void updateRevealCount(Context context)
    {
        SharedPreferences SaveData = context.getSharedPreferences("Files", MODE_PRIVATE);
        SharedPreferences.Editor editor = SaveData.edit();
        editor.putInt("numWordReveals", numWordReveals);
        editor.apply();
    }
}
