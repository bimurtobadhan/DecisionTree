/**
 * Created by bimurto on 27-Nov-15.
 */
public class Node {
    int attrType;
    int attrvalue;
    int label;
    Node parent;
    Node child[];

    public Node(){
        attrType = -1;
        attrvalue = -1;
        parent = null;
        child = new Node[11];
        label = -1;
    }
}
