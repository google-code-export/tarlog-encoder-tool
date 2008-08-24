package tarlog.encoder.tool.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputField {

    /**
     * name of field. By default name of field in class is used. Cannot be null
     * or empty string.
     */
    String name() default "";

    /**
     * order of fields
     */
    int order() default 0;

    /**
     * for text field declares of the field is multiline
     */
    boolean multiline() default false;
}
