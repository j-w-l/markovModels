import java.io.BufferedReader;
import java.io.FileReader;
import java.util.*;

/***
 Jonathan Lee, Ryan Wu
 Winter 2020.
 ***/

public class Viterbi {
    // Instance variables
    // -Transition Map
    private HashMap<String, HashMap<String, Double>> transition;
    // -Observation Map
    private HashMap<String, HashMap<String, Double>> observations;
    // -Penalty -- set to -100 as per assignment specs
    private double penalty = -100;

    // Constructor -- can pass in empty or non-empty hash-maps. Empty case is equivalent to a constructor that auto-initializes them...
    public Viterbi(HashMap<String, HashMap<String, Double>> transition, HashMap<String, HashMap<String, Double>> observation){
        this.transition=transition;
        this.observations =observation;
    }

    // Decoding. Takes a string and performs Viterbi decoding, the heart of the assignment.
    public ArrayList<String> decoding(String obs){
        // A set of our current states.
        Set<String> currStates = new HashSet<>();

        // "#", as per assignment specs, represents start.
        currStates.add("#");

        // A map of tags to their scores.
        Map <String, Double> currScores = new HashMap<>();
        currScores.put("#", 0.0);

        // A map of tags and their backtracing tag (as Strings)
        ArrayList<Map <String, String>> backtrace = new ArrayList<>();

        // Splits the input string into respective words
        String[] words = obs.split(" ");

        // For each word in the input string
        for (int i = 0; i < words.length; i++){
            // Holds the word's next possible tags (connection)
            Set<String> nextStates = new HashSet<>();

            // Maps each tag to the scores of those next possible tags
            Map <String, Double> nextScores = new HashMap<>();

            // Temporary backtracing; we'll add it to the backtrace variable in outer scope.
            Map <String, String> link = new HashMap<>();

            // For each of our current words --
            for (String s: currStates){
                // For those words with a connection (transition)
                if (transition.containsKey(s)) {
                    for (String nextState : transition.get(s).keySet()) {
                        // Needed to preserve variable's value in outer scope
                        double nextScore;
                        // Each of these nextState strings are literally a nextState...hence why they're added.
                        nextStates.add(nextState);

                        // If it contains our word of interest, add the current score, transition sore, and observation score
                        if (observations.get(nextState) != null && observations.get(nextState).containsKey(words[i]))
                            nextScore = currScores.get(s) + transition.get(s).get(nextState) + observations.get(nextState).get(words[i]);

                            // If it does not contain our word of interest, add the first two aforementioned value but add the (negative) penalty.
                        else
                            nextScore = currScores.get(s) + transition.get(s).get(nextState) + penalty;

                        // Puts/updates our nextScores and backtrace map/list if necessary
                        if ((!nextScores.containsKey(nextState)) || nextScore > nextScores.get(nextState)) {
                            nextScores.put(nextState, nextScore);
                            if (backtrace.size() == i) {
                                link.put(nextState, s);
                                backtrace.add(link);
                            }
                            else
                                backtrace.get(i).put(nextState, s);
                        }
                    }
                }
            }

            // Move to the next step # (a ripple outward, like BFS).
            currStates=nextStates;
            currScores=nextScores;
        }

        // Largest state
        // Getting first element of set: https://stackoverflow.com/questions/8882295/how-to-get-the-first-element-of-the-list-or-set
        Iterator iter = currStates.iterator();
        String end = "";
        if (!currStates.isEmpty())
            end = (String) iter.next();

        // Get the largest score.
        Double largest = currScores.get(end);
        for (String s: currScores.keySet()){
            if (largest < currScores.get(s)){
                end = s;
                largest = currScores.get(s);
            }
        }

        // Our result -- add the largest tag and use backtrace to go backwards and construct your list of states.
        ArrayList<String> pathway = new ArrayList<>();
        pathway.add(end);
        String holder= end;
        for (int i = backtrace.size() - 1; i > 0; i--) {
            holder = backtrace.get(i).get(holder);
            pathway.add(0, holder);
        }

        return pathway;
    }


    // consoleTest. Give the tags from an input line / console, then decode it (by invoking decoding).
    public void consoleTest(Scanner reader) {
        System.out.println("Enter a line, or to quit, type 'xyz': ");

        String line = reader.nextLine();

        // Excludes quit/invalid input cases.
        if (!line.equals("xyz") && !line.equals(""))
            System.out.println(decoding(line));
    }


