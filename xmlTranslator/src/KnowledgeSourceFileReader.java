import java.util.Vector;

/**
 * Created by Hanchen on 4/26/14.
 */
public abstract class KnowledgeSourceFileReader {
    protected String sourceFileType;
    protected Vector<String> metaByLine = new Vector<String>();
    protected Vector<String> sourceByLine = new Vector<String>();

    public String getType(){
        return sourceFileType;
    }


}
