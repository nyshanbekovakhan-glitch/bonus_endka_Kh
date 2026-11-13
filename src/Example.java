import java.util.*;
public class Example {
    public static void main(String[] args) {
        List<String> patterns = Arrays.asList("he", "she", "his", "hers");
        AhoCorasick matcher = new AhoCorasick(patterns);
        for (AhoCorasick.Match match : matcher.search("ahishers")) {
            System.out.println(match);
        }
    }
}