    // fileTest. Evaluate performance on a pair of test files, as per assignment specs.
    public void fileTest(String sentenceFile, String tagFile) throws Exception {
        // Exception handling -- avoids invalid input (files that are invalid / don't exist).
        BufferedReader readSentence = null, readTag = null;
        try {
            readSentence = new BufferedReader(new FileReader(sentenceFile));
            readTag = new BufferedReader(new FileReader(tagFile));
        }

        catch (Exception e) {
            System.err.println("File(s) invalid, try again!");
        }

        // We need these numbers to calculate an accuracy ratio, obviously.
        int sigma = 0, failure = 0;

        // Our line reader.
        String tag;

        // Reader each line in the tagFile.
        while ((tag = readTag.readLine()) != null) {
            // Decode that line.
            ArrayList<String> viterbi = decoding(readSentence.readLine().toLowerCase());

            String[] pieces = tag.split(" ");

            // For each word in the line, check the accuracy of our decoding model by accumulating total / failure counts.
            for (int i = 0; i < pieces.length; i++) {
                if (!viterbi.get(i).equals(pieces[i])) {
                    failure++;
                    sigma++;
                }
                else
                    sigma++;
            }
        }

        // Console output our results: total, mistakes, accuracy.
        System.out.print("Total: " + sigma);
        System.out.print("; mistakes made: " + failure);
        int success = sigma - failure;
        double accuracy = 100.0 * success / sigma;

        System.out.print("; that makes for an accuracy of: " + accuracy);

        // Close files; we're done with them.
        readSentence.close();
        readTag.close();
    }


    // Training -- uses a sentenceFile and tagFile to train our model as per assignment specs.
    public void training(String sentenceFile, String tagFile) throws Exception {
        // Necessary to avoid "might not have initialized Java error"
        BufferedReader sentence = null, tag = null;
        String lineS, lineT;

        // Exception handling.
        try {
            sentence = new BufferedReader(new FileReader(sentenceFile));
            tag = new BufferedReader(new FileReader(tagFile));
        }

        catch (Exception e) {
            System.err.println("Error opening files!");
        }

        // Stores our starting states.
        HashMap<String, Double> startTags = new HashMap<>();

        // Transition training.
        while ((lineT = tag.readLine()) != null) {
            String[] pieces = lineT.split(" ");

            // Builds up startTags -- puts in or updates count of starting tags.
            if (!startTags.containsKey(pieces[0]))
                startTags.put(pieces[0], 1.0);
            else
                startTags.put(pieces[0], 1.0 + startTags.get(pieces[0]));

            // For each word in the line --
            for (int i = 0; i < pieces.length - 1; i++) {
                // If our transition map already contains the key --
                if (transition.containsKey(pieces[i])) {
                    // Establish the connection with it and its next word, or update it.
                    if (transition.get(pieces[i]).containsKey(pieces[i + 1]))
                        transition.get(pieces[i]).put(pieces[i + 1], 1.0 + transition.get(pieces[i]).get(pieces[i + 1]));
                    else
                        transition.get(pieces[i]).put(pieces[i + 1], 1.0);
                }

                // If our transition map doesn't already contain the key, add it!
                else {
                    transition.put(pieces[i], new HashMap<>());
                    transition.get(pieces[i]).put(pieces[i + 1], 1.0);
                }
            }

            // Add the entirety of our starting tags from "#" which denotes start, as per assignment specs.
            transition.put("#", startTags);
        }

        // Closes the tagFile, we'll need to reopen it anew.
        tag.close();
        // reread tag -- we'll need it fresh
        tag = new BufferedReader(new FileReader(tagFile));

        // Observation training.
        while((lineT = tag.readLine()) != null && (lineS = sentence.readLine()) != null) {
            // Store the tags and the words into variables.
            String[] tags = lineT.split(" ");
            String[] words = lineS.split(" ");

            // For each of the tags in the line --
            for (int i = 0; i < tags.length; i++) {
                // If the observation map already contains that tag --
                if (observations.containsKey(tags[i])) {
                    // Put it into or update the observation map
                    if (observations.get(tags[i]).containsKey(words[i]))
                        observations.get(tags[i]).put(words[i], 1.0 + observations.get(tags[i]).get(words[i]));
                    else
                        observations.get(tags[i]).put(words[i], 1.0);
                }

                // The observation map doesn't already contain that tag, so add it.
                else {
                    observations.put(tags[i], new HashMap<>());
                    observations.get(tags[i]).put(words[i], 1.0);
                }
            }
        }

        // Mathematical normalize for transition.
        for (String innerMap : transition.keySet()) {
            double sigma = 0;
            for (String prob : transition.get(innerMap).keySet())
                sigma += transition.get(innerMap).get(prob);

            for (String each : transition.get(innerMap).keySet())
                transition.get(innerMap).put(each, Math.log(transition.get(innerMap).get(each) / sigma));
        }

        // Mathematical normalize for observation.
        for (String innerMap : observations.keySet()) {
            double sigma = 0;
            for (String prob : observations.get(innerMap).keySet())
                sigma += observations.get(innerMap).get(prob);

            for (String each : observations.get(innerMap).keySet())
                observations.get(innerMap).put(each, Math.log(observations.get(innerMap).get(each) / sigma));
        }
    }
}