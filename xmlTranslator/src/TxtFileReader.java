import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
/**
 * Created by Hanchen on 4/26/14.
 * TODO: separate reader and writer for better independency
 */
public class TxtFileReader extends KnowledgeSourceFileReader {

    TxtFileReader(String filePath, String metaFilePath){
        sourceFileType = "txt";
        String newLine;
        //read in knowledge source file -> sourceByLine
        try{
            BufferedReader fileReader = new BufferedReader(new FileReader(filePath));
            try{
                while((newLine = fileReader.readLine())!= null){
                    sourceByLine.add(newLine);
                }
            }
            catch (IOException ioException){
                System.out.println("file empty");
            }
        }
        catch (FileNotFoundException e){
            System.out.println("file not found");
            System.exit(1);
        }

        //read in meta file -> metaByLine
        try{
            BufferedReader fileReader = new BufferedReader(new FileReader(metaFilePath));
            try {
                while((newLine = fileReader.readLine())!= null){
                    metaByLine.add(newLine);
                }
            }catch (IOException e){
                e.printStackTrace();
            }
        }catch (FileNotFoundException e){
            System.out.println("file not found");
            System.exit(1);
        }

//        for(String s: metaByLine)
//            System.out.println(s);
    }

    public void generateXml(){
        String title = sourceByLine.get(0);
        String xmlFileName = title + ".xml";

        try{
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            //root node
            Element rootElement = doc.createElement("entry");
            doc.appendChild(rootElement);
            //TODO: create element nodes

            //test node
            Element test = doc.createElement("History");
            test.appendChild(doc.createTextNode("this is the content of History"));
            rootElement.appendChild(test);

            //write the content into xml file
            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            DOMSource domSource = new DOMSource(doc);
            StreamResult result = new StreamResult(new File(xmlFileName));

            // Output to console for testing
            // StreamResult result = new StreamResult(System.out);

            transformer.transform(domSource, result);
            System.out.println("file saved");
        }catch (ParserConfigurationException parserConfigException){
            parserConfigException.printStackTrace();
        }catch (TransformerException transException){
            transException.printStackTrace();
        }

    }
}
