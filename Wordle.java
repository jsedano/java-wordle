/**
taken from: https://www.tomsguide.com/news/what-is-wordle

Wordle is such a simple game that there are hardly any rules. But here you go:

You have to guess the Wordle in six goes or less
A correct letter turns green
A correct letter in the wrong place turns yellow
An incorrect letter turns gray
Letters can be used more than once
Answers are never plurals

also words are 5 character long
**/

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Scanner;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.stream.Stream;
import java.nio.charset.StandardCharsets;
import java.io.Reader;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.Random;

public class Wordle {



    record EvaluationResult(boolean result, String breakdown) {}


    public static EvaluationResult evaluate(String target, String guess) {
        if(target.length() != guess.length()) {
            throw new IllegalArgumentException("target length and guess length must be the same");
        }
        String result = "";
        for(int i = 0; i < target.length(); i++) {
            if(target.charAt(i) == guess.charAt(i)) {
                result += "🟩";
            } else if(target.indexOf(guess.charAt(i)) != -1){
                result += "🟨";
            } else {
                result += "⬜";
            }
        }
        return new EvaluationResult(target.equals(guess), result);
    }

    public static boolean game(Input input, String target) {
        System.out.println("Starting new game");
        int tries = 6;
        String breakdown = "";
        EvaluationResult er = null;
        for(int i = 0; i < tries; i++) {
            System.out.print(String.format("%s Enter guess: ", breakdown));
            String guess = input.readInput();
            //validate input
            er = evaluate(target, guess);
            if (er.result()){
                break;
            }
            breakdown = er.breakdown();
        }
        System.out.println(er.breakdown());
        return er.result();
    }

    public static void gameLoop(){
        Input input = new Input();
        int option = -1;
        String failedWord = null;
        Word word = new Word();
        do {
            System.out.println("- java wordle -");
            System.out.println("1.- new game");
            if(failedWord != null) {
                System.out.println("2.- try again with the same word");
            }
            System.out.println("0.- exit");
            System.out.print("select an option: ");
            option = input.readInt();

            switch(option) {
                case 1:
                case 2:
                    String newWord = word.getWord();
                    if(!game(input, option == 1 || failedWord == null ? newWord : failedWord)) {
                        failedWord = newWord;
                        System.out.println("you've lost");
                    } else {
                        failedWord = null;
                        System.out.println("Congrats! you've won!!");
                    }
                break;
                case 0:
                    System.out.println("have a nice day");
                break;
                default:
                    System.out.println("please enter valid input");
            }
        } while (option != 0);
    }



    public static void main(String []args){
        gameLoop();
        new Word();
    }
}

class Input {
    private BufferedReader br;

    public Input() {
        br = new BufferedReader(new InputStreamReader(System.in));
    }

    public String readInput(){
        try {
            return br.readLine();
        }
        catch(IOException e) {
            throw new RuntimeException("something is wrong :/ ");
        }
    }

    public int readInt(){
        try {
            return Integer.parseInt(readInput());
        } catch(NumberFormatException e) {
            return -1;
        }
    }
}

class Word {
    private Random r;
    private List <String> words;

    public Word() {
        loadFile();
        r = new Random();
    }

    private void loadFile(){
        try (final InputStream is = new FileInputStream(new File("donquixote.txt"));
                final Reader r = new InputStreamReader(is, StandardCharsets.UTF_8);
                final BufferedReader br = new BufferedReader(r);
                final Stream<String> lines = br.lines()) {
                    words = new ArrayList(lines.map(i -> i.split("[ .,]+")).flatMap(Arrays::stream).filter(i -> i.length()==5).map(String::toLowerCase).filter(i -> i.matches("[a-z]+")).filter(i -> i.charAt(i.length()-1)!='s').collect(Collectors.toSet()));
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public String getWord() {
        String w = words.get(r.nextInt(words.size()));
        System.out.println(w);
        return w;
    }
}
