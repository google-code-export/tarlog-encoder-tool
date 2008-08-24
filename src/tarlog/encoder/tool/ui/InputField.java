package tarlog.encoder.tool.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies that this field is actually input field. Meaning that input dialog
 * would be opened in order to populate this field.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputField {

    /**
     * name of field. By default name of field in class is used. Cannot be null
     * or empty string.
     */
    String name() default "";

    /**
     * order of fields in the input dialog. The fields are sorted in the ascending
     * order.
     */
    int order() default 0;

    /**
     * indicates if the field is read only
     */
    boolean readonly() default false;

    /**
     * indicates if the field is enabled
     * 
     * @return
     */
    boolean enabled() default true;
}
