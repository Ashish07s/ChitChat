package com.example.chitchat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import android.support.v7.app.AlertDialog.Builder;
import android.widget.Toast;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

//Taken from decompiled source
public class GameActivity extends AppCompatActivity {
    public static final String TAG = "GameActivity";

    private View gameBackground;
    private Bitmap[] bitmaps = new Bitmap[3];
    private Context ctx;
    protected ImageView hintIV;
    private Thread isst;
    protected final int numImages = 3;
    private GridView puzzle;
    private ArrayList<String> shown;
    private ArrayList<String> soln;
    private List<Integer> solutionLength = new ArrayList<>();
    private TextView revealCount;
    private Button useRevealButton;
    private Purchases purchases;
    private Settings settings;
    private Button hintButton;
    private View background;

    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); //was (5)
        this.gameBackground = findViewById(R.id.all);
        this.hintIV = (ImageView) findViewById(R.id.game_iv_hint);
        this.ctx = getApplicationContext();
        this.gameBackground.setOnSystemUiVisibilityChangeListener(new Game1());
        Intent launchIntent = getIntent();
        String start = launchIntent.getStringExtra("start_word");
        String str = TAG;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(start);
        stringBuilder.append("\n");
        Log.d(str, stringBuilder.toString());
        str = launchIntent.getStringExtra("end_word");
        String str2 = TAG;
        StringBuilder stringBuilder2 = new StringBuilder();
        stringBuilder2.append(str);
        stringBuilder2.append("\n");
        Log.d(str2, stringBuilder2.toString());
        this.soln = launchIntent.getStringArrayListExtra("solution");

        for (String solution : soln)
        {
            solutionLength.add(solution.length());
        }

        str2 = TAG;
        stringBuilder2 = new StringBuilder();
        stringBuilder2.append("solution: ");
        stringBuilder2.append(this.soln);
        stringBuilder2.append("\n");
        Log.d(str2, stringBuilder2.toString());
        this.shown = new ArrayList(this.soln.size());
        while (this.shown.size() < this.soln.size())
        {
            this.shown.add("    ");
        }
        this.shown.set(0, this.soln.get(0));
        this.shown.set(this.soln.size() - 1, this.soln.get(this.soln.size() - 1));
        this.puzzle = findViewById(R.id.game_words_gv);
        this.puzzle.setNumColumns(this.shown.size());
        this.puzzle.setAdapter(new ArrayAdapter<>(this, R.layout.cell, shown.toArray(new String[shown.size()])));

        // -- unique code --
        purchases = Purchases.getInstance();
        revealCount = findViewById(R.id.textView_revealsLeft_game);
        useRevealButton = findViewById(R.id.button_useReveal_game);
        hintButton = findViewById(R.id.game_butt_hint);
        updateRevealCount();
        settings = Settings.getInstance();

        final Handler handler = new Handler();
        setBackgroundColor();
        //Wait until the grid is fully initialized, then update colors
        final Runnable r = new Runnable()
        {
            public void run()
            {
                if (puzzle.getChildAt(0) == null)
                {
                    handler.postDelayed(this, 1000);
                }
                else
                {
                    setFontColor();
                }
            }
        };

        handler.postDelayed(r, 1);
        // -- end unique code --
    }

    // -- unique code --
    private void updateRevealCount()
    {
        revealCount.setText("Reveals: " + String.valueOf(purchases.getNumWordReveals()));
    }

    public void onRevealButtonClicked(View view)
    {
        if (purchases.getNumWordReveals() > 0)
        {
            //do the thing
            purchases.useWordReveal(this);
            updateRevealCount();
            int emptyIndex = getCurrentEmptySpot();
            String solution = soln.get(emptyIndex);
            shown.set(emptyIndex, solution);
            TextView emptySpotTextView = (TextView) puzzle.getChildAt(emptyIndex);
            emptySpotTextView.setText(solution);
            if (shown.equals(soln))
            {
                winTheGame();
            }
        }
        else
        {
            Toast.makeText(this, "Insufficient reveals", Toast.LENGTH_SHORT).show();
        }
    }

    private View.OnClickListener winListener = new View.OnClickListener()
    {
        public void onClick(View v)
        {
            GameActivity.this.finish();
        }
    };

    private void setFontColor()
    {
        Purchases.ElementColor selectedColor = settings.getFontColor(this);
        if (selectedColor.equals(Purchases.ElementColor.Black))
        {
            for (int i = 0; i < puzzle.getChildCount(); i++)
            {
                TextView text = (TextView) puzzle.getChildAt(i);
                if (text != null)
                {
                    text.setTextColor(getResources().getColor(R.color.colorBlack));
                }
            }
            hintButton.setTextColor(getResources().getColor(R.color.colorBlack));
            useRevealButton.setTextColor(getResources().getColor(R.color.colorBlack));
        }
        else if(selectedColor.equals(Purchases.ElementColor.Green))
        {
            for (int i = 0; i < puzzle.getChildCount(); i++)
            {
                TextView text = (TextView) puzzle.getChildAt(i);
                if (text != null)
                {
                    text.setTextColor(getResources().getColor(R.color.colorGreen));
                }
            }
            hintButton.setTextColor(getResources().getColor(R.color.colorGreen));
            useRevealButton.setTextColor(getResources().getColor(R.color.colorGreen));
        }
        else if (selectedColor.equals(Purchases.ElementColor.Yellow))
        {
            for (int i = 0; i < puzzle.getChildCount(); i++)
            {
                TextView text = (TextView) puzzle.getChildAt(i);
                if (text != null)
                {
                    text.setTextColor(getResources().getColor(R.color.colorYellow));
                }
            }

            hintButton.setTextColor(getResources().getColor(R.color.colorYellow));
            useRevealButton.setTextColor(getResources().getColor(R.color.colorYellow));
        }
        else if (selectedColor.equals(Purchases.ElementColor.Red))
        {
            for (int i = 0; i < puzzle.getChildCount(); i++)
            {
                TextView text = (TextView) puzzle.getChildAt(i);
                if (text != null)
                {
                    text.setTextColor(getResources().getColor(R.color.colorRed));
                }
            }

            hintButton.setTextColor(getResources().getColor(R.color.colorRed));
            useRevealButton.setTextColor(getResources().getColor(R.color.colorRed));
        }
    }

    private void setBackgroundColor()
    {
        Purchases.ElementColor selectedColor = settings.getBackgroundColor(this);
        if (selectedColor.equals(Purchases.ElementColor.White))
        {
            gameBackground.setBackgroundColor(getResources().getColor(R.color.colorLight));
        }
        else if (selectedColor.equals(Purchases.ElementColor.Yellow))
        {
            gameBackground.setBackgroundColor(getResources().getColor(R.color.colorYellow));
        }
        else if (selectedColor.equals(Purchases.ElementColor.Green))
        {
            gameBackground.setBackgroundColor(getResources().getColor(R.color.colorGreen));
        }
        else if (selectedColor.equals(Purchases.ElementColor.Red))
        {
            gameBackground.setBackgroundColor(getResources().getColor(R.color.colorRed));
        }
    }
    // -- end unique code --

    /* Was c03021 */
    class Game1 implements View.OnSystemUiVisibilityChangeListener {

        /* was c03011 */
        class Game2 implements Runnable {
            Game2() {
            }

            public void run() {
                GameActivity.this.hide(null);
            }
        }

        Game1() {
        }

        public void onSystemUiVisibilityChange(int visibility) {
            if ((visibility & 4) == 0) {
                new Handler().postDelayed(new Game2(), 2500);
            }
        }
    }

    /* Was C03053 */
    class Game3 implements DialogInterface.OnClickListener {
        Game3() {
        }

        public void onClick(DialogInterface dialog, int which) {
            dialog.cancel();
        }
    }

    class ImageDownloader extends AsyncTask<String, Void, Void> {
        ImageDownloader() {
        }

        protected void onPreExecute() {
            GameActivity.this.endSlideShow();
        }

        private void downloadImageFromURL(String URL, int idx) {
            try {
                InputStream in = new BufferedInputStream(new URL(URL).openStream());
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                byte[] buf = new byte[1024];
                while (true) {
                    int read = in.read(buf);
                    int n = read;
                    if (-1 != read) {
                        out.write(buf, 0, n);
                    } else {
                        out.close();
                        in.close();
                        byte[] response = out.toByteArray();
                        GameActivity.this.bitmaps[idx] = BitmapFactory.decodeByteArray(response, 0, response.length);
                        return;
                    }
                }
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }

        protected Void doInBackground(String... params) {
            BufferedReader reader = null;
            int i = 0;
            String keyword = params[0];
            String str = GameActivity.TAG;
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append("Downloading image for: ");
            stringBuilder.append(keyword);
            Log.d(str, stringBuilder.toString());
            try {
                String readLine;
                StringBuilder stringBuilder2 = new StringBuilder();
                stringBuilder2.append("https://pixabay.com/api/?key=11798986-90e51c47bdc042dae3106b141&q=");
                stringBuilder2.append(keyword);
                HttpURLConnection con = (HttpURLConnection) new URL(stringBuilder2.toString()).openConnection();
                con.setRequestMethod("GET");
                con.setDoOutput(true);
                con.connect();
                reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String line = null;
                while (true) {
                    readLine = reader.readLine();
                    line = readLine;
                    if (readLine == null) {
                        break;
                    }
                    StringBuilder stringBuilder3 = new StringBuilder();
                    stringBuilder3.append(line);
                    stringBuilder3.append("\n");
                    sb.append(stringBuilder3.toString());
                }
                readLine = null;
                try
                {
                    JSONArray arr = new JSONObject(sb.toString()).getJSONArray("hits");
                    if (arr.length() == 0)
                    {
                        String str2 = GameActivity.TAG;
                        StringBuilder stringBuilder4 = new StringBuilder();
                        stringBuilder4.append("No images for: ");
                        stringBuilder4.append(keyword);
                        Log.d(str2, stringBuilder4.toString());
                        GameActivity.this.endSlideShow();
                        reader.close();
                        /* Was:
                        try {
                            reader.close();
                        } catch (int i2) {
                            i2.printStackTrace();
                        }
                         */
                        return null;
                    }

                    while (i < 3)
                    { //was i2, which was defined in above try/catch
                        downloadImageFromURL(arr.getJSONObject(i).getString("webformatURL"), i); //both were also i2
                        i++; //i2
                    }

                    try
                    {
                        reader.close();
                    }
                    catch (IOException ioe)
                    {
                        ioe.printStackTrace();
                    }

                    Log.d(GameActivity.TAG, "done downloading images.");
                    return null;
                } catch (JSONException je) {
                    je.printStackTrace();
                }
            } catch (MalformedURLException mue) {
                mue.printStackTrace();
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch(IOException e) {
                        Log.e(TAG, "IO Exception");
                    }
                }
            } catch (IOException ioe2) {
                ioe2.printStackTrace();
                if (reader != null) {
                    try {
                        reader.close();
                    }
                    catch(IOException e) {
                        Log.e(TAG, "IO Exception");
                    }
                }
            } catch (Throwable th) {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException ioe3) {
                        ioe3.printStackTrace();
                    }
                }
            }
            return null;
        }
        protected void onPostExecute(Void v) {
            GameActivity.this.isst = new ImageSlideShowThread();
            GameActivity.this.isst.start();
        }
    }

    class ImageSlideShowThread extends Thread {
        private final long SLEEP_TIME_MS = 8000;
        private int cur = 0;

        /* Was C03061 */
        class GameSlideShow implements Runnable
        {
            GameSlideShow() { }

            public void run()
            {
                ImageSlideShowThread.this.animatedImageSwitch();
            }
        }

        ImageSlideShowThread() { }

        public void run()
        {
            while (true)
            {
                GameActivity.this.runOnUiThread(new GameSlideShow());
                this.cur = (this.cur + 1) % 3;
                try
                {
                    Thread.sleep(8000);
                }
                catch (InterruptedException e)
                {
                    return;
                }
            }
        }

        private void animatedImageSwitch() {
            Animation anim_out = AnimationUtils.loadAnimation(GameActivity.this.ctx, R.anim.fade_out);//Was random numbers instead of R.anim.blink
            final Animation anim_in = AnimationUtils.loadAnimation(GameActivity.this.ctx, R.anim.fade_in);//Was random numbers
            anim_out.setDuration(1500);
            anim_out.setAnimationListener(new Animation.AnimationListener()
            {

                /* Was C03071 */
                class GameSlideShow2 implements Animation.AnimationListener
                {
                    GameSlideShow2()
                    {
                    }

                    public void onAnimationStart(Animation animation)
                    {
                        GameActivity.this.hintIV.setVisibility(View.VISIBLE);
                    }

                    public void onAnimationRepeat(Animation animation)
                    {
                    }

                    public void onAnimationEnd(Animation animation) {
                    }
                }

                public void onAnimationStart(Animation animation) {
                }

                public void onAnimationRepeat(Animation animation) {
                }

                public void onAnimationEnd(Animation animation) {
                    GameActivity.this.hintIV.setImageBitmap(GameActivity.this.bitmaps[ImageSlideShowThread.this.cur]);
                    anim_in.setAnimationListener(new GameSlideShow2());
                    GameActivity.this.hintIV.startAnimation(anim_in);
                }
            });
            GameActivity.this.hintIV.startAnimation(anim_out);
        }
    }

    private int getCurrentEmptySpot()
    {
        int idx = -1;
        String text = "blahblahblah";
        while (idx < this.puzzle.getChildCount() && !text.equals("    "))
        {
            idx++;
            text = ((TextView) this.puzzle.getChildAt(idx)).getText().toString();
        }

        return idx;
    }

    private void winTheGame()
    {
        Log.d(GameActivity.TAG, "Player has won game");
        Toast.makeText(GameActivity.this.ctx, "Correct!", Toast.LENGTH_SHORT).show();
        GameActivity.this.endSlideShow();
        ((Button) GameActivity.this.findViewById(R.id.game_butt_hint)).setVisibility(View.INVISIBLE);
        GameActivity.this.hintIV.setVisibility(View.VISIBLE);
        useRevealButton.setVisibility(View.GONE);
        GameActivity.this.hintIV.setImageResource(R.drawable.star);
        GameActivity.this.hintIV.setBackground(null);
        GameActivity.this.hintIV.setAnimation(AnimationUtils.loadAnimation(GameActivity.this.ctx, R.anim.spin));
        GameActivity.this.hintIV.animate();
        GameActivity.this.hintIV.setOnClickListener(winListener);
    }

    public void guess(View v)
    {
        final TextView tv = (TextView) v;
        final int curEmptySpot = getCurrentEmptySpot();
        Builder builder = new Builder(this);
        builder.setTitle("Guess a word");
        final EditText input = new EditText(this);
        input.setInputType(1);
        builder.setView(input);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which)
            {
                int curCorrectLength = solutionLength.get(curEmptySpot);
                String guess = input.getText().toString();
                if (guess.length() != curCorrectLength)
                {
                    Toast.makeText(GameActivity.this.ctx, "That word is not " + curCorrectLength + " letters long!", Toast.LENGTH_SHORT).show();
                }
                else if (!WordGraph.oneLetterDiff((String) GameActivity.this.soln.get(curEmptySpot - 1), guess))
                {
                    Context access$000 = GameActivity.this.ctx;
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("That word is not one letter different from ");
                    stringBuilder.append((String) GameActivity.this.soln.get(curEmptySpot - 1));
                    stringBuilder.append("!");
                    Toast.makeText(access$000, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
                } else if (guess.equals(GameActivity.this.soln.get(curEmptySpot)))
                {
                    GameActivity.this.shown.set(curEmptySpot, guess);
                    tv.setText(guess);
                    String str = GameActivity.TAG;
                    StringBuilder stringBuilder2 = new StringBuilder();
                    stringBuilder2.append("shown: ");
                    stringBuilder2.append(GameActivity.this.shown);
                    stringBuilder2.append("   soln: ");
                    stringBuilder2.append(GameActivity.this.soln);
                    Log.d(str, stringBuilder2.toString());
                    if (GameActivity.this.shown.equals(GameActivity.this.soln)) {
                        winTheGame();
                        return;
                    }
                    new ImageDownloader().execute(new String[]{(String) GameActivity.this.soln.get(curEmptySpot + 1)});
                } else {
                    Toast.makeText(GameActivity.this.ctx, "That's not the word I'm thinking of!", Toast.LENGTH_SHORT).show();
                }
            }
        });
        builder.setNegativeButton("Cancel", new Game3());
        builder.show();
    }

    protected void onResume()
    {
        super.onResume();
        hide(null);
        new ImageDownloader().execute(new String[]{(String) this.soln.get(1)});
    }

    public void hint(View v)
    {
        int idx = getCurrentEmptySpot();
        String answer = (String) this.soln.get(idx);
        int i = 0;
        while (((String) this.soln.get(idx - 1)).charAt(i) == answer.charAt(i)) {
            i++;
        }
        char newLetter = answer.charAt(i);
        Context context = this.ctx;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(BuildConfig.FLAVOR);
        stringBuilder.append(newLetter);
        Toast.makeText(context, stringBuilder.toString(), Toast.LENGTH_SHORT).show();
    }

    public void hide(View v)
    {
        this.gameBackground.setSystemUiVisibility(2822);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null)
        {
            getSupportActionBar().hide();
        }
    }

    private void endSlideShow() {
        if (this.isst != null) {
            this.isst.interrupt();
            this.isst = null;
        }
    }
}
