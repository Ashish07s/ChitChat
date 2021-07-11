package com.example.chitchat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity
{

    Purchases purchases;
    Settings settings;
    RadioButton defaultFontButton;
    RadioButton defaultBgButton;
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        purchases = Purchases.getInstance();
        settings = Settings.getInstance();
        defaultFontButton = findViewById(R.id.radioButton_font_black_settings);
        defaultBgButton = findViewById(R.id.radioButton_bg_white_settings);
        setDefaultToggles();
    }

    public void onRadioButtonClicked(View view)
    {
        // Is the button now checked?
        RadioButton radioButton = (RadioButton) view;
        boolean checked = radioButton.isChecked();

        // Check which radio button was clicked
        switch(view.getId())
        {
            case R.id.radioButton_font_yellow_settings:
                if (checked)
                {
                    //Check if not unlocked
                    if (!purchases.isUnlocked(Purchases.ItemType.Font, Purchases.ElementColor.Yellow))
                    {
                        //Switch selection to default
                        radioButton.toggle();
                        defaultFontButton.toggle();
                        Toast.makeText(this, "You haven't unlocked that yet. Checkout the store.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        settings.setCurrentColor(Purchases.ItemType.Font, Purchases.ElementColor.Yellow, this);
                    }
                }
                break;
            case R.id.radioButton_font_green_settings:
                if (checked)
                {
                    //Check if not unlocked
                    if (!purchases.isUnlocked(Purchases.ItemType.Font, Purchases.ElementColor.Green))
                    {
                        radioButton.toggle();
                        defaultFontButton.toggle();
                        Toast.makeText(this, "You haven't unlocked that yet. Checkout the store.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        settings.setCurrentColor(Purchases.ItemType.Font, Purchases.ElementColor.Green, this);
                    }
                }
                break;
            case R.id.radioButton_font_red_settings:
                if (checked)
                {
                    //Check if not unlocked
                    if (!purchases.isUnlocked(Purchases.ItemType.Font, Purchases.ElementColor.Red))
                    {
                        radioButton.toggle();
                        defaultFontButton.toggle();
                        Toast.makeText(this, "You haven't unlocked that yet. Checkout the store.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        settings.setCurrentColor(Purchases.ItemType.Font, Purchases.ElementColor.Red, this);
                    }
                }
                break;
            case R.id.radioButton_font_black_settings:
                settings.setCurrentColor(Purchases.ItemType.Font, Purchases.ElementColor.Black, this);
                break;
            case R.id.radioButton_bg_red_settings:
                if (checked)
                {
                    //Check if not unlocked
                    if (!purchases.isUnlocked(Purchases.ItemType.Background, Purchases.ElementColor.Red))
                    {
                        radioButton.toggle();
                        defaultBgButton.toggle();
                        Toast.makeText(this, "You haven't unlocked that yet. Checkout the store.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        settings.setCurrentColor(Purchases.ItemType.Background, Purchases.ElementColor.Red, this);
                    }
                }
                break;
            case R.id.radioButton_bg_yellow_settings:
                if (checked)
                {
                    //Check if not unlocked
                    if (!purchases.isUnlocked(Purchases.ItemType.Background, Purchases.ElementColor.Yellow))
                    {
                        radioButton.toggle();
                        defaultBgButton.toggle();
                        Toast.makeText(this, "You haven't unlocked that yet. Checkout the store.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        settings.setCurrentColor(Purchases.ItemType.Background, Purchases.ElementColor.Yellow, this);
                    }
                }
                break;
            case R.id.radioButton_bg_green_settings:
                if (checked)
                {
                    //Check if not unlocked
                    if (!purchases.isUnlocked(Purchases.ItemType.Background, Purchases.ElementColor.Green))
                    {
                        radioButton.toggle();
                        defaultBgButton.toggle();
                        Toast.makeText(this, "You haven't unlocked that yet. Checkout the store.", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        settings.setCurrentColor(Purchases.ItemType.Background, Purchases.ElementColor.Green, this);
                    }
                }
                break;
            case R.id.radioButton_bg_white_settings:
                if (checked)
                {
                    settings.setCurrentColor(Purchases.ItemType.Background, Purchases.ElementColor.White, this);
                }
                break;
        }
    }

    private void setDefaultToggles()
    {
        Purchases.ElementColor currentBGColor = settings.getBackgroundColor(this);
        RadioButton selectedButton;
        switch (currentBGColor)
        {
            case Red:
                selectedButton = findViewById(R.id.radioButton_bg_red_settings);
                selectedButton.toggle();
                break;
            case Yellow:
                selectedButton = findViewById(R.id.radioButton_bg_yellow_settings);
                selectedButton.toggle();
                break;
            case Green:
                selectedButton = findViewById(R.id.radioButton_bg_green_settings);
                selectedButton.toggle();
                break;
            default:
            case White:
                selectedButton = findViewById(R.id.radioButton_bg_white_settings);
                selectedButton.toggle();
                break;
        }


        Purchases.ElementColor currentFontColor = settings.getFontColor(this);
        switch (currentFontColor)
        {
            case Yellow:
                selectedButton = findViewById(R.id.radioButton_font_yellow_settings);
                selectedButton.toggle();
                break;
            case Green:
                selectedButton = findViewById(R.id.radioButton_font_green_settings);
                selectedButton.toggle();
                break;
            case Red:
                selectedButton = findViewById(R.id.radioButton_font_red_settings);
                selectedButton.toggle();
                break;
            default:
            case Black:
                selectedButton = findViewById(R.id.radioButton_font_black_settings);
                selectedButton.toggle();
                break;
        }
    }
}
