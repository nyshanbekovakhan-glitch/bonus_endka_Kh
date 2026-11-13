import java.util.*;
public class AhoCorasick {
    private static class Node {
        Map<Character, Node> next = new HashMap<>();
        Node failure;
        List<Integer> output = new ArrayList<>();
        Node outputLink;
    }
    private final Node root = new Node();
    private final List<String> patterns;
    public AhoCorasick(List<String> patterns) {
        this.patterns = new ArrayList<>(patterns);
        buildTrie();
        buildFailureLinks();
    }
    private void buildTrie() {
        for (int id = 0; id < patterns.size(); id++) {
            Node current = root;
            for (char ch : patterns.get(id).toCharArray()) {
                current = current.next.computeIfAbsent(ch, key -> new Node());
            }
            current.output.add(id);
        }
    }
    private void buildFailureLinks() {
        Queue<Node> queue = new ArrayDeque<>();
        root.failure = root;
        for (Node child : root.next.values()) {
            child.failure = root;
            queue.add(child);
        }
        while (!queue.isEmpty()) {
            Node current = queue.remove();
            for (Map.Entry<Character, Node> edge : current.next.entrySet()) {
                char ch = edge.getKey();
                Node child = edge.getValue();
                Node fallback = current.failure;
                while (fallback != root && !fallback.next.containsKey(ch)) {
                    fallback = fallback.failure;
                }
                if (fallback.next.containsKey(ch) && fallback.next.get(ch) != child) {
                    child.failure = fallback.next.get(ch);
                } else {
                    child.failure = root;
                }
                child.outputLink = !child.failure.output.isEmpty() ? child.failure : child.failure.outputLink;
                queue.add(child);
            }
        }
    }
    public List<Match> search(String text) {
        List<Match> matches = new ArrayList<>();
        Node current = root;
        for (int i = 0; i < text.length(); i++) {
            char ch = text.charAt(i);
            while (current != root && !current.next.containsKey(ch)) {
                current = current.failure;
            }
            current = current.next.getOrDefault(ch, root);
            Node outputNode = current;
            while (outputNode != null) {
                for (int patternId : outputNode.output) {
                    String pattern = patterns.get(patternId);
                    matches.add(new Match(pattern, i - pattern.length() + 1, i));
                }
                outputNode = outputNode.outputLink;
            }
        }
        return matches;
    }
    public static class Match {
        public final String pattern;
        public final int start;
        public final int end;
        public Match(String pattern, int start, int end) {
            this.pattern = pattern;
            this.start = start;
            this.end = end;
        }
        @Override
        public String toString() {
            return pattern + " at [" + start + ", " + end + "]";
        }
    }
}
