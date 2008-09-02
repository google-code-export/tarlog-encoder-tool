package tarlog.encoder.tool.eclipse.preferences;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EncodersStore {

    private static final String        GROUP_SEP = "\r\n$$\r\n";
    private static final String        GROUP_KEY = "===\r\n";

    private Map<String, EncodersGroup> store     = new HashMap<String, EncodersGroup>();

    /**
     * group encoder name className classpath
     * 
     */

    public static EncodersStore fromString(String string) {
        EncodersStore encodersStore = new EncodersStore();
        String[] split = string.split(GROUP_SEP);
        for (String str : split) {
            String[] groupSplit = str.split(GROUP_KEY);
            encodersStore.store.put(groupSplit[0],
                EncodersGroup.fromString(groupSplit[1]));
        }
        return encodersStore;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        for (Entry<String, EncodersGroup> entry : store.entrySet()) {
            stringBuilder.append(entry.getKey());
            stringBuilder.append(GROUP_KEY);
            stringBuilder.append(entry.getValue().toString());
            stringBuilder.append(GROUP_SEP);
        }
        return stringBuilder.toString();
    }

    public static class EncodersGroup {

        List<EncoderDef>    list = new ArrayList<EncoderDef>();

        final static String SEP  = "\r\n%%%";

        @Override
        public String toString() {
            StringBuilder stringBuilder = new StringBuilder();
            for (EncoderDef encoderDef : list) {
                stringBuilder.append(encoderDef.toString());
                stringBuilder.append(EncodersGroup.SEP);
            }
            return stringBuilder.toString();
        }

        static EncodersGroup fromString(String string) {
            EncodersGroup encodersGroup = new EncodersGroup();
            String[] split = string.split(EncodersGroup.SEP);
            for (String str : split) {
                encodersGroup.list.add(EncoderDef.fromString(str));
            }
            return encodersGroup;
        }
    }

    public static class EncoderDef {

        static final String FIELD_SEP     = "^^";
        static final String CLASSPATH_SEP = ";";

        String              name;
        String              className;
        String[]            classPath;

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

        static EncoderDef fromString(String string) {
            EncoderDef encoderDef = new EncoderDef();
            String[] fields = string.split(FIELD_SEP);
            encoderDef.name = fields[0];
            encoderDef.className = fields[1];
            encoderDef.classPath = fields[2].split(CLASSPATH_SEP);
            return encoderDef;
        }

    }

}
