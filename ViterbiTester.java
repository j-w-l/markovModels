import java.util.*;

/***
 Jonathan Lee, Ryan Wu
 Winter 2020.
 ***/

public class ViterbiTester {
    public static void main(String[] args) {
        HashMap<String, HashMap<String, Double>> transition = new HashMap<>();

        // Hard code a test.
        HashMap<String, Double> holder = new HashMap<>();
        holder.put("PRO", Math.log(.7));
        holder.put("DET", Math.log(.2));
        holder.put("N", Math.log(.1));
        transition.put("#", holder);

        holder = new HashMap<>();
        holder.put("N", Math.log((double)3/8));
        holder.put("V", Math.log((double)4/8));
        holder.put("MOD", Math.log((double)1/8));
        transition.put("PRO", holder);

        holder = new HashMap<>();
        holder.put("V", Math.log((double)6/14));
        holder.put(".", Math.log((double)7/14));
        holder.put("P", Math.log((double)1/14));
        transition.put("N", holder);

        holder = new HashMap<>();
        holder.put("ADJ", Math.log((double)2/11));
        holder.put("P", Math.log((double)3/11));
        holder.put("PRO", Math.log((double)1/11));
        holder.put("DET", Math.log((double)4/11));
        holder.put(".", Math.log((double)1/11));
        transition.put("V", holder);

        holder = new HashMap<>();
        holder.put(".", Math.log((double)1));
        transition.put("ADJ", holder);

        holder = new HashMap<>();
        holder.put("N", Math.log((double)1/4));
        holder.put("DET", Math.log((double)3/4));
        transition.put("P", holder);

        holder = new HashMap<>();
        holder.put("N", Math.log((double)1));
        transition.put("DET", holder);

        holder = new HashMap<>();
        holder.put("V", Math.log((double)1));
        transition.put("MOD", holder);

        holder = new HashMap<>();
        transition.put(".", holder);



        HashMap<String, HashMap<String, Double>> observations = new HashMap<>();

        holder = new HashMap<>();
        holder.put("your", Math.log((double)1/8));
        holder.put("you", Math.log((double)1/8));
        holder.put("we", Math.log((double)3/8));
        holder.put("he", Math.log((double)1/8));
        holder.put("my", Math.log((double)2/8));
        observations.put("PRO", holder);

        holder = new HashMap<>();
        holder.put("work", Math.log((double)1/14));
        holder.put("trains", Math.log((double)3/14));
        holder.put("cave", Math.log((double)3/14));
        holder.put("night", Math.log((double)2/14));
        holder.put("watch", Math.log((double)1/14));
        holder.put("dog", Math.log((double)3/14));
        holder.put("bark", Math.log((double)1/14));
        observations.put("N", holder);

        holder = new HashMap<>();
        holder.put("is", Math.log((double)2/11));
        holder.put("work", Math.log((double)2/11));
        holder.put("hide", Math.log((double)1/11));
        holder.put("are", Math.log((double)1/11));
        holder.put("fast", Math.log((double)1/11));
        holder.put("bark", Math.log((double)1/11));
        holder.put("held", Math.log((double)1/11));
        holder.put("watch", Math.log((double)1/11));
        holder.put("trains", Math.log((double)1/11));
        observations.put("V", holder);

        holder = new HashMap<>();
        holder.put("beautiful", Math.log((double)1/2));
        holder.put("fast", Math.log((double)1/2));
        observations.put("ADJ", holder);

        holder = new HashMap<>();
        holder.put("for", Math.log((double)1/4));
        holder.put("in", Math.log((double)3/4));
        observations.put("P", holder);

        holder = new HashMap<>();
        holder.put("this", Math.log((double)1/9));
        holder.put("the", Math.log((double)6/9));
        holder.put("a", Math.log((double)2/9));
        observations.put("DET", holder);

        holder = new HashMap<>();
        holder.put("should", Math.log((double)1));
        observations.put("MOD", holder);

        holder = new HashMap<>();
        holder.put(".", Math.log((double)1));
        observations.put(".", holder);


        // Create our simple hardcoded model
        Viterbi simple = new Viterbi (transition, observations);

        // Test it on the sentence "the dog saw trains in the night"
        String test = "";
        test += "the ";
        test += "dog ";
        test += "saw ";
        test += "trains ";
        test += "in ";
        test += "the ";
        test += "night ";
        test += ".";

        // Print parts of speech for the sentence.
        System.out.println("Part of Speech: " + simple.decoding(test));
    }
}
