package tarlog.encoder.tool.api.fields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Specifies directory fields in addition to the regular input fields. Putting this
 * annotation without the <tt>InputField</tt> annotation will do nothing.
 * <p>
 * It's possible to define the filter path for the dialog.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputDirectoryField {

    /**
     * <p>
     * Sets the directory path that the dialog will use to the argument, which
     * may be null. File names in this path will appear in the dialog, filtered
     * according to the filter extensions. If the string is null, then the
     * operating system's default filter path will be used.
     * <p>
     * Note that the path string is platform dependent. For convenience, either
     * '/' or '\' can be used as a path separator.
     */
    String filterPath() default "";

    
}
