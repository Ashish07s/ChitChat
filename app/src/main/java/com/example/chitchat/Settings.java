package com.example.chitchat;

import android.content.Context;
import android.content.SharedPreferences;

import static android.content.Context.MODE_PRIVATE;

public class Settings
{
    private static Settings instance;
    private static Purchases.ElementColor fontColor;
    private static Purchases.ElementColor bgColor;

    private Settings() {}

    public static Settings getInstance()
    {
        if (instance == null)
        {
            instance = new Settings();
        }

        return instance;
    }

    public void setCurrentColor(Purchases.ItemType type, Purchases.ElementColor color, Context context)
    {
        SharedPreferences savedData = context.getSharedPreferences("Files", MODE_PRIVATE);
        SharedPreferences.Editor editor = savedData.edit();

        if (type.equals(Purchases.ItemType.Font))
        {
            fontColor = color;
            editor.putString("CurrentFont", color.toString());
        }
        else  if (type.equals(Purchases.ItemType.Background))
        {
            bgColor = color;
            editor.putString("CurrentBG", color.toString());
        }

        editor.apply();
    }

    public Purchases.ElementColor getFontColor(Context context)
    {
        if (fontColor != null)
        {
            return fontColor;
        }

        //Get current color from the saved data
        else
        {
            SharedPreferences savedData = context.getSharedPreferences("Files", MODE_PRIVATE);
            String colorString = savedData.getString("CurrentFont", null);
            if (colorString == null)
            {
                fontColor = Purchases.ElementColor.Black;
            }
            else
            {
                for (Purchases.ElementColor color : Purchases.ElementColor.values())
                {
                    if (color.toString().equals(colorString))
                    {
                        fontColor = color;
                    }
                }
            }

            return fontColor;
        }
    }

    public Purchases.ElementColor getBackgroundColor(Context context)
    {
        if (bgColor != null)
        {
            return bgColor;
        }

        //Get current color from the saved data
        else
        {
            SharedPreferences savedData = context.getSharedPreferences("Files", MODE_PRIVATE);
            String colorString = savedData.getString("CurrentBG", null);
            if (colorString == null)
            {
                bgColor = Purchases.ElementColor.Black;
            }
            else
            {
                for (Purchases.ElementColor color : Purchases.ElementColor.values())
                {
                    if (color.toString().equals(colorString))
                    {
                        bgColor = color;
                    }
                }
            }

            return bgColor;
        }
    }
}
