/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.text.StrSubstitutor;

/**
 *
 * @author namnt3
 */
public class MsgBuilder {

    public static class NamedTemplate {

        private final Map<String, String> model;
        private final StrSubstitutor builder;
        private final String template;

        private NamedTemplate(String template) {
            this.model = new HashMap<String, String>();
            this.template = template;
            this.builder = new StrSubstitutor(model, "{{", "}}");
        }

        public NamedTemplate put(String name, Object toString) {
            model.put(name, String.valueOf(toString));
            return this;
        }

        public NamedTemplate put(String name, Object toString, String onNull) {
            return put(name, toString == null ? onNull : toString);
        }

        @Override
        public String toString() {
            return builder.replace(template);
        }
    }

    public static class IndexedTemplate {

        private final NamedTemplate engine;
        private int index = 0;

        public IndexedTemplate(String template) {
            engine = new NamedTemplate(template);
        }

        public IndexedTemplate put(Object toString) {
            engine.put(String.valueOf(index), toString);
            index++;
            return this;
        }

        public IndexedTemplate put(Object toString, String onNull) {
            return put(toString == null ? onNull : toString);
        }

        @Override
        public String toString() {
            return engine.toString();
        }

    }

    public static class IteratedTemplate {

        private final String template;
        private final List<String> strings;

        public IteratedTemplate(String template) {
            strings = new ArrayList<String>();
            this.template = template;
        }

        public IteratedTemplate put(Object value) {
            this.strings.add(String.valueOf(value));
            return this;
        }

        public IteratedTemplate put(Object toString, String onNull) {
            return put(toString == null ? onNull : toString);
        }

        @Override
        public String toString() {
            StringBuilder builder = new StringBuilder();
            for (int i = 0, k = 0; i < template.length(); i++) {
                if (k < strings.size() && template.charAt(i) == '?' && i + 1 < template.length() && template.charAt(i) == template.charAt(i + 1)) {
                    builder.append(strings.get(k));
                    k++;
                    i++;
                    continue;
                }
                builder.append(template.charAt(i));
            }
            return builder.toString();
        }

    }

    public static NamedTemplate namedTmpl(String template) {
        return new NamedTemplate(template);
    }

    public static IndexedTemplate numberedTmpl(String template) {
        return new IndexedTemplate(template);
    }

    public static IteratedTemplate tmpl(String template) {
        return new IteratedTemplate(template);
    }

    public static String format(Object... array) {
        if (array == null || array.length == 0) {
            return null;
        }
        if (!(array[0] instanceof String)) {
            return null;
        }
        String template = String.valueOf(array[0]);
        if (array.length == 1) {
            return template;
        }

        StringBuilder builder = new StringBuilder();
        for (int i = 0, k = 1; i < template.length(); i++) {
            if (k < array.length && template.charAt(i) == '?' && i + 1 < template.length() && template.charAt(i) == template.charAt(i + 1)) {
                builder.append(array[k]);
                k++;
                i++;
                continue;
            }
            builder.append(template.charAt(i));
        }
        return builder.toString();
    }
}
