package tarlog.encoder.tool.api.fields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * <p>
 * Specifies file fields in addition to the regular input fields. Putting this
 * annotation without the <tt>InputField</tt> annotation will do nothing.
 * <p>
 * It's possible to define the filter path and filter extensions for the dialog.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputFileField {

    String buttonText() default "...";

    /**
     * Set the file extensions which the file dialog will use to filter the
     * files it shows to the argument, which may be null.
     * <p>
     * The strings are platform specific. For example, on some platforms, an
     * extension filter string is typically of the form "*.extension", where
     * "*.*" matches all files. For filters with multiple extensions, use
     * semicolon as a separator, e.g. "*.jpg;*.png".
     * <p>
     * Relevant only for file dialogs.
     */
    String[] filterExtensions() default {};

    /**
     * Sets the names that describe the filter extensions which the dialog will
     * use to filter the files it shows to the argument, which may be null.
     * <p>
     * Each name is a user-friendly short description shown for its
     * corresponding filter. The <code>names</code> array must be the same
     * length as the <code>extensions</code> array.
     * 
     * <p>
     * Relevant only for file dialogs.
     */
    String[] filterNames() default {};

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

    /**
     * <p>Indicates either to return a full path or a relative path
     * <p>The path is relative to the filter path
     * 
     * @return
     */
    boolean relative() default false;
}
