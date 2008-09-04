package tarlog.encoder.tool.api.fields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Specifies text fields in addition to the regular input fields. Putting this
 * annotation without the <tt>InputField</tt> annotation will do nothing.
 * <p>
 * Text fields can be of the following types:
 * <ul>
 * <li>Single line text.</li>
 * <li>Multi-line text.</li>
 * <li>Combo box with valid values.</li>
 * </ul>
 * <p>
 * In addition it's possible to declare text field as password, which will cause
 * to replace input characters with the stars.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputTextField {

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

    /**
     * <p>
     * Values that will be added to combo. If specified, multiline and password
     * will be ignored and combo will be placed instead of the text box.
     * <p>
     * In order to force the user to use only these values, set
     * <tt>readonly</tt> attribute on the <tt>InputField</tt> annotation to
     * true.
     */
    String[] values() default {};

}
