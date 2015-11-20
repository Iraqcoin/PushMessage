/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.admin.common;

import com.vng.zing.pusheventmessage.common.ZTemplateResourceLoader;
import hapax.Template;
import hapax.TemplateDataDictionary;
import hapax.TemplateException;
import hapax.TemplateLoader;

/**
 *
 * @author root
 */
public class Templates {

    public static String apply(String tplName, TemplateDataDictionary dic) throws TemplateException {
        TemplateLoader templateLoader = ZTemplateResourceLoader.create("com/vng/zing/pusheventmessage/admin/view/");
        Template template = templateLoader.getTemplate(tplName);

        if (template != null) {
            return template.renderToString(dic);
        }
        return "";
    }
}
