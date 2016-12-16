/**
 * Stores the text file information and score object for the file
 * By Aaron Brzowski and Michelle Schapmire
 */


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.Stack;


public class Document implements Comparable<Document>{

    private String fileLoc;               // file location
    private ArrayList<String> wordList;     // array of words from the file
    private Score docScore;                 // the docScore relevant to the search

    Document(String fileLoc) {
        this.fileLoc = fileLoc;
        this.wordList = new ArrayList<String>();
        readFile();
    }

    // Returns the name of the file
    public String getFileName() {

        String fileName = fileLoc;
        int pos = fileName.lastIndexOf(".");
        if (pos > 0) {
            fileName = fileName.substring(0, pos);
        }
        return fileName;
    }

    // Returns the score of the document
    public double getScore() {
        return docScore.getFinalScore();
    }

    // Calculates and sets the score based on the search terms
    public void setScore(String[] searchWords) {
        calculateScore(searchWords);
    }

    // Reads the file by words and stores it into an array
    private void readFile() {
        try {
            File file = new File(fileLoc);
            Scanner scanFile = null;
            scanFile = new Scanner(new FileReader(file));
            String nextWord;

            while (scanFile.hasNext()) {
                nextWord = scanFile.next().toLowerCase().trim();
                wordList.add(nextWord);
            }
        } catch (FileNotFoundException e) {
            System.out.println("File not found");
        }
    }

    // Helper method for getScore
    // Calculates and creates a score based on the search words
    private void calculateScore(String[] searchWords) {
        String currWord;
        Stack<Integer> foundStack = new Stack<Integer>();   // to store the searchWords index every time it's found
        Stack<Integer> indexStack = new Stack<Integer>();   // holds the index where the word was found in the wordsList
        Stack<Integer> spreadStack = new Stack<Integer>();  // stores how far apart a different word is found

        for (int i=0; i<wordList.size(); i++){
            currWord = wordList.get(i).toLowerCase();

            for (int n=0; n<searchWords.length; n++) {
                if (currWord.replaceAll("[^a-zA-Z0-9]+", " ").trim().equals(searchWords[n])){
                    if (foundStack.isEmpty()) {
                        foundStack.push(n);
                        indexStack.push(i);
                    }
                    else {
                        if (foundStack.peek()==n) {
                            indexStack.pop();
                            indexStack.push(i);
                        }
                        else {
                            int spread = (i - indexStack.pop());
                            indexStack.push(i);
                            spreadStack.push(spread);
                        } // else
                        foundStack.push(n);
                    } // else
                } // if (currWord.contains(searchWords[n]))
            } // for (int n=0; n<searchWords.length; n++)
        } // for (int i=0; i<fileList.size(); i++)

        docScore = new Score(foundStack, spreadStack, searchWords.length, wordList.size());
    } // calculateScore()


    @Override
    public int compareTo(Document document) {
        if (this.getScore() < document.getScore()) {
            return -1;
        }
        else if (this.getScore() > document.getScore()) {
            return 1;
        }
        else {
            return 0;
        }
    }
}