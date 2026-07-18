package de.corneliusmay.silkspawners.wiring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a getter on a wired loader whose return value becomes available as a dependency once
 * the loader has loaded. Class retention is required for incremental annotation processing.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.METHOD)
public @interface Provides {}
