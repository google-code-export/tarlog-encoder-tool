package tarlog.encoder.tool.api.fields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Specifies that this field is actually input field. Meaning that input dialog
 * would be opened in order to populate this field.
 * <p>
 * Supported types:
 * <ul>
 * <li><b>String</b> - input of text. See <tt>TextField</tt> for more details.</li>
 * <li><b>Boolean</b> - input of boolean value. Displays a check box.</li>
 * <li><b>Enum</b> - input of enum value. Displays a combo box.</li>
 * <li><b>Integer</b> - input of integer value. Displays a spinner.</li>
 * <li><b>File</b> - input of a file or a directory. See <tt>FileField</tt> for
 * more details.</li>
 * </ul>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputField {

    /**
     * name of field. By default name of the field in a class is used. Cannot be
     * null or empty string.
     */
    String name() default "";

    /**
     * order of fields in the input dialog. The fields are sorted in the
     * ascending order.
     */
    int order() default 0;

    /**
     * indicates if the field is read only
     */
    boolean readonly() default false;

    /**
     * indicates if the field is enabled
     */
    boolean enabled() default true;

    /**
     * Indicates if the field is required. Required field cannot be null.
     */
    boolean required() default false;
}
