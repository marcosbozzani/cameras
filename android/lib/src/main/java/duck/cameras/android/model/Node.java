package duck.cameras.android.model;

public interface Node {
    String value();
    XmlNode parent();
    void parent(XmlNode parent);
}
