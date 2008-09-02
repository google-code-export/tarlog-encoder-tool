package tarlog.encoder.tool.eclipse.preferences;

import java.io.IOException;
import java.util.Map;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;

import tarlog.encoder.tool.eclipse.Activator;

/**
 * This class represents a preference page that is contributed to the
 * Preferences dialog. By subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows us to create a page
 * that is small and knows how to save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They are stored in the
 * preference store that belongs to the main plug-in class. That way,
 * preferences can be accessed directly via the preference store.
 */

public class EncoderToolPreferencePage extends PreferencePage implements
    IWorkbenchPreferencePage {

    public EncoderToolPreferencePage() {
        this(Activator.getDefault().getPreferenceStore());
    }

    public EncoderToolPreferencePage(String string) throws IOException {
        this(new PreferenceStore(string));
    }

    private EncoderToolPreferencePage(IPreferenceStore preferencePage) {
        setPreferenceStore(preferencePage);
        setDescription("A demonstration of a preference page implementation");
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
     */
    public void init(IWorkbench workbench) {
    }

    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.BORDER);
        composite.setLayout(new GridLayout());
        composite.setLayoutData(new GridData(SWT.CENTER, SWT.CENTER, true, true));
        TreeViewer treeViewer = new TreeViewer(composite);

        return composite;
    }

}