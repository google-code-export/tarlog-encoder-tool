package tarlog.encoder.tool.eclipse.preferences;

import java.net.URL;

import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncodersGroup;
import tarlog.encoder.tool.ui.AbstractLabelProvider;

class LabelProvider extends AbstractLabelProvider {

    @Override
    public String getText(Object element) {
        if (element == null) {
            return null;
        }
        if (element instanceof String) {
            return (String) element;
        }
        if (element instanceof URL) {
            return element.toString();
        }
        if (element instanceof EncodersGroup) {
            EncodersGroup encodersGroup = (EncodersGroup) element;
            return "Group: " + encodersGroup.getGroupName();
        }
        if (element instanceof EncoderDef) {
            EncoderDef encoderDef = (EncoderDef) element;
            return "Encoder: " + encoderDef.name;
        }

        if (element instanceof EncoderClassWrapper) {
            EncoderClassWrapper classWrapper = (EncoderClassWrapper) element;
            return "Class: " + classWrapper.className;
        }

        if (element instanceof EncoderClasspathWrapper) {
            return "Classpath:";
        }

        System.out.println("LabelProvider.getText() "
            + element.getClass().getName());
        return null;
    }

}
