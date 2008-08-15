package tarlog.encoder.tool.eclipse.handlers;

import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import tarlog.encodertool.ui.EncoderTool;

/**
 * Our sample handler extends AbstractHandler, an IHandler base class.
 * 
 * @see org.eclipse.core.commands.IHandler
 * @see org.eclipse.core.commands.AbstractHandler
 */
public class Handler extends AbstractHandler {

    /**
     * The constructor.
     */
    public Handler() {
    }

    /**
     * the command has been executed, so extract extract the needed information
     * from the application context.
     */
    public Object execute(ExecutionEvent event) throws ExecutionException {
//        IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
        EncoderTool encoderTool = new EncoderTool();
        try {
            encoderTool.init();
        } catch (IOException e) {
            throw new ExecutionException(e.getMessage(), e);
        }
        return null;
    }
}
