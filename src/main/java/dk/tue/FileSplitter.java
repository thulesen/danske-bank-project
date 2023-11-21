package dk.tue;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public class FileSplitter {
    final static Pattern pattern = Pattern.compile("\\W+");
    final Path path;

    public FileSplitter(final Path path) {
        this.path = path;
    }

    public Stream<String> words() {
        final Scanner scanner;
        try {
            scanner = new Scanner(path).useDelimiter(pattern);
        } catch (final IOException e) {
            throw new RuntimeException("File at path %s could not be read.".formatted(path), e);
        }
        return scanner.tokens();
    }
}
