/**
 * Created by Hanchen on 5/10/14.
 */
public class MetaNode {
    private int hierarchy;
    private String label;
    private nodeType type;

    public nodeType getType() {
        return type;
    }

    public void setType(nodeType type) {
        this.type = type;
    }

    public enum nodeType{SECTION, PARAGRAPH, NULL}

    MetaNode(int hierarchy, String label){
        this.hierarchy = hierarchy;
        this.label = label;
        this.type = nodeType.NULL;
    }

    MetaNode(int hierarchy, String label, nodeType type){
        this.hierarchy = hierarchy;
        this.label = label;
        this.type = type;
    }

    public int getHierarchy() {
        return hierarchy;
    }

    public void setHierarchy(int hierarchy) {
        this.hierarchy = hierarchy;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString(){
        return "Node " + label + " with hierarchy " + hierarchy + ". It is type " + type;
    }
}
