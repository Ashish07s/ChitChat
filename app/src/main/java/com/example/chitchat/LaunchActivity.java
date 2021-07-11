/*
.
.
.
.
.
============== We will be using James's free gift for this project. ==============
.
.
.
.
.
 */
package com.example.chitchat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputFilter.LengthFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

//Taken from decompiled source
public class LaunchActivity extends AppCompatActivity 
{
    private static final String TAG = "LaunchActivity";
    private Context ctx;
    private EditText endET;
    private SharedPreferences prefs;
    private EditText startET;
    private WordGraph wordGraph;
    private int wordLength = 4;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);
        this.ctx = getApplicationContext();
        this.startET = (EditText) findViewById(R.id.et_start);
        this.endET = (EditText) findViewById(R.id.et_end);
        this.prefs = getSharedPreferences(BuildConfig.APPLICATION_ID, 0);
        this.startET.addTextChangedListener(new textWatcher());
        if (savedInstanceState == null) {
            genGame(null);
        }

        // --unique code start--
        showSpinner();
        Purchases.getInstance().readPurchasesFromSaveData(this);
        Coins.getInstance().readCoinsFromSavedData(this);
        // -- unique code end --
    }

    /* renamed from: edu.fandm.enovak.wordly.Launch$1 */
    class textWatcher implements TextWatcher {
        textWatcher() {
        }

        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        public void afterTextChanged(Editable s) {
            int len = s.length();
            LaunchActivity.this.endET.setFilters(new InputFilter[]{new LengthFilter(len)});
            if (s.length() < LaunchActivity.this.endET.getText().length()) {
                LaunchActivity.this.endET.setText(LaunchActivity.this.endET.getText().subSequence(0, s.length()));
            }
        }
    }

    class FindSolutionTask extends AsyncTask<String, Void, ArrayList<String>> {
        private String end;
        private String start;
        private View tv_loading;

        FindSolutionTask() {
        }

        protected void onPreExecute() {
            this.tv_loading = LaunchActivity.this.findViewById(R.id.tv_loading);
            this.tv_loading.setVisibility(View.VISIBLE);
            this.tv_loading.setAnimation(AnimationUtils.loadAnimation(LaunchActivity.this.ctx, R.anim.blink));
            this.tv_loading.animate();
        }

        protected ArrayList<String> doInBackground(String[] params)
        {
            this.start = params[0];
            this.end = params[1];
            Log.d(TAG, "FindSolutionTask - doInBackground: Building the graph");
            WordGraph wg = LaunchActivity.this.buildGraph(this.start.length());
            HashMap<String, String> map = new HashMap();
            HashMap<String, Boolean> marked = new HashMap();
            LinkedList<String> q = new LinkedList();
            String cur = this.start;
            q.add(cur);
            while (!cur.equals(this.end)) {
                if (q.isEmpty()) {
                    return null;
                }
                cur = (String) q.remove();
                marked.put(cur, Boolean.valueOf(true));
                ArrayList<String> neighbors = wg.getNeighbors(cur);
                for (int i = 0; i < neighbors.size(); i++) {
                    if (!map.containsKey((String) neighbors.get(i))) {
                        map.put(neighbors.get(i), cur);
                        q.add(neighbors.get(i));
                    }
                }
            }
            ArrayList<String> solution = new ArrayList(2);
            for (String cur2 = this.end; !cur2.equals(this.start); cur2 = (String) map.get(cur2)) {
                solution.add(0, cur2);
            }
            solution.add(0, this.start);
            return solution;
        }

        protected void onPostExecute(ArrayList<String> solution) {
            this.tv_loading.clearAnimation();
            this.tv_loading.setVisibility(View.INVISIBLE);
            if (solution == null) {
                StringBuilder tmp = new StringBuilder();
                tmp.append("no possible solution from '");
                tmp.append(this.start);
                tmp.append("' to '");
                tmp.append(this.end);
                tmp.append("'");
                Toast.makeText(LaunchActivity.this.ctx, tmp.toString(), Toast.LENGTH_SHORT).show();
                return;
            }
            Intent i = new Intent(LaunchActivity.this.ctx, GameActivity.class);
            i.putExtra("start_word", this.start);
            i.putExtra("end_word", this.end);
            i.putStringArrayListExtra("solution", solution);
            LaunchActivity.this.startActivity(i);
        }
    }

    class GenPuzzleTask extends AsyncTask<Integer, Void, String[]> {
        private View tv;

        GenPuzzleTask() {
        }

        protected void onPreExecute() {
            this.tv = LaunchActivity.this.findViewById(R.id.tv_loading);
            this.tv.setVisibility(View.VISIBLE);
            this.tv.setAnimation(AnimationUtils.loadAnimation(LaunchActivity.this.ctx, R.anim.blink));
            this.tv.animate();
        }

        private String genSequence(String start, int SEQ_LEN) throws IllegalStateException
        {
            HashMap<String, Boolean> marked = new HashMap();
            marked.put(start, Boolean.valueOf(true));
            int hops = 0;
            LinkedList<String> curRound = new LinkedList();
            curRound.push(start);
            LinkedList<String> nextRound = new LinkedList();
            while (hops < SEQ_LEN)
            {
                Iterator it = curRound.iterator();
                while (it.hasNext())
                {
                    Iterator it2 = LaunchActivity.this.wordGraph.getNeighbors((String) it.next()).iterator();
                    while (it2.hasNext())
                    {
                        String n = (String) it2.next();
                        if (!marked.containsKey(n))
                        {
                            marked.put(n, Boolean.valueOf(true));
                            nextRound.add(n);
                        }
                    }
                }
                if (nextRound.size() != 0)
                {
                    curRound = nextRound;
                    nextRound = new LinkedList();
                    hops++;
                }
                else
                {
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append("Cannot find a sequence of len: ");
                    stringBuilder.append(SEQ_LEN);
                    throw new IllegalStateException(stringBuilder.toString());
                }
            }
            return (String) curRound.get(PositiveRandom.nextInt(curRound.size()));
        }

        protected String[] doInBackground(Integer[] params)
        {
            Log.d(TAG, "GenPuzzleTask - doInBackground: Building the graph");
            WordGraph wg = LaunchActivity.this.buildGraph(params[0]);
            String[] startAndEnd = new String[2];

            while (startAndEnd[1] == null)
            {
                startAndEnd[0] = wg.getRandomWord();
                try
                {
                    startAndEnd[1] = genSequence(startAndEnd[0], 3);
                }
                catch (IllegalStateException ise)
                {
                    //Log.d(LaunchActivity.TAG, "GenPuzzleTask - doInBackground: Illegal state:" + ise.getMessage());
                    String message = "Giving up on start word: " + startAndEnd[0];
                    //Log.d(TAG, "GenPuzzleTask - doInBackground:" + message);
                    startAndEnd[1] = null;
                }
            }
            return startAndEnd;
        }

        protected void onPostExecute(String[] result) {
            this.tv.clearAnimation();
            this.tv.setVisibility(View.INVISIBLE);
            String s = result[0];  //result[null] was original line
            String e = result[1];
            LaunchActivity.this.startET.setText(s);
            LaunchActivity.this.endET.setText(e);
        }
    }

    // --unique code start--
    public void showSpinner()
    {
        Spinner spinner = findViewById(R.id.spinner_word_length_launch);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.word_length_array, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(R.layout.drop_down_item);
        // Apply the adapter to the spinner
        spinner.setAdapter(adapter);
        spinner.setSelection(2);
        spinner.setOnItemSelectedListener(spinnerListener);
    }

    private AdapterView.OnItemSelectedListener spinnerListener = new AdapterView.OnItemSelectedListener()
    {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int pos, long id)
        {
            wordLength = Integer.valueOf((String) parent.getItemAtPosition(pos));
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent)
        {
            // Do nothing
        }
    };

    public void openStore(View v)
    {
        Intent storeIntent = new Intent(this.ctx, StoreActivity.class);
        startActivity(storeIntent);
    }

    public void openSettings(View v)
    {
        Intent settingsIntent = new Intent(this.ctx, SettingsActivity.class);
        startActivity(settingsIntent);
    }
    // -- unique code end --

    public void startGame(View v)
    {
        String s = this.startET.getText().toString();
        String e = this.endET.getText().toString();
        if (s.length() == this.endET.getText().toString().length()) {
            if (s.length() != 0) {
                new FindSolutionTask().execute(new String[]{s, e});
                return;
            }
        }

        Toast.makeText(this, "Starting word and ending word must be the same length!", Toast.LENGTH_SHORT).show();
    }

    public void genGame(View v)
    {
        new GenPuzzleTask().execute(new Integer[]{Integer.valueOf(wordLength)});
    }

    protected void onResume()
    {
        super.onResume();
        findViewById(R.id.tv_loading).setVisibility(View.INVISIBLE);
        if (this.prefs.getBoolean("firstrun", true)) {
            this.prefs.edit().putBoolean("firstrun", false).commit();
            startActivity(new Intent(this.ctx, ExplainActivity.class));
        }
    }

    private WordGraph buildGraph(int len) 
    {
        if (this.wordGraph != null && this.wordGraph.getRandomWord().length() == len) 
        {
            Log.d(TAG, "buildGraph: Graph already exists");
            return this.wordGraph;
        }
        this.wordGraph = new WordGraph();
        InputStream inputStream = null;
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        Log.d(TAG, "buildGraph: Init area");
        try 
        {
            Log.d(TAG, "buildGraph: Inside try block");
            inputStream = getResources().openRawResource(R.raw.words_unix);
            streamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(streamReader);
            String line;
            while (true) 
            {
                String readLine = bufferedReader.readLine();
                line = readLine;
                if (readLine == null) {
                    break;
                }
                line = line.replace("\n", "").toLowerCase();
                if (line.length() == len && line.matches("[a-zA-Z]+")) {
                    this.wordGraph.addWord(line);
                }
            }
            try 
            {
                streamReader.close();
                if (inputStream != null)
                {
                    inputStream.close();
                }
                bufferedReader.close();
            } 
            catch (IOException ioe) 
            {
                ioe.printStackTrace();
            }
        } 
        catch (IOException ioe2) 
        {
            ioe2.printStackTrace();
        }
        
        return this.wordGraph;
    }

    public void storeActivity(View view){
        Intent intent = new Intent(this, StoreActivity.class);
        startActivity(intent);
    }
}
