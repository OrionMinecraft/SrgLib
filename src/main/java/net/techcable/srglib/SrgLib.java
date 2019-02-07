package net.techcable.srglib;

import net.techcable.srglib.mappings.Mappings;

/**
 * Static utility methods for handling srg.
 */
public final class SrgLib {
    private SrgLib() {}

    /**
     * Checks if the given name is a valid java identifier
     *
     * Java identifiers are used for field or method names
     *
     * @param name the name to check
     */
    public static boolean isValidIdentifier(String name) {
        if(name.isEmpty()) throw new IllegalArgumentException("Empty name: " + name);
        return Character.isJavaIdentifierStart(name.codePointAt(0)) && name.codePoints()
                .skip(1) // Skip the first char, since we already checked it
                .allMatch(Character::isJavaIdentifierPart);
    }

    /**
     * Checks that all fields and methods in the mappings have the correct type information
     *
     * @param mappings the mappings to check
     */
    public static void checkConsistency(Mappings mappings) {
        mappings.forEachField((originalField, renamedField) -> {
            if(!originalField.mapTypes(mappings::getNewType).hasSameTypes(renamedField))
                throw new IllegalArgumentException("Remapped field data (" + originalField + ") doesn't correspond to original types (" + renamedField + ")");
        });
        mappings.forEachMethod((originalMethod, renamedMethod) -> {
            if(!originalMethod.mapTypes(mappings::getNewType).hasSameTypes(renamedMethod))
                throw new IllegalArgumentException("Remapped method data (" + originalMethod + ") doesn't correspond to original types (" + renamedMethod + ")");
        });
    }
}
