/**
 * Created by Hanchen on 4/26/14.
 */

public class xmlTranslatroRun {
    public static void main(String[] args) {
        String testFilePath = "dummy/Dummy Knowledge.txt";
        String metaFilePath = "dummy/Dummy Knowledge.gmeta";
//        TxtFileReader testReader = new TxtFileReader(testFilePath, metaFilePath);
        TxtFileReaderAndXmlGenerator test = new TxtFileReaderAndXmlGenerator(testFilePath,metaFilePath);
//        for(String e: testReader.sourceByLine){
//            System.out.println(e);
//        }

        test.generateXml();
    }
}
