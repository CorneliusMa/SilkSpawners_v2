package de.corneliusmay.silkspawners.wiring;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks the single class that drives the wiring at runtime. The component registry is generated
 * into its package, and its constructor parameters are available to every component. Class
 * retention is required for incremental annotation processing.
 */
@Retention(RetentionPolicy.CLASS)
@Target(ElementType.TYPE)
public @interface Registry {}
