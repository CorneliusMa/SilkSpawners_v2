package de.corneliusmay.silkspawners.wiring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a class as created through the plugin loader. The wiring processor validates the
 * constructor contract at compile time. Class retention is required for incremental annotation
 * processing.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Wired {}
