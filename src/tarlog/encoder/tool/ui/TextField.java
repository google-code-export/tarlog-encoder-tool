package tarlog.encoder.tool.ui;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Specifies text fields in addition to the regular input fields. Should be used
 * only together with the <tt>InputField</tt> annotation.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface TextField {

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
     * will be ignored and combo will be placed instead of text box.
     * <p>
     * In order to force the user to use only these values, set
     * <tt>readonly</tt> attribute on the <tt>InputField</tt> annotation to
     * true.
     */
    String[] values() default {};

}
