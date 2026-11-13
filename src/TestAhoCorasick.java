import java.util.*;
public class TestAhoCorasick {
    public static void main(String[] args) {
        AhoCorasick matcher = new AhoCorasick(Arrays.asList("he", "she", "his", "hers", "he"));
        List<AhoCorasick.Match> matches = matcher.search("ahishers");
        if (matches.size() != 5) throw new AssertionError("Expected 5 matches, got " + matches.size());
        Set<String> found = new HashSet<>();
        for (AhoCorasick.Match match : matches) found.add(match.pattern + ":" + match.start + ":" + match.end);
        if (!found.contains("his:1:3") || !found.contains("she:3:5") || !found.contains("he:4:5") || !found.contains("hers:4:7")) throw new AssertionError("Missing expected occurrence: " + found);
        System.out.println("All Aho-Corasick tests passed.");
    }
}
