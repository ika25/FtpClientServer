package ie.gmit.util;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * The ParseUtil class is use to parse the xml file
 */
public class ParseUtil {

	/**
	 * The method parseXML is use to parse the specified xml file and put the
	 * configuration values into the map
	 * 
	 * @param file
	 *            to be parsed
	 * @return map that contains all configuration data
	 * @throws Exception
	 */
	public static Map<String, String> parseXML(File file) throws Exception {

		Map<String, String> configMap = new HashMap<String, String>();

		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(file);

		Element rootElement = doc.getDocumentElement();

		configMap.put("username", rootElement.getAttribute("username"));
		configMap.put("server-host", rootElement.getElementsByTagName("server-host").item(0).getTextContent());
		configMap.put("server-port", rootElement.getElementsByTagName("server-port").item(0).getTextContent());
		configMap.put("download-dir", rootElement.getElementsByTagName("download-dir").item(0).getTextContent());

		return configMap;
	}

}
