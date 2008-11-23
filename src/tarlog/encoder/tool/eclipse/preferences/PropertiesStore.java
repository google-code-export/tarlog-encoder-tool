package tarlog.encoder.tool.eclipse.preferences;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.preference.IPreferenceStore;

import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.encoder.tool.api.fields.InputDirectoryField;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.InputFileField;
import tarlog.encoder.tool.api.fields.InputListField;
import tarlog.encoder.tool.api.fields.InputTextField;
import tarlog.encoder.tool.api.fields.Validator;
import tarlog.encoder.tool.api.fields.InputListField.InputType;

public class PropertiesStore {

    private List<EncodersGroup> store = new ArrayList<EncodersGroup>();

    public PropertiesStore(IPreferenceStore preferenceStore, boolean isDefault)
        throws MalformedURLException {
        int groupsAmount = isDefault ? preferenceStore.getDefaultInt(PropertiesStore.class.getName())
            : preferenceStore.getInt(PropertiesStore.class.getName());
        for (int i = 0; i < groupsAmount; ++i) {
            store.add(new EncodersGroup(i, preferenceStore, isDefault));
        }
    }

    void moveUp(EncodersGroup group) {
        int indexOf = store.indexOf(group);
        if (indexOf > 0) {
            Collections.swap(store, indexOf, indexOf - 1);
        }
    }

    void remove(EncodersGroup group) {
        store.remove(group);

    }

    boolean canMoveUp(EncodersGroup group) {
        int indexOf = store.indexOf(group);
        return (indexOf > 0);
    }

    void moveDown(EncodersGroup group) {
        int indexOf = store.indexOf(group);
        int size = store.size();
        if (indexOf < size - 1) {
            Collections.swap(store, indexOf, indexOf + 1);
        }
    }

    boolean canMoveDown(EncodersGroup group) {
        int indexOf = store.indexOf(group);
        int size = store.size();
        return (indexOf < size - 1);
    }

    /**
     * <p>
     * Returns copy of store
     * <p>
     * Performs shallow copy
     */
    public EncodersGroup[] getStore() {
        return store.toArray(new EncodersGroup[store.size()]);
    }

    public void store(IPreferenceStore preferenceStore) {
        preferenceStore.setValue(PropertiesStore.class.getName(), store.size());
        for (int i = 0; i < store.size(); ++i) {
            store.get(i).store(i, preferenceStore);
        }
    }

    public void newGroup(String groupName) {
        EncodersGroup encodersGroup = new EncodersGroup();
        encodersGroup.setGroupName(groupName);
        store.add(encodersGroup);

    }

