/**
 * Created by Hanchen on 4/26/14.
 */

public class xmlTranslatroRun {
    public static void main(String[] args) {
        String testFilePath = "Empire_Angevin.txt";
        String metaFilePath = "meta";
        TxtFileReader testReader = new TxtFileReader(testFilePath, metaFilePath);

//        for(String e: testReader.sourceByLine){
//            System.out.println(e);
//        }

        testReader.generateXml();
    }
}
