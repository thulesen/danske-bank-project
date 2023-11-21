package dk.tue;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordCount {
    private static final Predicate<String> WORD_PREDICATE = Pattern.compile("^[a-zA-Z]+$").asPredicate();
    private final Map<String, Integer> wordCounts;

    private WordCount(final Map<String, Integer> wordCounts) {
        this.wordCounts = wordCounts;
    }

    public WordCount() {
        this(new HashMap<>());
    }

    public static WordCount of(final Stream<String> words) {
        final var result = new WordCount();
        words.forEach(result::addWord);
        return result;
    }

    public void addWord(final String word) {
        final var upperCaseWord = word.toUpperCase();
        wordCounts.put(upperCaseWord, getCount(upperCaseWord) + 1);
    }

    public int getCount(final String word) {
        if (!WORD_PREDICATE.test(word)) throw new IllegalArgumentException("String %s is not a word.".formatted(word));

        final var upperCaseWord = word.toUpperCase();
        return wordCounts.getOrDefault(upperCaseWord, 0);
    }

    public WordCount mergeWith(final WordCount wordCount) {
        wordCount.wordCounts.forEach((word, count) -> wordCounts.put(word, getCount(word) + count));
        return this;
    }

    public Map<Character, WordCount> partitionByLeadingCharacter() {
        return wordCounts.entrySet()
                         .stream()
                         .collect(Collectors.groupingBy(entry -> entry.getKey().charAt(0),
                                                        Collector.of(WordCount::new,
                                                                     (wordCount, entry) -> wordCount.wordCounts.put(
                                                                             entry.getKey(),
                                                                             entry.getValue()),
                                                                     WordCount::mergeWith)));
    }

    public WordCount filterBy(final Predicate<String> wordPredicate) {
        return new WordCount(wordCounts.entrySet()
                                       .stream()
                                       .filter(entry -> wordPredicate.test(entry.getKey()))
                                       .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue)));
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final WordCount wordCount = (WordCount) o;

        return wordCounts.equals(wordCount.wordCounts);
    }

    @Override
    public int hashCode() {
        return wordCounts.hashCode();
    }

    @Override
    public String toString() {
        return wordCounts.entrySet()
                         .stream()
                         .map(entry -> entry.getKey() + ": " + entry.getValue())
                         .collect(Collectors.joining("\n"));
    }
}
