package tarlog.encoder.tool.views;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import tarlog.encoder.tool.ui.EncoderUI;

public class EncoderView extends ViewPart {

    private EncoderUI encoderUI;

    /**
     * The constructor.
     */
    public EncoderView() {
    }

    /**
     * This is a callback that will allow us to create the viewer and initialize
     * it.
     */
    public void createPartControl(Composite parent) {
        encoderUI = new EncoderUI(parent, false);
    }

    /**
     * Passing the focus request to the viewer's control.
     */
    public void setFocus() {
        encoderUI.setFocus();
    }
}