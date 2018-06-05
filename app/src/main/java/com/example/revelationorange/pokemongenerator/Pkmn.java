package com.example.revelationorange.pokemongenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Random;


public class Pkmn {
    private Random rng = new Random();
    private String species, usrSpecies, nature, filename;
    private List<Integer> stats = new ArrayList<>(6);
    private List<Integer> IVs = new ArrayList<>(6);
    private List<String> moves = new ArrayList<>(4);
    private List<Integer> EVs = new ArrayList<>(6);
    private int lv, dexNum, fnameNum;
    private boolean saved;
    static String[] si = {"hp", "atk", "def", "spatk", "spdef", "spd"};
    private static List<String> statIndices = new ArrayList<>();
    private static HashMap<String, List<Integer>> baseStatsDict = new HashMap<>();
    private static HashMap<String, String> speciesDict = new HashMap<>();
    private static HashMap<String, String> naturesDict = new HashMap<>();
    private static JSONObject moveLists;

    static void setup(InputStream bstatsIS, InputStream naturesIS, InputStream movesIS) throws IOException, JSONException {
        Pkmn.statIndices.addAll(Arrays.asList(Pkmn.si));

        InputStreamReader isr = new InputStreamReader(bstatsIS);
        BufferedReader br = new BufferedReader(isr);
        String[] lineArr;
        String lineStr = br.readLine();
        while ((lineStr = br.readLine()) != null) {
            List<Integer> stats = new ArrayList<>();
            lineArr = lineStr.split(",");
            stats.add(Integer.parseInt(lineArr[0]));
            for (String st: Arrays.copyOfRange(lineArr,3,9)) { stats.add(Integer.parseInt(st)); }
            baseStatsDict.put(lineArr[2].toLowerCase(), stats);
            speciesDict.put(lineArr[2].toLowerCase(), lineArr[2]);
        }

        isr = new InputStreamReader(naturesIS);
        br = new BufferedReader(isr);
        while ((lineStr = br.readLine()) != null) {
            lineArr = lineStr.split(",");
            naturesDict.put(lineArr[0], lineArr[1] + "," + lineArr[2]);
        }

        StringBuilder resp = new StringBuilder();
        isr = new InputStreamReader(movesIS);
        br = new BufferedReader(isr);
        String inputStr;
        while ((inputStr = br.readLine()) != null) {
            resp.append(inputStr);
        }
        moveLists = new JSONObject(resp.toString());
    }

//    Pkmn(String species) {
//        this.usrSpecies = species;
//        this.lv = 50;
//        this.genValues();
//    }
    Pkmn(String species, int level) {
        this.usrSpecies = species;
        this.lv = level;
        this.genValues();
    }
    private void genValues() {
        // not saved at first (obvs)
        this.saved = false;

        // species name
        this.species = speciesDict.get(this.usrSpecies);

        // IVs + EVs
        for (int i = 0; i < 6; i++) {
            this.IVs.add(rng.nextInt(32));
            this.EVs.add(0);
        }

        // nature
        this.nature = naturesDict.keySet().toArray()[rng.nextInt(naturesDict.size())].toString();

        // get base stats (and dex #)
        List<Integer> bstats = baseStatsDict.get(this.usrSpecies);
        this.dexNum = bstats.get(0);

        // calculate stats
        String[] natureBoostNerf = naturesDict.get(this.nature).split(",");
        int boost = Pkmn.statIndices.indexOf(natureBoostNerf[0]);
        int nerf = Pkmn.statIndices.indexOf(natureBoostNerf[1]);
        this.stats.add((int) (Math.floor(((2 * bstats.get(1)) + this.IVs.get(0)) * this.lv/100.) + this.lv + 10));
        for (int i = 1; i < 6; i++) {
            double natureMod = 1.;
            if (i == boost) { natureMod = 1.1; }
            else if (i == nerf) { natureMod = 0.9; }
            this.stats.add((int) (Math.floor((Math.floor(((2 * bstats.get(i+1)) + this.IVs.get(i)) * this.lv / 100.) + 5) * natureMod)) );
        }

        // moves
        try {
            JSONArray moveList = (JSONArray) moveLists.get(this.species);
            int lastMoveIndex = 0;
            for (int i = 0 ; i < moveList.length(); i++) {
                JSONArray m = (JSONArray) moveList.get(i);
                if (Integer.parseInt(m.get(0).toString()) <= this.lv) { lastMoveIndex++; }
            }
            lastMoveIndex = Math.min(lastMoveIndex, moveList.length());
            lastMoveIndex--;
            String moveName;
            int moveIndex = lastMoveIndex;
            int nMovesAdded = 0;
            while (nMovesAdded < 4 && moveIndex >= 0) {
                JSONArray move = (JSONArray) moveList.get(moveIndex);
                moveName = move.get(1).toString();
                if (!this.moves.contains(moveName)) {
                    this.moves.add(moveName);
                    nMovesAdded++;
                }
                moveIndex--;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    static boolean chkSpc(String spc) {
        return baseStatsDict.containsKey(spc.toLowerCase());
    }

    List<Integer> getStats() { return this.stats; }
    List<Integer> getIVs() { return this.IVs; }
    List<Integer> getEVs() { return this.EVs; }

    List<String> getMoves() { return this.moves; }

    String getSpecies() { return this.species; }
    String getNature() { return this.nature; }

    int getLv() { return this.lv; }

    public boolean isSaved() { return saved; }

    public void setSaved() { this.saved = true; }
}
