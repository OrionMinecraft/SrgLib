package net.techcable.srglib.format;

import net.techcable.srglib.FieldData;
import net.techcable.srglib.JavaType;
import net.techcable.srglib.MethodData;
import net.techcable.srglib.MethodSignature;
import net.techcable.srglib.mappings.Mappings;
import net.techcable.srglib.mappings.MutableMappings;
import net.techcable.srglib.utils.Exceptions;
import net.techcable.srglib.utils.LineProcessor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.UncheckedIOException;

/* package */ class SrgMappingsFormat implements MappingsFormat {
    public static final SrgMappingsFormat INSTANCE = new SrgMappingsFormat();

    private SrgMappingsFormat() {
    }

    @Override
    public LineProcessor<Mappings> createLineProcessor() {
        return new SrgLineProcessor();
    }

    @Override
    public void write(Mappings mappings, Appendable output) throws IOException {
        try {
            mappings.forEachClass(Exceptions.sneakyThrowing((original, renamed) -> {
                output.append("CL: ");
                output.append(original.getInternalName());
                output.append(' ');
                output.append(renamed.getInternalName());
                output.append('\n');
            }));
            mappings.forEachField(Exceptions.sneakyThrowing((original, renamed) -> {
                output.append("FD: ");
                output.append(original.getInternalName());
                output.append(' ');
                output.append(renamed.getInternalName());
                output.append('\n');
            }));
            mappings.forEachMethod(Exceptions.sneakyThrowing((original, renamed) -> {
                output.append("MD: ");
                output.append(original.getInternalName());
                output.append(' ');
                output.append(original.getSignature().getDescriptor());
                output.append(' ');
                output.append(renamed.getInternalName());
                output.append(' ');
                output.append(renamed.getSignature().getDescriptor());
                output.append('\n');
            }));
        } catch (UncheckedIOException e) {
            throw e.getCause();
        }
    }

    /* package */ static class SrgLineProcessor implements LineProcessor<Mappings> {
        private final MutableMappings result = MutableMappings.create();

        @Override
        public boolean processLine(@NonNull String line) throws IOException {
            parseLine(line);
            return true;
        }

        public void parseLine(@NonNull String line) {
            line = line.trim(); // Strip whitespace
            if (line.startsWith("#") || line.isEmpty()) return;
            if(line.length() < 4) throw new IllegalArgumentException("Invalid line: " + line);
            String id = line.substring(0, 2);
            String[] args = line.substring(4).split(" ");
            final String originalInternalName, renamedInternalName;
            switch (id) {
                case "MD":
                    if(args.length != 4) throw new IllegalArgumentException("Invalid line: " + line);
                    originalInternalName = args[0];
                    MethodSignature originalSignature = MethodSignature.fromDescriptor(args[1]);
                    renamedInternalName = args[2];
                    MethodSignature renamedSignature = MethodSignature.fromDescriptor(args[3]);
                    MethodData originalMethodData = MethodData.fromInternalName(originalInternalName, originalSignature);
                    MethodData renamedMethodData = MethodData.fromInternalName(renamedInternalName, renamedSignature);
                    result.putMethod(originalMethodData, renamedMethodData);
                    return;
                case "FD":
                    if(args.length != 2) throw new IllegalArgumentException("Invalid line: " + line);
                    originalInternalName = args[0];
                    renamedInternalName = args[1];
                    FieldData originalFieldData = FieldData.fromInternalName(originalInternalName);
                    FieldData renamedFieldData = FieldData.fromInternalName(renamedInternalName);
                    result.putField(originalFieldData, renamedFieldData);
                    return;
                case "CL":
                    if(args.length != 2) throw new IllegalArgumentException("Invalid line: " + line);
                    originalInternalName = args[0];
                    renamedInternalName = args[1];
                    JavaType originalType = JavaType.fromInternalName(originalInternalName);
                    JavaType renamedType = JavaType.fromInternalName(renamedInternalName);
                    result.putClass(originalType, renamedType);
                    return;
                case "PK":
                    return; // Ignore packages, because they are stupid
                default:
                    throw new IllegalArgumentException("Invalid line: " + line);
            }
        }

        @Override
        public Mappings getResult() {
            return result;
        }
    }
}
