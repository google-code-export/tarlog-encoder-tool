package tarlog.encoder.tool.eclipse.preferences;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Properties;

import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.jface.preference.IPreferenceStore;

import tarlog.encoder.tool.eclipse.Activator;

/**
 * Class used to initialize default preference values.
 */
public class PreferenceInitializer extends AbstractPreferenceInitializer {

    /*
     * (non-Javadoc)
     * 
     * @seeorg.eclipse.core.runtime.preferences.AbstractPreferenceInitializer#
     * initializeDefaultPreferences()
     */
    public void initializeDefaultPreferences() {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        loadDefault(store);
    }

    public static void loadDefault(IPreferenceStore store) {
        Properties properties = new Properties();
        InputStream is = PreferenceInitializer.class.getClassLoader().getResourceAsStream(
            "encoder-default.properties");
        try {
            if (is == null) {
                is = new FileInputStream("encoder-default.properties");
            }
            properties.load(is);
            @SuppressWarnings("unchecked")
            Enumeration<String> propertyNames = (Enumeration<String>) properties.propertyNames();
            while (propertyNames.hasMoreElements()) {
                String name = propertyNames.nextElement();
                String property = properties.getProperty(name);
                store.setDefault(name, property);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
