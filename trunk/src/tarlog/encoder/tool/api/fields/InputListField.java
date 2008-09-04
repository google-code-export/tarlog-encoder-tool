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

    public enum InputType {
        STRING, FILE, FOLDER
    }

}
