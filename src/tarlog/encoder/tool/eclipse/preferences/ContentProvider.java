package tarlog.encoder.tool.eclipse.preferences;

import java.net.URL;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import tarlog.encoder.tool.eclipse.preferences.EncodersStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.EncodersStore.EncodersGroup;


class ContentProvider implements ITreeContentProvider {

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof EncodersGroup) {
            EncodersGroup encodersGroup = (EncodersGroup) parentElement;
            return encodersGroup.getList().toArray();
        }
        if (parentElement instanceof EncoderDef) {
            EncoderDef encoderDef = (EncoderDef) parentElement;
            return new Object[] {
                new EncoderClassWrapper(encoderDef.className),
                new EncoderClasspathWrapper(encoderDef.classPath) };
        }
        if (parentElement instanceof EncoderClasspathWrapper) {
            EncoderClasspathWrapper classpathWrapper = (EncoderClasspathWrapper) parentElement;
            return classpathWrapper.classpath;
        }
        System.out.println("ContentProvider.getChildren() "
            + parentElement.getClass().getName());
        return null;
    }

    public Object getParent(Object element) {
        System.out.println("ContentProvider.getParent() ");
        return null;
    }

    public boolean hasChildren(Object element) {
        if (element == null) {
            return false;
        }
        if (element instanceof EncodersGroup) {
            EncodersGroup encodersGroup = (EncodersGroup) element;
            return encodersGroup.getList().size() > 0;

        }
        if (element instanceof EncoderDef) {
            return true;
        }

        if (element instanceof EncoderClassWrapper) {
            return false;
        }

        if (element instanceof URL) {
            return false;
        }

        if (element instanceof EncoderClasspathWrapper) {
            EncoderClasspathWrapper classpathWrapper = (EncoderClasspathWrapper) element;
            return (classpathWrapper.classpath != null && classpathWrapper.classpath.length > 0);
        }

        System.out.println("ContentProvider.hasChildren() "
            + element.getClass().getName());
        return false;
    }

    public Object[] getElements(Object inputElement) {
        if (inputElement == null) {
            return new Object[0];
        }
        if (inputElement instanceof EncodersStore) {
            EncodersStore encodersStore = (EncodersStore) inputElement;
            return encodersStore.getStore();
        }
        System.out.println("ContentProvider.getElements() "
            + inputElement.getClass().getName());
        return null;
    }

    public void dispose() {
        // TODO Auto-generated method stub

    }

    public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
        // TODO Auto-generated method stub

    }

}

