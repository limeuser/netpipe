package netpipe.pipe;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Task {
    String name() default "";
    String parent() default "";
    String[] in() default {};
    String[] out() default {};
    int cpu() default 0;
    int memory() default 0;
    int count() default 0;
}
