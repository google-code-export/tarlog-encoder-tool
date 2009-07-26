/*******************************************************************************
 *     Licensed to the Apache Software Foundation (ASF) under one
 *     or more contributor license agreements.  See the NOTICE file
 *     distributed with this work for additional information
 *     regarding copyright ownership.  The ASF licenses this file
 *     to you under the Apache License, Version 2.0 (the
 *     "License"); you may not use this file except in compliance
 *     with the License.  You may obtain a copy of the License at
 *     
 *      http://www.apache.org/licenses/LICENSE-2.0
 *     
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 *******************************************************************************/
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
