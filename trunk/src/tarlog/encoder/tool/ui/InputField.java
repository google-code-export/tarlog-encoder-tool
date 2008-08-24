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
     * indicates if the field is read only
     */
    boolean readonly() default false;

    /**
     * <p>
     * For text field declares if the field is multiline
     * <p>
     * Irrelevant for non-text fields
     */
    boolean multiline() default false;

    /**
     * <p>
     * For text field declares if the field is password, which cause to replace
     * characters with the stars.
     * <p>
     * Irrelevant for non-text fields
     */
    boolean password() default false;
}
