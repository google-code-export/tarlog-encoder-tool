package tarlog.encoder.tool.api.fields;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FileField {

    /**
     * type of the file field. Can be either file or directory 
     */
    FileFieldType fileFieldType() default FileFieldType.file;
    
    public enum FileFieldType {file, directory}
}
