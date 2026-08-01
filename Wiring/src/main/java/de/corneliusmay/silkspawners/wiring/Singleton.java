package de.corneliusmay.silkspawners.wiring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a wired component as created once during load and injected by type from then on, where a
 * plain component is created fresh for every injection. Class retention is required for incremental
 * annotation processing.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Singleton {}
