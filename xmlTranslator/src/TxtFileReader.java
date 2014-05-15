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

        //parse metaByLine to MetaNode
        for(String metaLine: metaByLine){
            String[] temp = metaLine.split(",");
            metaNodes.add(new MetaNode(Integer.parseInt(temp[0]), temp[1]));
        }

//        for(MetaNode mn: metaNodes){
//            System.out.print(mn.getHierarchy() + "  ");
//            System.out.println(mn.getLabel());
//        }

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
            //for each line of text from the source file
            //if the string of a line l is l.equalsIgnoreCase(next label in meta vector)
            //  3 cases: number greater than, same as, or less than the previous hierarchy number
            int labelCounter = 1;
            MetaNode nodeWithLastChangedHierarchy = metaNodes.get(1);
            //System.out.println(nodeWithLastChangedHierarchy);
            Element titleElement = doc.createElement("title");
            titleElement.appendChild(doc.createTextNode(metaNodes.get(0).getLabel()));
            rootElement.appendChild(titleElement);

            Element elementWithLastChangedHierarchy = rootElement;
            Element appendTextToThisElement = rootElement;

            for(String lineOfText: sourceByLine){
                if(lineOfText.equalsIgnoreCase(metaNodes.get(labelCounter).getLabel())){
                    //if this line is a label, 3 cases as described above
                    if(metaNodes.get(labelCounter).getHierarchy() > nodeWithLastChangedHierarchy.getHierarchy()){
                        //if larger than the last hierarchy, this line should be nested in the last node
                        //if larger, it can only be the case that this nodes hierarchy is exactly lastHierarchy + 1
                        Element labelElement = doc.createElement("paragraph");
                        labelElement.appendChild(doc.createElement("image"));
                        Element pTitle = doc.createElement("pTitle");
                        labelElement.appendChild(pTitle);
                        pTitle.appendChild(doc.createTextNode(metaNodes.get(labelCounter).getLabel()));
                        elementWithLastChangedHierarchy.appendChild(labelElement);
                        elementWithLastChangedHierarchy = labelElement;
                        appendTextToThisElement = labelElement;
                    }
                    else if(metaNodes.get(labelCounter).getHierarchy() == nodeWithLastChangedHierarchy.getHierarchy()){
                        Element labelElement = doc.createElement("paragraph");
                        labelElement.appendChild(doc.createElement("image"));
                        labelElement.appendChild(doc.createTextNode(metaNodes.get(labelCounter).getLabel()));
                        elementWithLastChangedHierarchy.appendChild(labelElement);
                        appendTextToThisElement = labelElement;
                    }
                    else if(metaNodes.get(labelCounter).getHierarchy() < nodeWithLastChangedHierarchy.getHierarchy()){
                        //if less than the last hierarchy, get the parent element of the last changing hierarchy node
                        Element labelElement = doc.createElement("paragraph");
                        labelElement.appendChild(doc.createElement("image"));
                        //labelElement.appendChild(doc.createTextNode())
                    }
                    labelCounter++;//increment meta line counter only if one line if processed
                }
                else{//if this line is not a label, then it is a paragraph - append to the nearest paragraph node
                    Element paragraphText = doc.createElement("text");
                    paragraphText.appendChild(doc.createTextNode(lineOfText));
                    appendTextToThisElement.appendChild(paragraphText);
                    //appendTextToThisElement should have been appended
                }
            }

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