    public Object getGroup(String groupName) {
        for (EncodersGroup group : store) {
            if (group.getGroupName().equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    /**
     * 
     */
    public static class EncodersGroup {

        private String           groupName;
        private boolean          enabled = true;
        private List<EncoderDef> list    = new ArrayList<EncoderDef>();

        private EncodersGroup() {

        }

        public EncodersGroup(int i, IPreferenceStore preferenceStore,
            boolean isDefault) throws MalformedURLException {
            String groupStr = getClass().getName() + "." + String.valueOf(i)
                + ".group";
            groupName = isDefault ? preferenceStore.getDefaultString(groupStr)
                : preferenceStore.getString(groupStr);
            String enabledStr = getClass().getName() + "." + String.valueOf(i)
                + ".enabled";
            if (preferenceStore.contains(enabledStr)) {
                enabled = isDefault ? preferenceStore.getDefaultBoolean(enabledStr)
                    : preferenceStore.getBoolean(enabledStr);
            } else {
                enabled = true;
            }
            String encodersStr = getClass().getName() + "." + String.valueOf(i)
                + ".encoders";
            int encodersAmount = isDefault ? preferenceStore.getDefaultInt(encodersStr)
                : preferenceStore.getInt(encodersStr);
            for (int j = 0; j < encodersAmount; ++j) {
                list.add(new EncoderDef(i, j, preferenceStore, isDefault));
            }
        }

        public void moveUp(EncoderDef encoderDef) {
            int indexOf = list.indexOf(encoderDef);
            if (indexOf > 0) {
                Collections.swap(list, indexOf, indexOf - 1);
            }
        }

        public boolean canMoveUp(EncoderDef def) {
            int indexOf = list.indexOf(def);
            return (indexOf > 0);
        }

        public void moveDown(EncoderDef encoderDef) {
            int indexOf = list.indexOf(encoderDef);
            int size = list.size();
            if (indexOf < size - 1) {
                Collections.swap(list, indexOf, indexOf + 1);
            }
        }

        public boolean canMoveDown(EncoderDef def) {
            int indexOf = list.indexOf(def);
            int size = list.size();
            return (indexOf < size - 1);
        }

        public void store(int i, IPreferenceStore preferenceStore) {
            preferenceStore.setValue(getClass().getName() + "."
                + String.valueOf(i) + ".group", groupName);
            preferenceStore.setValue(getClass().getName() + "."
                + String.valueOf(i) + ".enabled", enabled);
            preferenceStore.setValue(getClass().getName() + "."
                + String.valueOf(i) + ".encoders", list.size());
            for (int j = 0; j < list.size(); ++j) {
                list.get(j).store(i, j, preferenceStore);
            }
        }

        public void remove(EncoderDef encoderDef) {
            list.remove(encoderDef);
        }

        public void setGroupName(String groupName) {
            this.groupName = groupName;
        }

        public String getGroupName() {
            return groupName;
        }

        public void setList(List<EncoderDef> list) {
            this.list = list;
        }

        public List<EncoderDef> getList() {
            return list;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }

    /**
     * 
     */
    public static class EncoderDef implements Validator {

        @InputField(name = "Name", required = true)
        @InputTextField(validateNotEmpty = true, validationPattern = InputTextField.WORD_PATTERN, validationMessage = "Encoder name must be a word")
        String          name;

        private boolean enabled = true;

        @InputField(name = "Class name", required = true)
        @InputTextField(validateNotEmpty = true)
        String          className;

        @InputField(name = "Encoding Method")
        String          encodingMethod;

        @InputField(name = "Classpath")
        @InputFileField(buttonText = "Add jar", filterExtensions = { "*.jar",
            "*.*" }, relative = true)
        @InputDirectoryField(buttonText = "Add class folder")
        @InputListField(inputType = { InputType.UP, InputType.DOWN })
        String[]        classPath;

        EncoderDef() {

        }

        private EncoderDef(int i, int j, IPreferenceStore preferenceStore,
            boolean isDefault) throws MalformedURLException {
            String prefix = getClass().getName() + "." + String.valueOf(i)
                + "." + String.valueOf(j) + ".";
            name = isDefault ? preferenceStore.getDefaultString(prefix + "name")
                : preferenceStore.getString(prefix + "name");
            if (preferenceStore.contains(prefix + "enabled")) {
                enabled = isDefault ? preferenceStore.getDefaultBoolean(prefix
                    + "enabled") : preferenceStore.getBoolean(prefix
                    + "enabled");
            } else {
                enabled = true;
            }

            if (preferenceStore.contains(prefix + "encodingMethod")) {
                encodingMethod = isDefault ? preferenceStore.getDefaultString(prefix
                    + "encodingMethod")
                    : preferenceStore.getString(prefix + "encodingMethod");
            } else {
                encodingMethod = null;
            }

            className = isDefault ? preferenceStore.getDefaultString(prefix
                + "className")
                : preferenceStore.getString(prefix + "className");
            int classPathLength = isDefault ? preferenceStore.getDefaultInt(prefix
                + "classPath")
                : preferenceStore.getInt(prefix + "classPath");
            classPath = new String[classPathLength];
            for (int k = 0; k < classPathLength; ++k) {
                classPath[k] = isDefault ? preferenceStore.getDefaultString(prefix
                    + "classPath." + String.valueOf(k))
                    : preferenceStore.getString(prefix + "classPath."
                        + String.valueOf(k));
            }
        }

        private void store(int i, int j, IPreferenceStore preferenceStore) {
            String prefix = getClass().getName() + "." + String.valueOf(i)
                + "." + String.valueOf(j) + ".";
            preferenceStore.setValue(prefix + "name", name);
            preferenceStore.setValue(prefix + "className", className);
            preferenceStore.setValue(prefix + "enabled", enabled);
            preferenceStore.setValue(prefix + "encodingMethod", encodingMethod);
            if (classPath != null) {
                preferenceStore.setValue(prefix + "classPath", classPath.length);
                for (int k = 0; k < classPath.length; ++k) {
                    preferenceStore.setValue(prefix + "classPath."
                        + String.valueOf(k), classPath[k].toString());
                }
            } else {
                preferenceStore.setValue(prefix + "classPath", 0);
            }
        }

        public String isValid() {
            try {
                Class<?> encoderClass = getEncoderClass();
                if (!AbstractEncoder.class.isAssignableFrom(encoderClass)) {
                    return "The encoder class must be instance of AbstractEncoder";
                }
            } catch (ClassNotFoundException e) {
                return "Class not found";
            } catch (MalformedURLException e) {
                return e.getMessage();
            }
            return null;
        }

        public Class<?> getEncoderClass() throws ClassNotFoundException,
            MalformedURLException {
            ClassLoader classLoader = getClass().getClassLoader();
            if (classPath != null) {
                URL[] urls = new URL[classPath.length];
                for (int i = 0; i < classPath.length; ++i) {
                    urls[i] = new File(classPath[i]).toURL();
                }
                classLoader = new URLClassLoader(urls,
                    getClass().getClassLoader());
            }
            return classLoader.loadClass(className);
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isEnabled() {
            return enabled;
        }

        public String getName() {
            return name;
        }

        public String getEncodingMethod() {
            return encodingMethod;
        }
    }

}
