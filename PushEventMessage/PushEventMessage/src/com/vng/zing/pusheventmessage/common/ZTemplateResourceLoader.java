/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.vng.zing.pusheventmessage.common;

import hapax.Template;
import hapax.TemplateException;
import hapax.TemplateLoader;
import hapax.TemplateResourceLoader;
import hapax.parser.TemplateParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author root
 */
public class ZTemplateResourceLoader extends TemplateResourceLoader{
	private static final Map<String, Template> cache = new LinkedHashMap<String, Template>();

	/**
	 * Creates a TemplateLoader for CTemplate language
	 */
	public static TemplateLoader create(String base_path) {
		return new ZTemplateResourceLoader(base_path);
	}

	/**
	 * Creates a TemplateLoader using the argument parser.
	 */
	public static TemplateLoader createForParser(String base_path, TemplateParser parser) {
		return new ZTemplateResourceLoader(base_path, parser);
	}

	public ZTemplateResourceLoader(String baseDir) {
		super(baseDir);
	}

	public ZTemplateResourceLoader(String baseDir, TemplateParser parser) {
		super(baseDir, parser);
	}

	@Override
	public Template getTemplate(TemplateLoader context, String resource) throws TemplateException {
		if (!resource.endsWith(".xtm")) {
			resource += ".xtm";
		}

		String templatePath = baseDir + resource;
		if (cache.containsKey(templatePath)) {
			return cache.get(templatePath);
		}

		InputStream is = getClass().getClassLoader().getResourceAsStream(templatePath);
		if (is == null) {
		    is = getClass().getClassLoader().getResourceAsStream(resource);
		    if (null == is)
			throw new TemplateException("Template " + templatePath + " could not be found");
		}

		String contents;
		try {
			contents = copyToString(new InputStreamReader(is));
		} catch (IOException e) {
			throw new TemplateException(e);
		}

		//Replace Static Messages
//		contents = replaceMessage(contents);

		Template template = parser == null ? new Template(contents, context) : new Template(parser, contents, context);

		synchronized (cache) {
			cache.put(templatePath, template);
		}

		return template;
	}

	private String copyToString(Reader in) throws IOException {
		StringWriter out = new StringWriter();
		try {
			char[] buffer = new char[4096];
			int bytesRead = -1;
			while ((bytesRead = in.read(buffer)) != -1) {
				out.write(buffer, 0, bytesRead);
			}
			out.flush();
		} finally {
			try {
				in.close();
			} catch (IOException ignore) {}
			try {
				out.close();
			} catch (IOException ignore) {}
		}
		return out.toString();
	}

//	private String replaceMessage(String source) {
//		String result = source;
//
//		try {
//			Pattern pattern = Pattern.compile("<z:message.*?\\>");
//			Matcher matcher = pattern.matcher(source);
//			while(matcher.find()) {
//				String code = geti18nCode(matcher.group());
//
//				String message = null;
//				try {
//					message = Configuration.getMessage(code);
//				} catch (Exception ex) {
//					message = null;
//				}
//
//				if (message == null || message.isEmpty()) {
//					message = geti18nDefaultMessage(matcher.group());
//				}
//
//				result = result.replace(matcher.group(), message);
//			}
//		} catch (Exception ex) {
//			result = source;
//		}
//
//		return result;
//	}

	private String geti18nCode(String target) {
		String result = "";

		try {
			Pattern pattern = Pattern.compile("code\\s*=\\s*(\'|\")(.*?)(\'|\")");
			Matcher matcher = pattern.matcher(target);
			if (matcher.find()) {
				result = matcher.group(2);
			}
		} catch (Exception ex) {
			result = "";
		}

		return result;
	}

	private String geti18nDefaultMessage(String target) {
		String result = "";

		try {
			Pattern pattern = Pattern.compile("default\\s*=\\s*(\'|\")(.*?)(\'|\")");
			Matcher matcher = pattern.matcher(target);
			if (matcher.find()) {
				result = matcher.group(2);
			}
		} catch (Exception ex) {
			result = "";
		}

		return result;
	}

}
