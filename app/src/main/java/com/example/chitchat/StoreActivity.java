package com.example.chitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class StoreActivity extends AppCompatActivity
{
    private final int FONT_COST = 10;
    private final int BACKGROUND_COST = 20;
    private final int REVEAL_COST = 50;
    private Purchases purchases;
    private Coins coins;
    private TextView coinsCount;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store);
        purchases = Purchases.getInstance();
        coins = Coins.getInstance();
        coinsCount = findViewById(R.id.textView_coins_count_store);
        updateCoinValue(coins.getCoins());
    }

    private void updateCoinValue(int newCoinsVal)
    {
        coinsCount.setText(": " + String.valueOf(newCoinsVal));
    }

    public void onInstantRevealBuyClick(View view)
    {
        if (coins.getCoins() >= REVEAL_COST)
        {
            coins.subtractCoin(REVEAL_COST, this);
            purchases.purchaseWordReveal(this);
            updateCoinValue(coins.getCoins());
        }
        else
        {
            Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onYellowFontBuyClick(View view)
    {
        if (coins.getCoins() >= FONT_COST)
        {
            coins.subtractCoin(FONT_COST, this);
            purchases.purchaseColor(Purchases.ElementColor.Yellow, Purchases.ItemType.Font, this);
            updateCoinValue(coins.getCoins());
        }
        else
        {
            Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show();
        }
    }
    public void onRedFontBuyClick(View view)
    {
        if (coins.getCoins() >= FONT_COST)
        {
            coins.subtractCoin(FONT_COST, this);
            purchases.purchaseColor(Purchases.ElementColor.Red, Purchases.ItemType.Font, this);
            updateCoinValue(coins.getCoins());
        }
        else
        {
            Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show();
        }
    }
    public void onGreenFontBuyClick(View view)
    {
        if (coins.getCoins() >= FONT_COST)
        {
            coins.subtractCoin(FONT_COST, this);
            purchases.purchaseColor(Purchases.ElementColor.Green, Purchases.ItemType.Font, this);
            updateCoinValue(coins.getCoins());
        }
        else
        {
            Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onYellowBackgroundBuyClick(View view)
    {
        if (coins.getCoins() >= BACKGROUND_COST)
        {
            coins.subtractCoin(BACKGROUND_COST, this);
            purchases.purchaseColor(Purchases.ElementColor.Yellow, Purchases.ItemType.Background, this);
            updateCoinValue(coins.getCoins());
        }
        else
        {
            Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onRedBackgroundBuyClick(View view)
    {
        if (coins.getCoins() >= BACKGROUND_COST)
        {
            coins.subtractCoin(BACKGROUND_COST, this);
            purchases.purchaseColor(Purchases.ElementColor.Red, Purchases.ItemType.Background, this);
            updateCoinValue(coins.getCoins());
        }
        else
        {
            Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onGreenBackgroundBuyClick(View view)
    {
        if (coins.getCoins() >= BACKGROUND_COST)
        {
            coins.subtractCoin(BACKGROUND_COST, this);
            purchases.purchaseColor(Purchases.ElementColor.Green, Purchases.ItemType.Background, this);
            updateCoinValue(coins.getCoins());
        }
        else
        {
            Toast.makeText(this, "Insufficient funds!", Toast.LENGTH_SHORT).show();
        }
    }

    public void onFreeCoins(View view)
    {
        coins.addCoin(100, this);
        updateCoinValue(coins.getCoins());
    }
}