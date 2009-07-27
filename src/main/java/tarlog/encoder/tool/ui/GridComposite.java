/*******************************************************************************
 *     Copyright 2009 Michael Elman (http://tarlogonjava.blogspot.com)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *     
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 *******************************************************************************/
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
