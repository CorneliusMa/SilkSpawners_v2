package de.corneliusmay.silkspawners.wiring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the static holders a wired loader initializes during load. Components marked as
 * {@link Requires requiring} such a holder load after its initializer. Class retention is required
 * for incremental annotation processing.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Initializes {

    Class<?>[] value();
}
