package org.safehaus.chop.api;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/**
 * Annotate Jukito enabled test classes with this annotation to run time based stress tests.
 * The difference here is not the number of times your test runs but the amount of time for
 * which your tests should run.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface TimeChop {

    /**
     * The amount of time to run the tests.
     *
     * @return the amount of time in milliseconds to run tests
     */
    long time() default Constants.DEFAULT_TIME;


    /**
     * The number of threads to use for concurrent invocation per runner.
     *
     * @return the number of concurrent threads per runner
     */
    int threads() default Constants.DEFAULT_THREADS;


    /**
     * Whether or not to perform a saturation test before running this test and
     * use those discovered saturation parameters as overrides to the provided
     * parameters.
     *
     * @return whether or not to run a preliminary saturation test
     */
    boolean saturate() default Constants.DEFAULT_SATURATE;


    /**
     * This parameter allows you to introduce a delay between test iterations.
     *
     * @return the delay between test iterations in milliseconds
     */
    long delay() default Constants.DEFAULT_DELAY;
}
