/*******************************************************************************
 *     Copyright 2009 Michael Elman (http://tarlogonjava.blogspot.com)
 *
 *     Licensed under the Apache License, Version 2.0 (the "License"); 
 *     you may not use this file except in compliance with the License. 
 *     You may obtain a copy of the License at 
 *     
 *     http://www.apache.org/licenses/LICENSE-2.0 
 *     
 *     Unless required by applicable law or agreed to in writing,
 *     software distributed under the License is distributed on an
 *     "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *     KIND, either express or implied.  See the License for the
 *     specific language governing permissions and limitations
 *     under the License.
 *******************************************************************************/
package tarlog.encoder.tool.encoders;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import tarlog.encoder.tool.api.AbstractEncoder;
import tarlog.ui.swt.ddialog.api.fields.InputField;

public class EpochDateDecoder extends AbstractEncoder {

	private static final Pattern p = Pattern.compile("(.*?)(\\d+)(.*)", Pattern.DOTALL);
	
	@InputField(name="Date Format")
	private String dateFormat = "yyyy-MM-dd'T'HH:mm:ss.SSSZ";
	
    @Override
    public Object encode(String source) {
    	if (source.trim().isEmpty()) {
    		return null;
    	}
    	SimpleDateFormat simpleDateFormat = new SimpleDateFormat(dateFormat);
    	
    	StringBuilder buf = new StringBuilder();
    	while (true) {
	    	Matcher matcher = p.matcher(source);
			if (!matcher.matches()) {
				return buf.append(source).toString();
			}
			String prefix = matcher.group(1);
			if (prefix != null && prefix.length() > 0) {
				buf.append(prefix);
			}
			String digits = matcher.group(2);
			source = matcher.group(3);
			
	    	if (dateFormat != null && dateFormat.length() > 0) {
	        	
	    	}
			buf.append(simpleDateFormat.format(new Date(Long.parseLong(digits))));
    	}
    	
//    	source = source.trim();
//    	source = source.replace("\r", "");
//    	String[] split;
//    	if (source.contains("\n")) {
//    		split = source.split("\n");
//    	} else {
//    		split = new String[] {source};
//    	}
//        try {
//        	StringBuilder buf = new StringBuilder();
//        	for (String s : split) {
//        		if (buf.length() > 0) {
//        			buf.append("\n");
//        		}
//        		buf.append();
//        	}
//            return buf.toString();
//        } catch (Exception e) {
//            showException(e);
//            return null;
//        }
    }

}
