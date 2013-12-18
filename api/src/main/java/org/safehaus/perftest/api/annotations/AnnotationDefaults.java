package org.safehaus.perftest.api.annotations;


/**
 * The defaults to use for annotation settings.
 */
public interface AnnotationDefaults {
    public static final long DEFAULT_ITERATIONS = 1000L;
    public static final long DEFAULT_TIME = 30000L;
    public static final int DEFAULT_THREADS = 10;
    public static final int DEFAULT_RUNNERS = 6;
    public static final boolean DEFAULT_SATURATE = false;
    public static final long DEFAULT_DELAY = 0;
}
