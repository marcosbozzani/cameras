package duck.cameras.android.model;

public class TextNode implements Node {
    private String value;
    private XmlNode parent;

    public TextNode(String value) {
        this.value = value;
    }

    @Override
    public String value() {
        return this.value;
    }

    @Override
    public XmlNode parent() {
        return this.parent;
    }

    @Override
    public void parent(XmlNode parent) {
        this.parent = parent;
    }
}
