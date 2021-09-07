import java.util.*;

/***
 Jonathan Lee, Ryan Wu
 Winter 2020.
 ***/

public class ViterbiTest {
    public static void main(String[] args) {
        // Stores user inputs and ability to read input
        Scanner input = new Scanner(System.in);
        String user;

        // Transition and observation maps
        HashMap<String, HashMap<String, Double>> transition = new HashMap<>();
        HashMap<String, HashMap<String, Double>> observations = new HashMap<>();

        Viterbi viterbi = new Viterbi(transition, observations);

        // Lets the user pick: train on brown or simple?
        System.out.println("Which corpus? 'B' for Brown, 'S' for Simple: ");

        user = input.next();

        // Repeats until the user properly responds.
        while (!user.toLowerCase().equals("b") && !user.toLowerCase().equals("s")) {
            System.out.println("Must chose one of the two options. 'B' for Brown, 'S' for Simple: ");
            user = input.next();
        }

        String sentenceFile = "", tagFile = "";

        // Train on brown files
        if (user.toLowerCase().equals("b")) {
            sentenceFile = "inputs/brown-train-sentences.txt";
            tagFile = "inputs/brown-train-tags.txt";
        }

        // Train on simple files
        else if (user.toLowerCase().equals("s")) {
            sentenceFile = "inputs/simple-train-sentences.txt";
            tagFile = "inputs/simple-train-tags.txt";
        }

        // Train model on brown or simple depending on the user's choice above
        try {
            viterbi.training(sentenceFile, tagFile);
        }

        catch (Exception e) {
            System.err.println("File(s) not found!");
        }

        // Now, the user chooses how they want to test the model: on the brown files, simple files, or with their own console input
        System.out.println("Now, your choice of test? 'B' for Brown, 'S' for Simple, 'I' for input/console: ");

        user = input.next();

        // Makes the user respond with proper input
        while (!user.toLowerCase().equals("b") && !user.toLowerCase().equals("s") && !user.toLowerCase().equals("i")) {

            System.out.println("Must chose one of the three options. 'B' for Brown, 'S' for Simple, 'I' for input/console: ");
            user = input.next();
        }

        // Brown case
        if (user.toLowerCase().equals("b")) {
            try {
                viterbi.fileTest("inputs/brown-test-sentences.txt", "inputs/brown-test-tags.txt");
            }

            catch (Exception e) {
                System.err.println("File(s) not found!");
            }
        }

        // Simple case
        else if (user.toLowerCase().equals("s")) {
            try {
                viterbi.fileTest("inputs/simple-test-sentences.txt", "inputs/simple-test-tags.txt");
            }

            catch (Exception e) {
                System.err.println("File(s) not found!");
            }
        }

        // Console input case
        else if (user.toLowerCase().equals("i")) {
            input = new Scanner(System.in);
            viterbi.consoleTest(input);
        }

        // Close file, we're done reading from it.
        input.close();
    }
}
