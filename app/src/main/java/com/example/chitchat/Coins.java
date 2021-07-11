package com.example.chitchat;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import static android.content.Context.MODE_PRIVATE;

public class Coins
{
    private final static String TAG = "Coins";
    public static int playerCoinCount = 0;

    private static Coins instance;

    private Coins() {}

    public static Coins getInstance()
    {
        if (instance == null)
        {
            instance = new Coins();
        }

        return instance;
    }

    public void readCoinsFromSavedData(Context context)
    {
        SharedPreferences saveData = context.getSharedPreferences("Files", MODE_PRIVATE);
        playerCoinCount = saveData.getInt("Coins", 0);
    }

    public void addCoin(int r, Context context)
    {

        if(r < 0)
        {
            Log.e(TAG, "Can't add negative coins");
        }
        else
        {
            playerCoinCount = playerCoinCount + r;
            updateSavedData(context);
        }

    }

    public void subtractCoin(int r, Context context)
    {
        if(r < 0)
        {
            Log.e(TAG, "Can't subtract negative coins");
        }
        else
        {
            playerCoinCount = playerCoinCount - r;
            updateSavedData(context);
        }

    }

    private void updateSavedData(Context context)
    {
        SharedPreferences saveData = context.getSharedPreferences("Files", MODE_PRIVATE);
        SharedPreferences.Editor editor = saveData.edit();
        editor.putInt("Coins", playerCoinCount);
        editor.apply();
    }

    public int getCoins(){
        return playerCoinCount;
    }



}
