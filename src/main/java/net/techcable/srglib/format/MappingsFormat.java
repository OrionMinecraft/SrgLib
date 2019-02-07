package net.techcable.srglib.format;

import net.techcable.srglib.mappings.Mappings;
import net.techcable.srglib.utils.Exceptions;
import net.techcable.srglib.utils.LineProcessor;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static net.techcable.srglib.utils.Exceptions.sneakyThrowing;

/**
 * A format for serializing mappings to and from text.
 */
public interface MappingsFormat {
    MappingsFormat SEARGE_FORMAT = SrgMappingsFormat.INSTANCE;
    MappingsFormat COMPACT_SEARGE_FORMAT = CompactSrgMappingsFormat.INSTANCE;

    default Mappings parse(BufferedReader readable) throws IOException {
        LineProcessor<Mappings> lineProcessor = createLineProcessor();
        String line;
        while ((line = readable.readLine()) != null) {
            if (!lineProcessor.processLine(line)) {
                break;
            }
        }
        return lineProcessor.getResult();
    }

    default Mappings parseFile(File file) throws IOException {
        return parseFile(file.toPath());
    }

    default Mappings parseFile(Path path) throws IOException {
        try (BufferedReader in = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            return parse(in);
        }
    }

    default Mappings parseLines(String... lines) {
        return parseLines(Arrays.asList(lines));
    }

    default Mappings parseLines(Iterable<String> lines) {
        return parseLines(lines.iterator());
    }

    default Mappings parseLines(Iterator<String> lines) {
        LineProcessor<Mappings> lineProcessor = createLineProcessor();
        lines.forEachRemaining(Exceptions.sneakyThrowing(lineProcessor::processLine));
        return lineProcessor.getResult();
    }

    LineProcessor<Mappings> createLineProcessor();

    void write(Mappings mappings, Appendable output) throws IOException;

    default void writeToFile(Mappings mappings, File file) throws IOException {
        writeToFile(mappings, file.toPath());
    }

    default void writeToFile(Mappings mappings, Path path) throws IOException {
        try (Writer out = Files.newBufferedWriter(path, StandardOpenOption.WRITE)) {
            write(mappings, out);
        }
    }

    default List<String> toLines(Mappings mappings) {
        StringWriter result = new StringWriter();
        return sneakyThrowing(() -> {
            this.write(mappings, result);
            return Stream.of(result.toString().split("\n")).collect(Collectors.toList());
        }).get();
    }
}
