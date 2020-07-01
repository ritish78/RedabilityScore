import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String command = args[0];
        int sentenceCount = 1;
        String currentCharacter = "";
        DecimalFormat formatter = new DecimalFormat("00.##");
        int count = 0;
        int syllableCount = 0;
        int characterCount = 0;
        int polysyllableCount = 0;
        int syllableCountOneWord = 0;

        boolean thisVowel = false;
        boolean nextVowel = false;

        List<String> pieces = new ArrayList<>();
        Scanner scanner = new Scanner(System.in);

        try(Scanner fileScanner = new Scanner(new File(String.valueOf(Paths.get(command))))){
            while (fileScanner.hasNext()){
                currentCharacter = fileScanner.next();
                pieces.add(currentCharacter);
                if (currentCharacter.contains(".") || currentCharacter.contains("!") || currentCharacter.contains("?")){
                    sentenceCount++;
                }

            }
            System.out.println("The text is:");
            for (String word:pieces){
                System.out.print(word+" ");
            }


            for (String piece:pieces){
                syllableCountOneWord = 0;

                //If the word is a single character, we add it to the syllable count and we skip it from further calculations.
                if (piece.length() == 1){
                    syllableCount++;
                    continue;
                }

                for (int i = 0; i < piece.length() - 1; i++) {
                    characterCount++;

                    //Single is the character which we are using currently for the comparison.
                    char single = piece.charAt(i);
                    thisVowel = isVowel(single);

                    //nextVowel is the character which comes after 'single' character.
                    //Loop runs from 0 to length-2. So, we don't get ArrayIndexOutOfBoundsException
                    nextVowel = isVowel(piece.charAt(i + 1));
                    if (i == piece.length()-2){
                        nextVowel = false;
                    }

                    //If the current character is a vowel, we are increasing the count.
                    //And if the next character is not a vowel then, we are increasing the syllableCount.
                    if (thisVowel){
                        count++;
                        if (!nextVowel){
                            syllableCount++;
                            syllableCountOneWord++;
                        }
                    }
                }

                //if one word has more than 2 syllable, then the word is polysyllable. So, increasing its count.
                if (syllableCountOneWord > 2){
                    polysyllableCount++;
                }

                //If we don't have any vowel, then we are increasing the syllable count by one.
                if (count == 0){
                    syllableCount++;
                }else {
                    count = 0;
                }


            }
            System.out.println("Words: " + pieces.size());
            System.out.println("Sentences: " + sentenceCount);
            //Characters is the addition of characterCount and pieces.size(or Word) since, we are skipping
            //each last character of every word from the list.
            System.out.println("Characters: " + (characterCount + pieces.size()));
            System.out.println("Syllables: " + syllableCount);
            System.out.println("Polysyllables: "+ polysyllableCount);
            System.out.print("Enter the score you want to calculate (ARI, FK, SMOG, CL, all): ");
            String score = scanner.nextLine().toUpperCase();
            System.out.println();

            int ageTotal = 0;
            double ari_score = automatedReadabilityIndex((characterCount+pieces.size()), pieces.size(), sentenceCount);
            int ari_age = checkAge(ari_score);

            double fk_score = fleshKincaidReadability(pieces.size(), sentenceCount, syllableCount);
            int fk_age = checkAge(fk_score);

            double smog_score = smogIndex(polysyllableCount, sentenceCount);
            int smog_age = checkAge(smog_score);

            double cl_score = clIndex((characterCount + pieces.size()), pieces.size(), sentenceCount);
            int cl_age = checkAge(cl_score);

            ageTotal = ari_age + fk_age + smog_age + cl_age;

            //Need to use formatter here otherwise it will print decimal to many  digits.
            switch (score){
                case "ARI":
                    System.out.println("Automated Readability Index: " + formatter.format(ari_score) + " (about "+ ari_age + " year olds.)");
                    break;
                case "FK":
                    System.out.println("Flesch–Kincaid readability tests: "+ formatter.format(fk_score) + " (about "+fk_age+" year olds.)");
                    break;
                case "SMOG":
                    System.out.println("Simple Measure of Gobbledygook: " + formatter.format(smog_score) +" (about "+smog_age+" year olds.)");
                    break;
                case "CL":
                    System.out.println("Coleman–Liau index: " + formatter.format(cl_score) + " (about " + cl_age + " year olds).");
                    break;
                case "ALL":
                    System.out.println("Automated Readability Index: " + formatter.format(ari_score) + " (about "+ ari_age + " year olds.)");
                    System.out.println("Flesch–Kincaid readability tests: "+ formatter.format(fk_score) + " (about "+fk_age+" year olds.)");
                    System.out.println("Simple Measure of Gobbledygook: " + formatter.format(smog_score) +" (about "+smog_age+" year olds.)");
                    System.out.println("Coleman–Liau index: " + formatter.format(cl_score) + " (about " + cl_age + " year olds).");
                    System.out.println();
                    System.out.println("The text should be understood in average by " + (double)ageTotal / 4 +" year olds.");
                    break;
                default:
                    break;
            }

        }catch(IOException e){
            System.out.println("File not found!");
        }

    }

    public static int checkAge(double scoreDecimal){
        int age = 0;
        int score = (int)Math.round(scoreDecimal);
        if (score == 1){
            age = 6;
        }else if (score == 2){
            age = 7;
        }else if (score == 3){
            age = 9;
        }else if (score == 4){
            age = 10;
        }else if (score == 5){
            age = 11;
        }else if (score == 6){
            age = 12;
        }else if (score == 7){
            age = 13;
        }else if (score == 8){
            age = 14;
        }else if (score == 9){
            age = 15;
        }else if (score == 10){
            age = 16;
        }else if (score == 11){
            age = 17;
        }else if (score == 12){
            age = 18;
        }else if (score == 13){
            age = 24;
        }else{
            age = 25;
        }
        return age;
    }

    public static boolean isVowel(char c){
        if (c == 'a' || c == 'e' || c == 'i' || c == 'o' || c == 'u' || c == 'y'){
            return true;
        }
        return false;
    }

    public static double automatedReadabilityIndex(int characters, int wordCount, int sentenceCount){
        double scoreDecimal = 4.71 * ((double)characters/wordCount) + 0.5 * ((double)wordCount/sentenceCount) -21.43;
        return scoreDecimal;
    }

    public static double fleshKincaidReadability(int words, int sentences, int syllables){
        double scoreDecimal = 0.39 * ((double) words/sentences) + 11.8 * ((double) syllables/words) - 15.59;
        return scoreDecimal;
    }

    public static double smogIndex(int polysyllables, int sentences){
        double scoreDecimal = (1.043 * Math.sqrt((polysyllables * (30/(double)sentences))) + 3.1291);
        return scoreDecimal;
    }

    public static double clIndex(double letters, double words, double sentences){
        double CLI = (0.0588 * ((letters / words) * 100)) - (0.296 * ((sentences / words) * 100)) - 15.8;
        return CLI;
    }
}
