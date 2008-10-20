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
            if (element instanceof EncoderClassWrapper) {
                EncoderClassWrapper classWrapper = (EncoderClassWrapper) element;
                return "Class: " + classWrapper.className;
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
