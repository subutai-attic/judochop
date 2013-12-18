package org.safehaus.perftest.api.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotate Jukito enabled test classes with this annotation to run iteration based stress tests.
 * The difference here is not the amount of time your test runs but the number of iterations of
 * your tests.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface IterationChop {


    /**
     * The number of times (iterations) your tests should be invoked.
     *
     * @return the number of times your tests should be invoked
     */
    long iterations() default AnnotationDefaults.DEFAULT_ITERATIONS;


    /**
     * The number of threads to use for concurrent invocation per runner.
     *
     * @return the number of concurrent threads per runner
     */
    int threads() default AnnotationDefaults.DEFAULT_THREADS;


    /**
     * The number of distributed runners to use for the chop.
     *
     * @return the number of distributed runners to use
     */
    int runners() default AnnotationDefaults.DEFAULT_RUNNERS;


    /**
     * Whether or not to perform a saturation test before running this test and
     * use those discovered saturation parameters as overrides to the provided
     * parameters.
     *
     * @return whether or not to run a preliminary saturation test
     */
    boolean saturate() default AnnotationDefaults.DEFAULT_SATURATE;


    /**
     * This parameter allows you to introduce a delay between test iterations.
     *
     * @return the delay between test iterations in milliseconds
     */
    long delay() default AnnotationDefaults.DEFAULT_DELAY;
}
