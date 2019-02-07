package net.techcable.srglib.mappings;

import net.techcable.srglib.FieldData;
import net.techcable.srglib.JavaType;
import net.techcable.srglib.MethodData;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Collections;
import java.util.Set;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/* package */ final class RenamingMappings implements Mappings {
    private final UnaryOperator<JavaType> typeTransformer;
    private final Function<MethodData, String> methodRenamer;
    private final Function<FieldData, String> fieldRenamer;
    public RenamingMappings(
            @Nullable UnaryOperator<JavaType> typeTransformer,
            @Nullable Function<MethodData, String> methodRenamer,
            @Nullable Function<FieldData, String> fieldRenamer
    ) {
        this.typeTransformer = typeTransformer != null ? typeTransformer : UnaryOperator.identity();
        this.methodRenamer = methodRenamer != null ? methodRenamer : MethodData::getName;
        this.fieldRenamer = methodRenamer != null ? fieldRenamer : FieldData::getName;
    }

    @Override
    public JavaType getNewClass(JavaType original) {
        if(!original.isReferenceType()) throw new IllegalArgumentException("Type isn't a reference type: " + original);
        JavaType result = typeTransformer.apply(original);
        return result == null ? original : result;
    }

    @Override
    public MethodData getNewMethod(MethodData original) {
        return original
                .mapTypes(this::getNewType)
                .withName(methodRenamer.apply(original));
    }

    @Override
    public FieldData getNewField(FieldData original) {
        return original
                .mapTypes(this::getNewType)
                .withName(fieldRenamer.apply(original));
    }

    @Override
    public Set<JavaType> classes() {
        return Collections.emptySet();
    }

    @Override
    public Set<MethodData> methods() {
        return Collections.emptySet();
    }

    @Override
    public Set<FieldData> fields() {
        return Collections.emptySet();
    }

    @Override
    public int hashCode() {
        return typeTransformer.hashCode() ^ methodRenamer.hashCode() ^ fieldRenamer.hashCode();
    }

    @Override
    public Mappings inverted() {
        throw new UnsupportedOperationException(); // Doesn't make much sense
    }

    @Override
    public ImmutableMappings snapshot() {
        throw new UnsupportedOperationException(); // Doesn't make much sense
    }

    @Override
    public boolean equals(Object obj) {
        return this == obj || obj.getClass() == RenamingMappings.class
                && ((RenamingMappings) obj).typeTransformer.equals(this.typeTransformer)
                && ((RenamingMappings) obj).methodRenamer.equals(this.methodRenamer)
                && ((RenamingMappings) obj).fieldRenamer.equals(this.fieldRenamer);
    }
}
