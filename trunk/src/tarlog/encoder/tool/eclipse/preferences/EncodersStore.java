package tarlog.encoder.tool.eclipse.preferences;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;

import org.eclipse.jface.preference.IPreferenceStore;

import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.Validator;

class EncodersStore {

    private static final String GROUP_SEP = "\r\n$$\r\n";

    private List<EncodersGroup> store     = new ArrayList<EncodersGroup>();

    /**
     * group encoder name className classpath
     * 
     */

    /**
     * <p>
     * Returns copy of store
     * <p>
     * Performs shallow copy
     */
    public EncodersGroup[] getStore() {
        return store.toArray(new EncodersGroup[store.size()]);
    }

    public EncodersStore(IPreferenceStore preferenceStore) {
        this(preferenceStore.getString(EncodersStore.class.getName()));
    }

    public EncodersStore(String string) {
        if ("".equals(string)) {
            return;
        }
        String[] split = string.split(GROUP_SEP);
        for (String str : split) {
            store.add(new EncodersGroup(str));
        }
    }

    public void store(IPreferenceStore preferenceStore) {
        preferenceStore.setValue(EncodersStore.class.getName(), toString());
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

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (EncodersGroup entry : store) {
            stringBuilder.append(entry.toString());
            stringBuilder.append(GROUP_SEP);
        }
        return stringBuilder.toString();
    }

    /**
     * 
     */
    public static class EncodersGroup {

        String                      groupName;
        List<EncoderDef>            list      = new ArrayList<EncoderDef>();
        private final static String GROUP_KEY = "===\r\n";
        private final static String SEP       = "\r\n%%%";

        private EncodersGroup(String string) {
            String[] group = string.split(GROUP_KEY);
            groupName = group[0];
            String[] split = group[1].split(EncodersGroup.SEP);
            for (String str : split) {
                list.add(new EncoderDef(str));
            }
        }

        private EncodersGroup() {

        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(groupName);
            stringBuilder.append(GROUP_KEY);
            for (EncoderDef encoderDef : list) {
                stringBuilder.append(encoderDef.toString());
                stringBuilder.append(EncodersGroup.SEP);
            }
            return stringBuilder.toString();
        }

    }

    /**
     * 
     */
    public static class EncoderDef implements Validator {

        private static final String FIELD_SEP     = "^^";
        private static final String CLASSPATH_SEP = ";";

        @InputField(name = "Name")
        String                      name;
        
        @InputField(name = "Class name")
        String                      className;
        
        String[]                    classPath;

        EncoderDef() {
            
        }
        
        EncoderDef(String string) {
            String[] fields = string.split(FIELD_SEP);
            name = fields[0];
            className = fields[1];
            classPath = fields[2].split(CLASSPATH_SEP);
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(FIELD_SEP);
            stringBuilder.append(className);
            stringBuilder.append(FIELD_SEP);
            for (String cls : classPath) {
                stringBuilder.append(cls);
                stringBuilder.append(CLASSPATH_SEP);
            }
            return stringBuilder.toString();
        }

        public String isValid() {
            if (name == null) {
                return "Encoder name cannot be empty";
            }
            Matcher matcher = EncoderToolPreferencePage.WORD_PATTERN.matcher(name);
            if (!matcher.matches()) {
                return "Encoder name must be a word";
            }
            return null;
        }
    }

}
