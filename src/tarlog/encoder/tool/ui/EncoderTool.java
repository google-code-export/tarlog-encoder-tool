package tarlog.encoder.tool.ui;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.IPreferenceNode;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.jface.preference.PreferenceManager;
import org.eclipse.jface.preference.PreferenceNode;
import org.eclipse.jface.window.ApplicationWindow;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

import tarlog.encoder.tool.eclipse.preferences.EncoderToolPreferencePage;
import tarlog.encoder.tool.ui.inner.ExitAction;
import tarlog.ui.swt.ddialog.utils.Utils;

/**
 *
 */
public class EncoderTool extends ApplicationWindow {

    static final String         ENCODER_PROPERTIES = "encoder.properties";
    private static final String VERSION            = "0.5.0";
    private static final String TITLE              = "Diagnostic Tool";

    private Shell               shell;
    private boolean             standalone         = false;
    private EncoderUI           encoderUI;

    public EncoderTool() {
        super(null);
    }

    public void init() throws IOException {
        setBlockOnOpen(true);
        if (standalone) {
            addMenuBar();
        }
        open();
    }

    private Image getImage() {
        try {
            InputStream img = Utils.getFile("icons/encoders.jpg");
            ImageLoader imageLoader = new ImageLoader();
            ImageData[] load = imageLoader.load(img);
            return new Image(shell.getDisplay(), load[0]);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    @Override
    protected MenuManager createMenuManager() {
        MenuManager menuManager = super.createMenuManager();

        // create menu only when stand alone
        MenuManager fileMenuManager = new MenuManager("&File");
        fileMenuManager.add(new OpenPreferencesAction());
        fileMenuManager.add(new ExitAction(this));
        menuManager.add(fileMenuManager);

        //        SaveAction saveAction = new SaveAction();
        //        saveAction.setAccelerator(SWT.CTRL | 'S');
        //        fileMenuManager.add(saveAction);
        //        LoadAction loadAction = new LoadAction();
        //        loadAction.setAccelerator(SWT.CTRL | 'O');
        //        fileMenuManager.add(loadAction);
        //        menuManager.add(fileMenuManager);

        return menuManager;
    }

    private class OpenPreferencesAction extends Action {

        public OpenPreferencesAction() {
            super("Preferences");
        }

        @Override
        public void run() {
            try {
                EncoderToolPreferencePage page = new EncoderToolPreferencePage(ENCODER_PROPERTIES);
                page.setTitle(TITLE);
                PreferenceManager mgr = new PreferenceManager();
                IPreferenceNode node = new PreferenceNode("1", page);
                mgr.addToRoot(node);
                PreferenceDialog dialog = new PreferenceDialog(shell, mgr);
                dialog.create();
                dialog.setMessage(page.getTitle());
                int rc = dialog.open();
                if (rc == Dialog.OK) {
                    encoderUI.reload();
                }
            } catch (IOException e) {
                Utils.showException(shell, e);
            }
        }
    }

    @Override
    protected Control createContents(Composite parent) {
        shell = getShell();
        shell.setText("Encoder Tool " + VERSION);
        shell.setSize(1000, 700);
        Image image = getImage();
        if (image != null) {
            shell.setImage(image);
        }
        encoderUI = new EncoderUI(shell, standalone);
        return shell;
    }

    //    private void getVersion() {
    //        String className = ManifestReader.class.getSimpleName();
    //
    //        String classFileName = className + ".class";
    //
    //
    //        String pathToThisClass = ManifestReader.class.getResource(classFileName).toString();
    //
    //        int mark = pathToThisClass.indexOf("!");
    //
    //        String pathToManifest = pathToThisClass.substring(0, mark + 1);
    //
    //        pathToManifest += "/META-INF/MANIFEST.MF";
    //
    //        Manifest manifest = new Manifest(new URL(pathToManifest).openStream());
    //    }

    /**
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        EncoderTool encoderTool = new EncoderTool();
        encoderTool.standalone = true;
        encoderTool.init();
        Display.getCurrent().dispose();
    }

}
