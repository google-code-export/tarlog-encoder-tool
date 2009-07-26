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
package tarlog.encoder.tool.eclipse.preferences;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncoderDef;
import tarlog.encoder.tool.eclipse.preferences.PropertiesStore.EncodersGroup;

class ContentProvider implements ITreeContentProvider {

    public Object[] getChildren(Object parentElement) {
        if (parentElement instanceof EncodersGroup) {
            EncodersGroup encodersGroup = (EncodersGroup) parentElement;
            return encodersGroup.getList().toArray();
        }
        if (parentElement instanceof EncoderDef) {
            EncoderDef encoderDef = (EncoderDef) parentElement;
            List<Object> list = new ArrayList<Object>(3);
            list.add(new StringWrapper("Class", encoderDef.className));
            if (encoderDef.encodingMethod != null) {
                list.add(new StringWrapper(
                    "Encoding Method", encoderDef.encodingMethod));
            }
            if (encoderDef.classPath != null && encoderDef.classPath.length > 0) {
                list.add(new EncoderClasspathWrapper(encoderDef.classPath));
            }
            
            return list.toArray();
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

        if (element instanceof StringWrapper) {
            return false;
        }

        if (element instanceof String) {
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
        if (inputElement instanceof PropertiesStore) {
            PropertiesStore encodersStore = (PropertiesStore) inputElement;
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
