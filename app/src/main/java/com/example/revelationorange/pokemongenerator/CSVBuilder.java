package com.example.revelationorange.pokemongenerator;

import android.text.TextUtils;

public class CSVBuilder {
    private String str, finalStr;
    private int maxArgs;

    CSVBuilder() {
        this.str = "";
        this.maxArgs = 0;
    }

    public void addLine(String... args) {
        this.str += TextUtils.join(",", args) + "\n";
        this.maxArgs = Math.max(this.maxArgs, args.length);
    }
    public String getStr() {
        this.finalStr = "";
        String[] lines = this.str.split("\n");
        String[] splitLine;
        StringBuilder filler;
        int diff;
        for (String line : lines) {
            splitLine = line.split(",");
            diff = this.maxArgs+1 - splitLine.length;
            filler = new StringBuilder();
            for (int i = 0; i < diff; i++) { filler.append(","); }
            this.finalStr += line + filler.toString() + "\n";
        }
        return this.finalStr;
    }
}
