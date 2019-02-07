package net.techcable.srglib.utils;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;

/**
 * @author Mark Vainomaa
 */
public interface LineProcessor<T> {
    boolean processLine(@NonNull String line) throws IOException;

    T getResult();
}
