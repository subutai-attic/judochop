package org.safehaus.perftest.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import com.google.inject.BindingAnnotation;
import static java.lang.annotation.RetentionPolicy.RUNTIME;
import static java.lang.annotation.ElementType.METHOD;
import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.ElementType.PARAMETER;

/**
 * Created with IntelliJ IDEA. User: akarasulu Date: 12/8/13 Time: 3:17 PM To change this template use File | Settings |
 * File Templates.
 */
@BindingAnnotation
@Target({ FIELD, PARAMETER, METHOD }) @Retention( RUNTIME )
public @interface DynStrProp {
}
