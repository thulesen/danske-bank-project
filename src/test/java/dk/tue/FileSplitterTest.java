package dk.tue;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class FileSplitterTest {
    private final List<String> expected = List.of("Lorem", "ipsum", "dolor", "sit", "amet");
    @TempDir
    Path tempDir;
    Path path;
    FileSplitter subject;

    @BeforeEach
    public void beforeEach() {
        path = tempDir.resolve("test");
        subject = new FileSplitter(path);
    }

    @Test
    public void words_splits_on_space() throws Exception {
        final var input = "Lorem ipsum dolor sit amet";
        Files.writeString(path, input);

        final Stream<String> actual = subject.words();
        assertEquals(expected, actual.toList());
    }

    @Test
    public void words_splits_on_commas_and_newlines() throws Exception {
        final var input = """
                Lorem,ipsum
                dolor sit
                amet
                """;
        Files.writeString(path, input);

        final Stream<String> actual = subject.words();
        assertEquals(expected, actual.toList());
    }

    @Test
    public void words_ignores_leading_space() throws Exception {
        final var input = "   Lorem ipsum dolor sit amet";
        Files.writeString(path, input);

        final Stream<String> actual = subject.words();
        assertEquals(expected, actual.toList());
    }

    @Test
    public void words_collapses_repeated_space() throws Exception {
        final var input = "Lorem    ipsum dolor sit amet";
        Files.writeString(path, input);

        final Stream<String> actual = subject.words();
        assertEquals(expected, actual.toList());
    }
}