package dk.tue;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.function.Predicate;

public class Main {
    public static void main(final String[] args) {
        if (args.length != 2) throw new IllegalArgumentException("Program requires exactly two arguments.");

        final var inputDirectory = Path.of(args[0]);
        if (!Files.isDirectory(inputDirectory))
            throw new IllegalArgumentException("The first argument must be a path to a directory.");

        final var exclusionFile = Path.of(args[1]);
        if (!Files.isRegularFile(exclusionFile))
            throw new IllegalArgumentException("The second argument must be a path to a regular file.");

        final var outputDirectoryPath = Path.of("output");
        if (!Files.exists(outputDirectoryPath))
            try {
                Files.createDirectory(outputDirectoryPath);
            } catch (final IOException e) {
                throw new RuntimeException("Unable to create output directory.", e);
            }

        final Predicate<String> exclusionPredicate;
        try {
            final var exclusionFileScanner = new Scanner(exclusionFile).useDelimiter("\r\n");
            final var exclusionList = exclusionFileScanner.tokens().toArray(String[]::new);
            exclusionPredicate = new WordInExclusionListPredicate(exclusionList);

        } catch (final IOException e) {
            throw new RuntimeException("The exclusion file at %s could not be read.".formatted(exclusionFile), e);
        }

        try (final var inputPaths = Files.list(inputDirectory)) {
            final WordCount totalWordCount = inputPaths.map(FileSplitter::new)
                                                       .map(FileSplitter::words)
                                                       .map(WordCount::of)
                                                       .reduce(WordCount::mergeWith)
                                                       .get();

            totalWordCount.filterBy(exclusionPredicate.negate())
                          .partitionByLeadingCharacter()
                          .forEach((key, value) -> {
                              final var outputPath = Path.of("output/output" + key);
                              try {
                                  Files.writeString(outputPath, value.toString());
                              } catch (final IOException e) {
                                  throw new RuntimeException("Unable to write to file %s.".formatted(
                                          outputPath), e);
                              }
                          });

            final var excludedWordsOutputPath = Path.of("output/excluded-words");
            final var excludedWordCount = totalWordCount.filterBy(exclusionPredicate);
            try {
                Files.writeString(excludedWordsOutputPath, excludedWordCount.toString());
            } catch (final IOException e) {
                throw new RuntimeException("Unable to write to file %s.".formatted(excludedWordsOutputPath), e);
            }
        } catch (final IOException e) {
            throw new RuntimeException("There was an I/O exception opening the input directory.", e);
        }
    }
}