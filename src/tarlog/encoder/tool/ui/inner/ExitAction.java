package tarlog.encoder.tool.ui.inner;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.window.Window;

public class ExitAction extends Action {

    private final Window window;

    public ExitAction(Window window) {
        super("Exit");
        this.window = window;
    }

    @Override
    public void run() {
        window.close();
    }

}
