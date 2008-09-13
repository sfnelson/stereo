package util.node;

public abstract class Node {
    public final int code;
    
    public Node(int code) {
        this.code = code;
    }
    
    public static String intToCode(int code) {
        return "" + (char) (code >> 24) + (char) ((code >> 16) % 256) + (char) ((code >> 8) % 256) + (char) (code % 256);
    }

    public String toString() {
        return intToCode(code);
    }
    
    public abstract int visit(Visitor visitor);
    
}