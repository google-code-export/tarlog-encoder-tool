package tarlog.encoder.tool.api.fields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface InputListField {

    @SuppressWarnings("unchecked")
    Class<? extends ListConverter> converter() default ListConverter.class;

    InputType[] inputType() default InputType.STRING;

    /**
     * <p>
     * STRING - String input will be added
     * <p>
     * PROVIDED - The input should be specified using additional annotation.
     * Using the property will do nothing, if any additional input selected.
     * <p>
     * UP - UP button will be added
     * <p>
     * DOWN - DOWN button will be added
     */
    public enum InputType {
        STRING, PROVIDED, UP, DOWN
    }

}
