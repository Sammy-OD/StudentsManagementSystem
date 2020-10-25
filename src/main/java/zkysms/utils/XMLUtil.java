package zkysms.utils;

import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.File;
import java.io.IOException;

public class XMLUtil {

    private static Logger LOGGER = LogUtil.getLOGGER(XMLUtil.class.getName());

    public static Document loadXML(String path) {
        try{
            DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            return dBuilder.parse(new File(path));
        }catch(SAXException | ParserConfigurationException | IOException e){
            LOGGER.error(e.getMessage(), e);
            return null;
        }

    }

    public static void updateXML(String path, Document doc) {
        try{
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(path));
            transformer.transform(source, result);
        }catch (TransformerException e){
            LOGGER.error(e.getMessage(), e);
        }
    }

}