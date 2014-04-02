package com.azudio.bootstrap3snippetgenerator;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.DocumentResult;
import org.dom4j.io.DocumentSource;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.SAXReader;
import org.dom4j.io.XMLWriter;

public class Bootstrap3SnippetGenerator {

	private Document d, s;
	private String categoryId;
	private Long categoryNumber;
	private Long itemNumber;
	private Integer variableNameNumber;

	public static void main(String[] args) throws DocumentException, IOException {
		Bootstrap3SnippetGenerator g = new Bootstrap3SnippetGenerator();

		g.start();
	}

	private void start() throws DocumentException, IOException {

		init();
		findFiles();

		System.out.println(d.asXML());

		write(d);

	}

	Element cat;

	private void init() throws MalformedURLException, DocumentException {
		d = parse(new URL("file:eclipse-snippets/snippets-skeleton.xml"));

		cat = (Element) d.selectSingleNode("//category");

		setCategoryId(cat.attribute("id").getValue());

		categoryNumber = Long.parseLong(getCategoryId().substring(getCategoryId().indexOf("_") + 1, getCategoryId().length()));
		setItemNumber(categoryNumber);

	}

	private void findFiles() throws DocumentException {
		String rootPath = ".";
		File[] domains = new File(rootPath).listFiles();
		for (File domain : domains) {
			if (domain.isDirectory()) {
				File[] files = new File(domain.getAbsolutePath()).listFiles();
				for (File file : files) {
					if (file.isFile() && file.getAbsolutePath().endsWith(".sublime-snippet")) {
						System.out.println(file.getAbsolutePath());
						try {
							String simple = file.getName().replaceAll("\\..*", "");
							System.out.println(simple);
							System.out.println(file.toURI().toURL().toString());

							s = parse(file.toURI().toURL());

							Element content = (Element) s.selectSingleNode("//content");

							cat.add(createItem(simple, simple + " Description", content.getTextTrim()));

						} catch (MalformedURLException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
				}
			}
		}
	}

	private Element createItem(String label, String description, String content) {
		Element e = DocumentHelper.createElement("item");
		e.addAttribute("category", getCategoryId());
		e.addAttribute("id", "item_" + ++itemNumber);
		e.addAttribute("label", label);

		e.addElement("description").addCDATA(description);

		for (Variable v : parseVariables(content)) {
			Element variableElement = e.addElement("variable");
			variableElement.addAttribute("default", v.getDefaultValue());
			variableElement.addAttribute("id", v.getName());
			variableElement.addAttribute("name", v.getName());
			variableElement.addElement("description").addCDATA(v.getDescription());
		}

		e.addElement("content").addCDATA(swapVariableNames(content));

		System.out.println(e.asXML());

		return e;
	}

	private String swapVariableNames(String content) {
		Pattern p = Pattern.compile("(\\$\\{(\\d):(.*?)\\})");
		Matcher m = p.matcher(content);

		variableNameNumber = 1;

		while (m.find()) {
			content = content.replace(m.group(0), "${name_" + variableNameNumber + "}");
			variableNameNumber++;
		}

		System.out.println(content);

		return content;
	}

	private List<Variable> parseVariables(String content) {
		List<Variable> l = new ArrayList<Variable>();

		Pattern p = Pattern.compile("(\\$\\{(\\d):(.*?)\\})");
		Matcher m = p.matcher(content);

		variableNameNumber = 1;

		while (m.find()) {
			// System.out.println("0" + m.group(0));
			// System.out.println("1" + m.group(1));
			System.out.println("Var Num: " + variableNameNumber);
			System.out.println("Param No: " + m.group(2));
			System.out.println("Default Value: " + m.group(3));
			l.add(new Variable("name_" + variableNameNumber, "", m.group(3)));

			variableNameNumber++;
		}

		System.out.println(content);

		return l;
	}

	private Document parse(URL url) throws DocumentException {
		SAXReader reader = new SAXReader();
		Document document = reader.read(url);

		return document;
	}

	private Document styleDocument(Document document, String stylesheet) throws Exception {

		// load the transformer using JAXP
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer(new StreamSource(stylesheet));

		// now lets style the given document
		DocumentSource source = new DocumentSource(document);
		DocumentResult result = new DocumentResult();
		transformer.transform(source, result);

		// return the transformed document
		Document transformedDoc = result.getDocument();
		return transformedDoc;
	}

	public String getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(String categoryId) {
		this.categoryId = categoryId;
	}

	public Long getItemNumber() {
		return itemNumber;
	}

	public void setItemNumber(Long itemNumber) {
		this.itemNumber = itemNumber;
	}

	public void write(Document document) throws IOException {

		// lets write to a file
		XMLWriter writer = new XMLWriter(new FileWriter("output-snippets.xml"));
		writer.write(document);
		writer.close();

		// Pretty print the document to System.out
		OutputFormat format = OutputFormat.createPrettyPrint();
		writer = new XMLWriter(System.out, format);
		writer.write(document);

		// Compact format to System.out
		format = OutputFormat.createCompactFormat();
		writer = new XMLWriter(System.out, format);
		writer.write(document);
	}
}
