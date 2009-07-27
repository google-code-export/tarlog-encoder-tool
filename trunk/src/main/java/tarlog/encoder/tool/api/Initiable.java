package tarlog.encoder.tool.api;

/**
 * Marks encoder as initiable. The arrow button will appear next to the
 * encoder's name. Method init will be called each time the used presses the
 * arrow button.
 */
public interface Initiable {

    void init();
}
