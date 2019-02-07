package net.techcable.srglib.format;

import net.techcable.srglib.mappings.Mappings;
import net.techcable.srglib.utils.LineProcessor;

import java.io.IOException;

/**
 * @author Mark Vainomaa
 */
/* package */ class TinyMappingsFormat implements MappingsFormat {
    public static final TinyMappingsFormat INSTANCE = new TinyMappingsFormat();

    private TinyMappingsFormat() {}

    @Override
    public LineProcessor<Mappings> createLineProcessor() {
        throw new UnsupportedOperationException("Not done yet.");
    }

    @Override
    public void write(Mappings mappings, Appendable output) throws IOException {
        throw new UnsupportedOperationException("Not done yet.");
    }
}
