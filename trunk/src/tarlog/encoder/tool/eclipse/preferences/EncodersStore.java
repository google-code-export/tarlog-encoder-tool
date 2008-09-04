package tarlog.encoder.tool.eclipse.preferences;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;

import org.eclipse.jface.preference.IPreferenceStore;

import tarlog.encoder.tool.api.fields.InputField;
import tarlog.encoder.tool.api.fields.InputListField;
import tarlog.encoder.tool.api.fields.Validator;
import tarlog.encoder.tool.api.fields.InputListField.InputType;

class EncodersStore {

    private static final String GROUP_SEP = "$$$$$";

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

    public EncodersStore(IPreferenceStore preferenceStore)
        throws MalformedURLException {
        this(preferenceStore.getString(EncodersStore.class.getName()));
    }

    public EncodersStore(String string) throws MalformedURLException {
        if ("".equals(string)) {
            return;
        }
        StringTokenizer stringTokenizer = new StringTokenizer(string, GROUP_SEP);
        while (stringTokenizer.hasMoreTokens()) {
            store.add(new EncodersGroup(stringTokenizer.nextToken()));
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
        private final static String GROUP_KEY = "@@@";
        private final static String SEP       = "%%%";

        private EncodersGroup(String string) throws MalformedURLException {
            StringTokenizer tokenizer = new StringTokenizer(string, GROUP_KEY);
            groupName = tokenizer.nextToken();
            if (tokenizer.countTokens() >= 1) {
                StringTokenizer split = new StringTokenizer(
                    tokenizer.nextToken(), EncodersGroup.SEP);
                while (split.hasMoreTokens()) {
                    list.add(new EncoderDef(split.nextToken()));
                }
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

        @InputField(name = "Classpath")
        @InputListField(inputType = { InputType.FILE, InputType.FOLDER })
        URL[]                       classPath;

        EncoderDef() {

        }

        EncoderDef(String string) throws MalformedURLException {
            StringTokenizer tokenizer = new StringTokenizer(string, FIELD_SEP);
            name = tokenizer.nextToken();
            if (tokenizer.countTokens() >= 1) {
                className = tokenizer.nextToken();
                if (tokenizer.countTokens() >= 1) {
                    StringTokenizer stringTokenizer = new StringTokenizer(
                        tokenizer.nextToken(), CLASSPATH_SEP);
                    int countTokens = stringTokenizer.countTokens();
                    classPath = new URL[countTokens];
                    for (int i = 0; i < countTokens; ++i) {
                        classPath[i] = new URL(stringTokenizer.nextToken());
                    }
                }
            }
        }

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            stringBuilder.append(name);
            stringBuilder.append(FIELD_SEP);
            stringBuilder.append(className);
            stringBuilder.append(FIELD_SEP);
            if (classPath != null) {
                for (URL cls : classPath) {
                    stringBuilder.append(cls.toString());
                    stringBuilder.append(CLASSPATH_SEP);
                }
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
