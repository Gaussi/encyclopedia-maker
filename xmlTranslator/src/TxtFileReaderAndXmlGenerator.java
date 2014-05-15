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
import java.util.Vector;

/**
 * Created by Hanchen on 5/10/14.
 */
public class TxtFileReaderAndXmlGenerator extends KnowledgeSourceFileReader {
    TxtFileReaderAndXmlGenerator(String filePath, String metaFilePath){
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

//        for(String line: sourceByLine){
//            System.out.println(line);
//        }

        //parse metaByLine to MetaNode
        for(String metaLine: metaByLine){
            String[] temp = metaLine.split(",");
            metaNodes.add(new MetaNode(Integer.parseInt(temp[0]), temp[1]));
        }
//        for(MetaNode metaNode: metaNodes){
//            System.out.println(metaNode);
//        }
        Vector<MetaNode> temp = metaNodes;
        metaNodes = processMetaNodes(temp);
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
            Element titleElement = doc.createElement("title");
            titleElement.appendChild(doc.createTextNode(metaNodes.get(0).getLabel()));
            rootElement.appendChild(titleElement);
            Vector<String> sourceByLineExcludeTitle = sourceByLine;
            sourceByLineExcludeTitle.remove(0);
            Vector metaNodesExcludeTitleNode = metaNodes;
            metaNodesExcludeTitleNode.remove(0);

            //auxXmlTreeBuilder(doc, rootElement, sourceByLineExcludeTitle, metaNodesExcludeTitleNode);
            auxXMLTreeBuilder(doc, rootElement, 0, sourceByLineExcludeTitle, metaNodesExcludeTitleNode);

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

    private int sourceLineCount = 0;
    private int metaNodesCount = 0;
    //doc: the Document that is going to be written to the xml file
    //relativeRoot: append stuff to this
    //sourceByLine: knowledge source file broken into a vector of Strings
    //metaNodes: int hierarchy and String label
    //TODO: this one isn't right
    private void auxXmlTreeBuilder(Document doc, Element relativeRoot, Vector<String> sourceByLine, Vector<MetaNode> metaNodes){
        for( ; sourceLineCount < sourceByLine.size();sourceLineCount++){
            if(sourceByLine.get(sourceLineCount).equalsIgnoreCase(metaNodes.get(metaNodesCount).getLabel())){
                Element toBeRoot = relativeRoot;//TODO: needs more thoughts
                if(metaNodes.get(metaNodesCount).getType() == MetaNode.nodeType.SECTION){
                    //if this is a section
                    Element element = doc.createElement("section");
                    Element sTitleElement = doc.createElement("sTitle");
                    sTitleElement.appendChild(doc.createTextNode(metaNodes.get(metaNodesCount).getLabel()));
                    element.appendChild(sTitleElement);
                    relativeRoot.appendChild(element);
                    toBeRoot = element;
                }
                else if(metaNodes.get(metaNodesCount).getType() == MetaNode.nodeType.PARAGRAPH){
                    //else if this is a paragraph
                    Element element = doc.createElement("paragraph");
                    Element pTitle = doc.createElement("pTitle");
                    pTitle.appendChild(doc.createTextNode(metaNodes.get(metaNodesCount).getLabel()));
                    element.appendChild(pTitle);
                    relativeRoot.appendChild(element);
                    toBeRoot = element;
                }

                Vector<String> unprocessedSource = sourceByLine;
                unprocessedSource.remove(0);
                Vector<MetaNode> unprocessedMetaNodes = metaNodes;
                unprocessedMetaNodes.remove(0);

                //pass in the unprocessed sourceByLine and metaNodes
                auxXmlTreeBuilder(doc, toBeRoot, unprocessedSource, unprocessedMetaNodes);
            }
            else{
                //sourceLine is text of a paragraph
                Element textElement = doc.createElement("text");
                textElement.appendChild(doc.createTextNode(sourceByLine.get(sourceLineCount)));
                relativeRoot.appendChild(textElement);
            }
        }
    }

    private void auxXMLTreeBuilder(Document doc, Element relativeRoot, int relativeRootHie, Vector<String> sourceByLine, Vector<MetaNode> metaNodes){
        //System.out.println("relativeRootHie: " + relativeRootHie);
        while(sourceLineCount < sourceByLine.size() && metaNodes.get(metaNodesCount).getHierarchy() > relativeRootHie){
//            System.out.println("in the loop");
//            System.out.println("SourceByLine: " + sourceByLine.get(sourceLineCount));
//            System.out.println("SourceByLine, next: " + sourceByLine.get(sourceLineCount+1));
//            System.out.println("Label: " + metaNodes.get(metaNodesCount).getLabel());

            if(sourceByLine.get(sourceLineCount).equalsIgnoreCase(metaNodes.get(metaNodesCount).getLabel())){
                System.out.println("deciding if it's a paragraph or section");
                System.out.println("source line: " + sourceByLine.get(sourceLineCount));
                //if the line is a title
                if(metaNodes.get(metaNodesCount).getType() == MetaNode.nodeType.PARAGRAPH){
                    System.out.println("It's a paragraph");
                    //if this is a paragraph title
                    Element paragraphElement = doc.createElement("paragraph");

                    Element pTitle = doc.createElement("pTitle");
                    String pTitleString = sourceByLine.get(sourceLineCount);

                    pTitle.appendChild(doc.createTextNode(pTitleString));
                    paragraphElement.appendChild(pTitle);

                    paragraphElement.appendChild(doc.createElement("image"));
                    relativeRoot.appendChild(paragraphElement);

                    int thisParagraphHierarchy = metaNodes.get(metaNodesCount).getHierarchy();
                    sourceLineCount++;
                    metaNodesCount++;
                    //TODO: append paragraph should use a different function
                    //while next source line is a text, wrap it with a <text> tag
                    //and append it to the paragraph element
                    //if metaNodesCount == size of metaNodes, put the rest of the paragraph into the <text>
                    if(metaNodesCount == metaNodes.size()){
                        while(sourceLineCount < sourceByLine.size()){
                            Element textElement = doc.createElement("text");
                            textElement.appendChild(doc.createTextNode(sourceByLine.get(sourceLineCount)));
                            //print to console for testing
                            System.out.println(sourceByLine.get(sourceLineCount));
                            paragraphElement.appendChild(textElement);
                            sourceLineCount++;
                        }
                    }
                    else{
                        while(!(sourceByLine.get(sourceLineCount).equalsIgnoreCase(metaNodes.get(metaNodesCount).getLabel()))){
                            //while this line is not a title
                            //create an element
                            Element textElement = doc.createElement("text");
                            textElement.appendChild(doc.createTextNode(sourceByLine.get(sourceLineCount)));
                            //print to console for testing
                            System.out.println(sourceByLine.get(sourceLineCount));
                            paragraphElement.appendChild(textElement);
                            sourceLineCount++;
                        }
                    }
                }
                else if(metaNodes.get(metaNodesCount).getType() == MetaNode.nodeType.SECTION){
                    System.out.println("It's a section");
                    //else if this is a section title
                    Element sectionElement = doc.createElement("section");

                    Element sTitle = doc.createElement("sTitle");
                    String sTitleString = sourceByLine.get(sourceLineCount);

                    sTitle.appendChild(doc.createTextNode(sTitleString));
                    sectionElement.appendChild(sTitle);

                    relativeRoot.appendChild(sectionElement);
                    int thisSectionHierarchy = metaNodes.get(metaNodesCount).getHierarchy();
                    sourceLineCount++;
                    metaNodesCount++;
                    auxXMLTreeBuilder(doc, sectionElement, thisSectionHierarchy, sourceByLine, metaNodes);
                }
            }
            //must start with a title
//            else{
//                //else if the line is not a title: then it is <text>
//                Element textElement = doc.createElement("text");
//                textElement.appendChild(doc.createTextNode(sourceByLine.get(sourceLineCount)));
//
//                relativeRoot.appendChild(textElement);
//                sourceLineCount++;
//            }
        }
    }

    //input null typed metaNodes, output nodes with correct type: SECTION or PARAGRAPH
    //first metaNode should be title node and therefore of NULL type
    //this function seems right
    private Vector<MetaNode> processMetaNodes(Vector<MetaNode> metaNodes){
        Vector<MetaNode> resultVector = metaNodes;
        MetaNode first, second;
        for(int i = 1; i < metaNodes.size(); i++){
            if(i != metaNodes.size() - 1){
                first = metaNodes.get(i);
                second = metaNodes.get(i+1);
                if(first.getHierarchy()<second.getHierarchy())
                    resultVector.get(i).setType(MetaNode.nodeType.SECTION);
                else
                    resultVector.get(i).setType(MetaNode.nodeType.PARAGRAPH);
            }
            else{ //when we are at the last node
                //last node must be a paragraph
                resultVector.get(i).setType(MetaNode.nodeType.PARAGRAPH);
            }
            //System.out.println(metaNodes.get(i).getType());
        }

        return resultVector;
    }

}
