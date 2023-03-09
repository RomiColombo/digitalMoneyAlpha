package com.digitalAlpha.accounts.utils;

import org.springframework.core.io.ClassPathResource;

import java.io.*;
import java.text.DecimalFormat;

public class AccountMaker {
    public static String[] getWords() throws IOException {
        BufferedReader br = new BufferedReader(new InputStreamReader(new ClassPathResource("words.csv").getInputStream()));
        String[] words = new String[15069];
        String line;
        String splitBy = "\\s";
        int i = 0;
        while ((line = br.readLine()) != null) {
            for(String s:line.split(splitBy)) {
                words[i] = s;
                i++;
            }
        }
        return words;
    }

    public static String getRandomAlias() throws IOException {
        String[] words = getWords();
        String w1 = words[(int) Math.floor((Math.random()*1500))];
        String w2 = words[(int) Math.floor((Math.random()*1500))];
        String w3 = words[(int) Math.floor((Math.random()*1500))];
        return w1 + "." + w2 + "." + w3;
    }

    public static String getRandomCvu() {
        Double number = Math.floor(Math.random() * 1e22);
        DecimalFormat df = new DecimalFormat("#");
        return df.format(number);
    }
}
