package net.techcable.srglib.mappings;

import net.techcable.srglib.FieldData;
import net.techcable.srglib.JavaType;
import net.techcable.srglib.MethodData;

import java.util.Map;
import java.util.Set;
import java.util.function.BiConsumer;

import static java.util.Objects.requireNonNull;

/* package */ class SimpleMappings implements MutableMappings {
    private final Map<JavaType, JavaType> classes;
    private final Map<MethodData, String> methodNames;
    private final Map<FieldData, String> fieldNames;

    /* package */ SimpleMappings(
            Map<JavaType, JavaType> classes,
            Map<MethodData, String> methodNames,
            Map<FieldData, String> fieldNames
    ) {
        this.classes = requireNonNull(classes, "Null types");
        this.methodNames = requireNonNull(methodNames, "Null methods");
        this.fieldNames = requireNonNull(fieldNames, "Null fields");
    }

    @Override
    public void putClass(JavaType original, JavaType renamed) {
        if(!original.isReferenceType()) throw new IllegalArgumentException("Original type isn't a reference type: " + original);
        if(!renamed.isReferenceType()) throw new IllegalArgumentException("Renamed type isn't a reference type: " + renamed);
        if (original.equals(renamed)) {
            classes.remove(original);
        } else {
            classes.put(original, renamed);
        }
    }

    @Override
    public void putMethod(MethodData original, String newName) {
        methodNames.put(requireNonNull(original, "Null original"), requireNonNull(newName, "Null newName"));
    }

    @Override
    public void putField(FieldData original, String newName) {
        fieldNames.put(requireNonNull(original, "Null original"), requireNonNull(newName, "Null newName"));
    }

    @Override
    public JavaType getNewClass(JavaType original) {
        if(!original.isReferenceType()) throw new IllegalArgumentException("Type isn't a reference type: " + original);
        return classes.getOrDefault(requireNonNull(original), original);
    }

    @Override
    public MethodData getNewMethod(MethodData original) {
        String newName = methodNames.getOrDefault(original, original.getName());
        return original.mapTypes(this::getNewType).withName(newName);
    }

    @Override
    public FieldData getNewField(FieldData original) {
        String newName = fieldNames.getOrDefault(original, original.getName());
        return FieldData.create(getNewType(original.getDeclaringType()), newName);
    }

    @Override
    public ImmutableMappings snapshot() {
        return ImmutableMappings.copyOf(
                this.classes,
                this.methodNames,
                this.fieldNames
        );
    }

    @Override
    public Set<JavaType> classes() {
        return classes.keySet();
    }

    @Override
    public Set<MethodData> methods() {
        return methodNames.keySet();
    }

    @Override
    public Set<FieldData> fields() {
        return fieldNames.keySet();
    }

    @Override
    public Mappings inverted() {
        return snapshot().inverted();
    }

    @Override
    public void forEachClass(BiConsumer<JavaType, JavaType> action) {
        classes.forEach(action);
    }

    @Override
    public void forEachMethod(BiConsumer<MethodData, MethodData> action) {
        methodNames.forEach((originalData, newName) -> {
            MethodData newData = originalData.mapTypes(this::getNewType).withName(newName);
            action.accept(originalData, newData);
        });
    }

    @Override
    public void forEachField(BiConsumer<FieldData, FieldData> action) {
        fieldNames.forEach((originalData, newName) -> {
            FieldData newData = FieldData.create(getNewType(originalData.getDeclaringType()), newName);
            action.accept(originalData, newData);
        });
    }

    @Override
    public boolean equals(Object otherObj) {
        if (this == otherObj) return true;
        if (otherObj == null) return false;
        if (otherObj.getClass() == SimpleMappings.class) {
            SimpleMappings other = (SimpleMappings) otherObj;
            return classes.equals(other.classes) && methodNames.equals(other.methodNames) && fieldNames.equals(other.fieldNames);
        } else if (otherObj instanceof Mappings) {
            return this.snapshot().equals(((Mappings) otherObj).snapshot());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        int result = classes.hashCode();
        result = 31 * result + methodNames.hashCode();
        result = 31 * result + fieldNames.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return snapshot().toString();
    }
}
