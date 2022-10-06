package com.nmd.utility.viewbinder;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by Pham Hai QUANG on 9/12/2016.
 */

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Attrs {
    int DIMENSION = 0;
    int COLOR = 1;
    int FLOAT = 2;
    int INTEGER = 3;
    int BOOLEAN = 4;

    int index() default -1;
    int type() default -1;
    String defVal() default "";
}
