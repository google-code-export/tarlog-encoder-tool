package tarlog.encoder.tool.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;

public class GridComposite extends Composite {

    public GridComposite(Composite parent) {
        this(parent, SWT.NONE);
    }
    
    public GridComposite(Composite parent, int style) {
        this(parent, style, 1);
    }

    public GridComposite(Composite parent, int style, int numColumns) {
        this(parent, style, numColumns, false);
    }

    public GridComposite(Composite parent, int style, int numColumns,
        boolean makeColumnsEqualWidth) {
        super(parent, style);
        setLayout(new GridLayout(numColumns, makeColumnsEqualWidth));
    }

    /**
     * method is overridden in order to ensure that none tries to override it again
     */
    @Override
    public final void setLayout(Layout layout) {
        super.setLayout(layout);
    }
    
    @Override
    public final GridLayout getLayout() {
        return (GridLayout) super.getLayout();
    }
    
    public final void removeMargins() {
        GridLayout layout = getLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
    }

}
