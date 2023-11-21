package dk.tue;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class WordInExclusionListPredicateTest {
    WordInExclusionListPredicate subject = new WordInExclusionListPredicate("foo", "Bar");

    @Test
    public void test_is_true_if_word_is_equal_to_word_in_constructor() {
        assertTrue(subject.test("foo"));
    }

    @Test
    public void test_is_false_if_word_is_not_equal_to_word_in_constructor() {
        assertFalse(subject.test("uuu"));
    }

    @Test
    public void test_is_true_if_word_is_equal_to_word_in_constructor_ignoring_case() {
        assertTrue(subject.test("Foo"));
    }

    @Test
    public void test_is_true_if_word_is_equal_to_second_word_in_constructor_ignoring_case() {
        assertTrue(subject.test("BaR"));
    }
}
