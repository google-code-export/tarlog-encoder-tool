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
package tarlog.encoder.tool.eclipse.preferences;

import java.net.URL;

import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.swt.graphics.Image;

import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncodersGroup;

class LabelProvider implements ITableLabelProvider {

    public Image getColumnImage(Object arg0, int arg1) {
        return null;
    }

    public String getColumnText(Object element, int index) {
        if (element == null) {
            return null;
        }

        if (index == 0) {
            if (element instanceof String) {
                return (String) element;
            }

            if (element instanceof URL) {
                return element.toString();
            }
            if (element instanceof StringWrapper) {
                StringWrapper stringWrapper = (StringWrapper) element;
                return String.format("%s: %s", stringWrapper.title,
                    stringWrapper.string);
            }

            if (element instanceof EncoderClasspathWrapper) {
                return "Classpath:";
            }
        }
        if (element instanceof EncodersGroup) {
            EncodersGroup encodersGroup = (EncodersGroup) element;
            switch (index) {
                case 0:
                    return "Group: " + encodersGroup.getGroupName();
                case 1:
                    return String.valueOf(encodersGroup.isEnabled());
            }
        }
        if (element instanceof EncoderDef) {
            EncoderDef encoderDef = (EncoderDef) element;
            switch (index) {
                case 0:
                    return "Encoder: " + encoderDef.name;
                case 1:
                    return String.valueOf(encoderDef.isEnabled());
            }
        }

        return null;
    }

    public void addListener(ILabelProviderListener arg0) {

    }

    public void dispose() {

    }

    public boolean isLabelProperty(Object arg0, String arg1) {
        return false;
    }

    public void removeListener(ILabelProviderListener arg0) {

    }

}
