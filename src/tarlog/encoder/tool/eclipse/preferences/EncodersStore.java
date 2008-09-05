package tarlog.encoder.tool.eclipse.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;

import org.eclipse.jface.preference.IPreferenceStore;

import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.encoder.tool.api.fields.InputDirectoryField;
import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.InputFileField;
import tarlog.encoder.tool.api.fields.InputListField;
import tarlog.encoder.tool.api.fields.Validator;
import tarlog.encoder.tool.api.fields.InputListField.InputType;

class EncodersStore {

    private List<EncodersGroup> store = new ArrayList<EncodersGroup>();

    public void moveUp(EncodersGroup group) {
        int indexOf = store.indexOf(group);
        if (indexOf > 0) {
            Collections.swap(store, indexOf, indexOf - 1);
        }
    }

    public void remove(EncodersGroup group) {
        store.remove(group);

    }

    public boolean canMoveUp(EncodersGroup group) {
        int indexOf = store.indexOf(group);
        return (indexOf > 0);
    }

    public void moveDown(EncodersGroup group) {
        int indexOf = store.indexOf(group);
        int size = store.size();
        if (indexOf < size - 1) {
            Collections.swap(store, indexOf, indexOf + 1);
        }
    }

    public boolean canMoveDown(EncodersGroup group) {
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

    public EncodersStore(IPreferenceStore preferenceStore)
        throws MalformedURLException {
        int groupsAmount = preferenceStore.getInt(EncodersStore.class.getName());
        for (int i = 0; i < groupsAmount; ++i) {
            store.add(new EncodersGroup(i, preferenceStore));
        }
    }

    public void store(IPreferenceStore preferenceStore) {
        preferenceStore.setValue(EncodersStore.class.getName(), store.size());
        for (int i = 0; i < store.size(); ++i) {
            store.get(i).store(i, preferenceStore);
        }
    }

    public void newGroup(String groupName) {
        EncodersGroup encodersGroup = new EncodersGroup();
        encodersGroup.groupName = groupName;
        store.add(encodersGroup);

    }

    public Object getGroup(String groupName) {
        for (EncodersGroup group : store) {
            if (group.groupName.equals(groupName)) {
                return group;
            }
        }
        return null;
    }

    /**
     * 
     */
    public static class EncodersGroup {

        String           groupName;
        List<EncoderDef> list = new ArrayList<EncoderDef>();

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

        public EncodersGroup(int i, IPreferenceStore preferenceStore)
            throws MalformedURLException {
            groupName = preferenceStore.getString(getClass().getName() + "."
                + String.valueOf(i) + ".group");
            int encodersAmount = preferenceStore.getInt(getClass().getName()
                + "." + String.valueOf(i) + ".encoders");
            for (int j = 0; j < encodersAmount; ++j) {
                list.add(new EncoderDef(i, j, preferenceStore));
            }
        }

        public void store(int i, IPreferenceStore preferenceStore) {
            preferenceStore.setValue(getClass().getName() + "."
                + String.valueOf(i) + ".group", groupName);
            preferenceStore.setValue(getClass().getName() + "."
                + String.valueOf(i) + ".encoders", list.size());
            for (int j = 0; j < list.size(); ++j) {
                list.get(j).store(i, j, preferenceStore);
            }
        }

        private EncodersGroup() {

        }

        public void remove(EncoderDef encoderDef) {
            list.remove(encoderDef);
        }
    }

    /**
     * 
     */
    public static class EncoderDef implements Validator {

        @InputField(name = "Name")
        String name;

        @InputField(name = "Class name")
        String className;

        @InputField(name = "Classpath")
        @InputFileField(buttonText = "Add jar", filterExtensions = { "*.jar",
            "*.*" })
        @InputDirectoryField(buttonText = "Add class folder")
        @InputListField(inputType = { InputType.UP, InputType.DOWN })
        URL[]  classPath;

        EncoderDef() {

        }

        private EncoderDef(int i, int j, IPreferenceStore preferenceStore)
            throws MalformedURLException {
            String prefix = getClass().getName() + "." + String.valueOf(i)
                + "." + String.valueOf(j) + ".";
            name = preferenceStore.getString(prefix + "name");
            className = preferenceStore.getString(prefix + "className");
            int classPathLength = preferenceStore.getInt(prefix + "classPath");
            classPath = new URL[classPathLength];
            for (int k = 0; k < classPathLength; ++k) {
                classPath[k] = new URL(preferenceStore.getString(prefix
                    + "classPath." + String.valueOf(k)));
            }
        }

        private void store(int i, int j, IPreferenceStore preferenceStore) {
            String prefix = getClass().getName() + "." + String.valueOf(i)
                + "." + String.valueOf(j) + ".";
            preferenceStore.setValue(prefix + "name", name);
            preferenceStore.setValue(prefix + "className", className);
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
            if (name == null) {
                return "Encoder name cannot be empty";
            }
            if (name.equals("")) {
                return "Encoder name cannot be empty";
            }
            Matcher matcher = EncoderToolPreferencePage.WORD_PATTERN.matcher(name);
            if (!matcher.matches()) {
                return "Encoder name must be a word";
            }
            if (className == null) {
                return "Encoder name cannot be empty";
            }
            if (className.equals("")) {
                return "Encoder name cannot be empty";
            }
            ClassLoader classLoader = classPath == null ? getClass().getClassLoader()
                : new URLClassLoader(classPath, getClass().getClassLoader());
            try {
                Class<?> encoderClass = classLoader.loadClass(className);
                if (!AbstractEncoder.class.isAssignableFrom(encoderClass)) {
                    return "The encoder class must be instance of AbstractEncoder";
                }
            } catch (ClassNotFoundException e) {
                return "Class not found";
            }
            return null;
        }

    }

}
