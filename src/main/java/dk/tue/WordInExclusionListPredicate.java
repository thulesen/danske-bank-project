package dk.tue;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class WordInExclusionListPredicate implements Predicate<String> {
    final Set<String> exclusionSet;

    public WordInExclusionListPredicate(final String... exclusionList) {
        exclusionSet = Stream.of(exclusionList).map(String::toUpperCase).collect(Collectors.toSet());
    }

    @Override
    public boolean test(final String s) {
        return exclusionSet.contains(s.toUpperCase());
    }
}
