package tarlog.encoder.tool.api.fields;

/**
 * The class should implement this interface in order to make the input
 * validated.
 */
public interface Validator {

    /**
     * @return null if valid or String with error message otherwise.
     */
    String isValid();

}
