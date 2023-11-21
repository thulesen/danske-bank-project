package dk.tue;

import org.junit.jupiter.api.Test;

import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class WordCountTest {

    private final WordCount subject = new WordCount();
    private final String VALID_WORD = "word";

    @Test
    public void getCount_is_zero_initially() {
        assertEquals(0, subject.getCount(VALID_WORD));
    }

    @Test
    public void getCount_throws_if_string_contains_whitespace() {
        assertThrows(IllegalArgumentException.class, () -> subject.getCount("some word"));
    }

    @Test
    public void getCount_throws_if_string_contains_numbers() {
        assertThrows(IllegalArgumentException.class, () -> subject.getCount("some8"));
    }

    @Test
    public void addCount_can_set_word_count_to_1() {
        subject.addWord(VALID_WORD);
        assertEquals(1, subject.getCount(VALID_WORD));
    }

    @Test
    public void addCount_can_set_word_count_to_2() {
        subject.addWord(VALID_WORD);
        subject.addWord(VALID_WORD);
        assertEquals(2, subject.getCount(VALID_WORD));
    }

    @Test
    public void addWord_throws_if_string_contains_whitespace() {
        assertThrows(IllegalArgumentException.class, () -> subject.addWord("some word"));
    }

    @Test
    public void addWord_throws_if_string_contains_numbers() {
        assertThrows(IllegalArgumentException.class, () -> subject.addWord("some8"));
    }

    @Test
    public void wordCount_is_case_insensitive() {
        subject.addWord(VALID_WORD);
        assertEquals(1, subject.getCount("WoRd"));
    }

    @Test
    public void merge_adds_one_word() {
        subject.addWord(VALID_WORD);

        final var otherWordCount = new WordCount();
        otherWordCount.addWord(VALID_WORD);

        subject.mergeWith(otherWordCount);

        assertEquals(2, subject.getCount(VALID_WORD));
    }

    @Test
    public void merge_adds_two_words() {
        subject.addWord(VALID_WORD);

        final var otherWordCount = new WordCount();
        otherWordCount.addWord(VALID_WORD);
        otherWordCount.addWord(VALID_WORD);

        subject.mergeWith(otherWordCount);

        assertEquals(3, subject.getCount(VALID_WORD));
    }

    @Test
    public void of_creates_WordCount_with_one_word() {
        final var subject = WordCount.of(Stream.of(VALID_WORD));

        assertEquals(1, subject.getCount(VALID_WORD));
    }

    @Test
    public void of_creates_WordCount_with_two_word() {
        final var subject = WordCount.of(Stream.of(VALID_WORD, "some"));

        assertEquals(1, subject.getCount(VALID_WORD));
        assertEquals(1, subject.getCount("some"));
    }

    @Test
    public void of_handles_duplicates() {
        final var subject = WordCount.of(Stream.of(VALID_WORD, "some", VALID_WORD));

        assertEquals(2, subject.getCount(VALID_WORD));
        assertEquals(1, subject.getCount("some"));
    }

    @Test
    public void partitionByLeadingCharacter_handles_one_word() {
        subject.addWord(VALID_WORD);

        final var expectedWordCount = new WordCount();
        expectedWordCount.addWord(VALID_WORD);
        final var expected = Map.of('W', expectedWordCount);
        final var actual = subject.partitionByLeadingCharacter();

        assertEquals(expected, actual);
    }

    @Test
    public void partitionByLeadingCharacter_handles_two_words() {
        subject.addWord(VALID_WORD);
        subject.addWord(VALID_WORD);
        subject.addWord("other");

        final var expected = Map.of('W', WordCount.of(Stream.of(VALID_WORD, VALID_WORD)),
                'O', WordCount.of(Stream.of("other")));
        final var actual = subject.partitionByLeadingCharacter();

        assertEquals(expected, actual);
    }

    @Test
    public void toString_is_empty_for_empty_wordCount() {
        final var expected = "";
        final var actual = subject.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void toString_is_single_line_for_one_word() {
        subject.addWord(VALID_WORD);

        final var expected = "WORD: 1";
        final var actual = subject.toString();
        assertEquals(expected, actual);
    }

    @Test
    public void toString_is_two_lines_for_two_words() {
        subject.addWord(VALID_WORD);
        subject.addWord(VALID_WORD);
        subject.addWord("other");

        final var expected = Set.of("WORD: 2", "OTHER: 1");
        final var actual = Set.of(subject.toString().split("\n"));
        assertEquals(expected, actual);
    }

    @Test
    public void filterBy_can_exclude_words() {
        subject.addWord(VALID_WORD);
        subject.addWord(VALID_WORD);
        subject.addWord("other");

        final var filteredWordCount = subject.filterBy(new WordInExclusionListPredicate(VALID_WORD));
        assertEquals(2, filteredWordCount.getCount(VALID_WORD));
        assertEquals(0, filteredWordCount.getCount("other"));
    }
}
