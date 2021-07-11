package com.example.chitchat;


import android.util.Log;

import java.util.ArrayList;
import java.util.Iterator;

//Taken from Decompiled Sources
class WordGraph
{
    public static final String TAG = "WordGraph";
    private ArrayList<ArrayList<Boolean>> edges = new ArrayList(32);
    private ArrayList<WordGraphNode> nodes = new ArrayList(16);

    class WordGraphNode
    {
        String word;

        public WordGraphNode(String w)
        {
            this.word = w;
        }

        public String toString() {
            return this.word;
        }
    }

    public boolean isWord(String word) {
        Iterator it = this.nodes.iterator();
        while (it.hasNext()) {
            if (((WordGraphNode) it.next()).equals(word)) {
                return true;
            }
        }
        return false;
    }

    public void addWord(String word) {
        this.nodes.add(new WordGraphNode(word));
        int index = this.nodes.size() - 1;
        ArrayList<Boolean> newList = new ArrayList(this.nodes.size());
        int i = 0;
        for (int i2 = 0; i2 < this.edges.size(); i2++) {
            ((ArrayList) this.edges.get(i2)).add(Boolean.valueOf(false));
            newList.add(Boolean.valueOf(false));
        }
        newList.add(Boolean.valueOf(false));
        this.edges.add(newList);
        while (i < this.nodes.size() - 1) {
            String w2 = ((WordGraphNode) this.nodes.get(i)).word;
            if (oneLetterDiff(word, w2)) {
                addEdge(word, w2);
                addEdge(w2, word);
            }
            i++;
        }
    }

    public static boolean oneLetterDiff(String w1, String w2) {
        if (w1.length() == w2.length()) {
            boolean z = false;
            int count = 0;
            for (int i = 0; i < w1.length(); i++) {
                if (w1.charAt(i) != w2.charAt(i)) {
                    count++;
                }
                if (count > 1) {
                    return false;
                }
            }
            if (count == 1) {
                z = true;
            }
            return z;
        }
        throw new IllegalArgumentException("Different length words!!");
    }

    private void addEdge(String w1, String w2) {
        int idx1 = getNodeIDX(w1);
        int idx2 = getNodeIDX(w2);
        if (idx1 != -1 && idx2 != -1) {
            ((ArrayList) this.edges.get(idx1)).set(idx2, Boolean.valueOf(true));
        }
    }

    private int getNodeIDX(String s) {
        for (int i = 0; i < this.nodes.size(); i++) {
            if (((WordGraphNode) this.nodes.get(i)).word.equals(s)) {
                return i;
            }
        }
        return -1;
    }

    public ArrayList<String> getNeighbors(String word)
    {
        ArrayList<String> neighbors = new ArrayList();
        int wordIdx = getNodeIDX(word);
        if (wordIdx < 0)
        {
            Log.d(TAG, "getNeighbors: Returned empty neighbors since word not in graph.");
            return neighbors;
        }
        ArrayList<Boolean> row = (ArrayList) this.edges.get(wordIdx);
        for (int i = 0; i < row.size(); i++) {
            if (((Boolean) row.get(i)).booleanValue()) {
                neighbors.add(((WordGraphNode) this.nodes.get(i)).word);
            }
        }
        return neighbors;
    }

    public String getRandomNeighbor(String word) {
        ArrayList<Boolean> row = (ArrayList) this.edges.get(getNodeIDX(word));
        int outIDX = PositiveRandom.nextInt(row.size());
        while (!((Boolean) row.get(outIDX)).booleanValue()) {
            outIDX = PositiveRandom.nextInt(row.size());
        }
        return ((WordGraphNode) this.nodes.get(outIDX)).word;
    }

    public String getRandomWord()
    {
        int randIndex = PositiveRandom.nextInt(this.nodes.size());
        if (randIndex < 0)
        {
            Log.e(TAG, "getRandomWord: Invalid index returned. Graph is empty");
            return null;
        }
        return (this.nodes.get(randIndex)).word;
    }

    public String toString() {
        String s = BuildConfig.FLAVOR;
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(s);
        stringBuilder.append("nodes: ");
        stringBuilder.append(this.nodes);
        s = stringBuilder.toString();
        stringBuilder = new StringBuilder();
        stringBuilder.append(s);
        stringBuilder.append("\n\n--edges--\n");
        s = stringBuilder.toString();
        for (int i = 0; i < this.edges.size(); i++) {
            StringBuilder stringBuilder2 = new StringBuilder();
            stringBuilder2.append(s);
            stringBuilder2.append(this.edges.get(i));
            stringBuilder2.append("\n");
            s = stringBuilder2.toString();
        }
        return s;
    }

    public static void main(String[] args) {
        System.out.println("This is just test code!!");
        WordGraph g = new WordGraph();
        g.addWord("vine");
        g.addWord("wine");
        g.addWord("wins");
        g.addWord("fine");
        g.addWord("find");
        System.out.println(g);
    }
}