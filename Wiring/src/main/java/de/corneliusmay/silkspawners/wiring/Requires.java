package de.corneliusmay.silkspawners.wiring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares the static holders a wired component reads, ordering it after the loader that
 * {@link Initializes initializes} them. Class retention is required for incremental annotation
 * processing.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Requires {

    Class<?>[] value();
}
