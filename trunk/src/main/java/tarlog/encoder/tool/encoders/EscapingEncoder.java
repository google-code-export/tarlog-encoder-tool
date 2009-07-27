package tarlog.encoder.tool.encoders;

import org.apache.commons.lang.StringEscapeUtils;

import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.ui.swt.ddialog.api.fields.InputField;

public class EscapingEncoder extends AbstractEncoder {

    private enum Methods {
        EscapeXML,
        UnescapeXML,
        EscapeHTML,
        UnescapeHTML,
        EscapeJava,
        UnescapeJava,
        EscapeJavascript,
        UnescapeJavascript,
        EscapeSql
    }

    @InputField(name = "Method", readonly = true, required = true)
    private Methods method;

    @Override
    public Object encode(String source) {
        switch (method) {
            case EscapeXML:
                return StringEscapeUtils.escapeXml(source);
            case UnescapeXML:
                return StringEscapeUtils.unescapeXml(source);
            case EscapeHTML:
                return StringEscapeUtils.escapeHtml(source);
            case UnescapeHTML:
                return StringEscapeUtils.unescapeHtml(source);
            case EscapeJava:
                return StringEscapeUtils.escapeJava(source);
            case UnescapeJava:
                return StringEscapeUtils.unescapeJava(source);
            case EscapeJavascript:
                return StringEscapeUtils.escapeJavaScript(source);
            case UnescapeJavascript:
                return StringEscapeUtils.unescapeJavaScript(source);
            case EscapeSql:
                return StringEscapeUtils.escapeSql(source);
        }
        return null;
    }

    @Override
    protected String historyInfo() {
        String historyInfo = super.historyInfo();
        return String.format("%s (%s)", historyInfo, String.valueOf(method));
    }
}
