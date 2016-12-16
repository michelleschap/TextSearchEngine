/**
 * Calculates and stores the score of a document object
 * By Aaron Brzowski and Michelle Schapmire
 */


import java.util.Stack;


public class Score {

    private int docSize;         // how many total words in the document
    private int wordCount;          // count of every match found
    private double foundPercent;    // percent of how much of the search phrase was found
    private double spreadAvg;       // avg of how spread out parts of the phrase was found
    private double singleCountAvg;    // avg of how many of each search word was found
    private int[] counts;           // holds the counts of how many times each element was found
    private double finalScore;         // the final calculated score


    // Constructor for score
    // Takes a stack of integers that corresponds to the matching search term found,
    //  a stack of how spread out different elements were found,
    // the number of search terms, and the number of words in the whole document
    Score (Stack<Integer> foundStack, Stack<Integer> spreadStack, int size, int docSize) {
        this.docSize = docSize;
        wordCount = 0;
        counts = new int[size];         // initialize the counts array
        for (int i=0; i<size; i++)      // start each count at 0
            counts[i] = 0;

        setWordCount(foundStack);
        setSpreadAvg(spreadStack);
        setSingleCountAvg();
        setFoundPercent();
        setFinalScore();
    }

    // Returns the final score
    public double getFinalScore() {
        return finalScore;
    }


    // Sets wordCount to the total amount of words found
    // Updates the counts array to have the correct count of every single search term
    private void setWordCount(Stack<Integer> foundStack) {
        while (!foundStack.isEmpty()) {
            int index = foundStack.pop();       // get the corresponding index of the word found
            counts[index] = counts[index] + 1;  // increase its individual count by 1
            wordCount++;                        // increase the total count by 1
        }
    }

    // Sets the spreadAvg to how many indexes apart on avg a different search term is found
    private void setSpreadAvg (Stack<Integer> spreadStack) {
        if (spreadStack.isEmpty()){         // if only 1 or less search terms are found
            if (counts.length==1)           // if there is only one search term
                spreadAvg = 1;
        }
        else {
            double sum = 0.0;
            double counter = 0.0;
            while (!spreadStack.isEmpty()) {
                sum += spreadStack.pop();
                counter++;
            }
            spreadAvg = sum/counter;
        }
    }


    // Sets singleCountAvg to avg number of times a single search term is found
    private void setSingleCountAvg() {
        if (counts.length == 1){
            singleCountAvg = 0;
        }
        else {
            double sum = 0.0;
            for (int i = 0; i < counts.length; i++) {
                sum += counts[i];
            }
            double avg = sum / counts.length;
            singleCountAvg = avg;
        }
    }

    // Sets foundPercent to the percent of search terms found
    private void setFoundPercent() {
        int counter = 0;
        for (int i = 0; i <counts.length ; i++) {
            if (counts[i]!=0)
                counter++;
        }
        foundPercent = counter/(double)counts.length;
    }

    // Calculates and sets the finalScore
    // finalScore can range from 0-100
    private void setFinalScore () {
        double total = 0;

        total += (foundPercent * 0.7);      // foundPercent: 70% of score

        if (foundPercent > 0) {     // if matches were found
            total += ((wordCount / (double) docSize) * 0.15);       // wordCount: 15% of score
            total += ((1 - (spreadAvg / (double) docSize)) * 0.1);  // spreadAvg: 10% of score
            total += ((singleCountAvg / (double) docSize) * 0.05);  // singleCountAvg: 5% of score
        }

        finalScore = total * 100;
    }
}
