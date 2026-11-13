import java.util.*;
import java.nio.file.*;
import java.io.IOException;
public class Benchmark {
    private static final int RUNS = 5;
    private static final char[] ALPHABET = "abcdefghijklmnopqrstuvwxyz".toCharArray();
    public static void main(String[] args) throws IOException {
        int[] patternCounts = {10, 100, 1000};
        int[] textLengths = {1000, 10000, 100000};
        StringBuilder csv = new StringBuilder("patterns,textLength,ahoAverageNs,kmpAverageNs,ahoMatches,kmpMatches\n");
        for (int count : patternCounts) {
            List<String> patterns = makePatterns(count, 2026 + count);
            AhoCorasick automaton = new AhoCorasick(patterns);
            for (int length : textLengths) {
                String text = makeText(length, 9000 + length);
                for (int warmup = 0; warmup < 5; warmup++) {
                    automaton.search(text);
                    for (String pattern : patterns) KMP.countOccurrences(text, pattern);
                }
                long ahoTotal = 0;
                long kmpTotal = 0;
                long ahoMatches = 0;
                long kmpMatches = 0;
                for (int run = 0; run < RUNS; run++) {
                    long start = System.nanoTime();
                    List<AhoCorasick.Match> found = automaton.search(text);
                    ahoTotal += System.nanoTime() - start;
                    ahoMatches = found.size();
                    start = System.nanoTime();
                    long countMatches = 0;
                    for (String pattern : patterns) countMatches += KMP.countOccurrences(text, pattern);
                    kmpTotal += System.nanoTime() - start;
                    kmpMatches = countMatches;
                    if (ahoMatches != kmpMatches) throw new IllegalStateException("Mismatch between Aho-Corasick and KMP results");
                }
                double ahoAverage = ahoTotal / (double) RUNS;
                double kmpAverage = kmpTotal / (double) RUNS;
                csv.append(count).append(',').append(length).append(',').append(String.format(Locale.US, "%.0f", ahoAverage)).append(',').append(String.format(Locale.US, "%.0f", kmpAverage)).append(',').append(ahoMatches).append(',').append(kmpMatches).append('\n');
                System.out.printf(Locale.US, "Patterns=%d Text=%d AC=%.3f ms KMP=%.3f ms Matches=%d/%d%n", count, length, ahoAverage / 1_000_000.0, kmpAverage / 1_000_000.0, ahoMatches, kmpMatches);
            }
        }
        Files.writeString(Path.of("results/benchmark_results.csv"), csv.toString());
    }
    private static List<String> makePatterns(int count, long seed) {
        Random random = new Random(seed);
        Set<String> unique = new LinkedHashSet<>();
        while (unique.size() < count) {
            int length = 5 + random.nextInt(6);
            StringBuilder pattern = new StringBuilder();
            for (int i = 0; i < length; i++) pattern.append(ALPHABET[random.nextInt(ALPHABET.length)]);
            unique.add(pattern.toString());
        }
        return new ArrayList<>(unique);
    }
    private static String makeText(int length, long seed) {
        Random random = new Random(seed);
        StringBuilder text = new StringBuilder(length);
        for (int i = 0; i < length; i++) text.append(ALPHABET[random.nextInt(ALPHABET.length)]);
        return text.toString();
    }
}
