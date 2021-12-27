package duck.cameras.android.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class XmlNode implements Node {
    private String name;
    private Map<String, String> attributes;
    private XmlNode parent;
    private ArrayList<Node> children;

    public XmlNode(String name, Map<String, String> attributes) {
        this.name = name;
        this.attributes = (attributes != null) ? attributes : new HashMap<>();
        this.children = new ArrayList<>();
    }

    public String name() {
        return this.name;
    }

    public Iterable<Node> children() {
        return this.children;
    }

    public void add(Node node) {
        node.parent(this);
        children.add(node);
    }

    public XmlNode get(String name) {
        for (Node child : children()) {
            if (child instanceof XmlNode) {
                XmlNode xmlNode = (XmlNode) child;
                if (xmlNode.name().equals(name)) {
                    return xmlNode;
                }
            }
        }
        return null;
    }

    public Iterable<XmlNode> getAll(String name) {
        ArrayList<XmlNode> result = new ArrayList<>();
        for (Node child : children()) {
            if (child instanceof XmlNode) {
                XmlNode xmlNode = (XmlNode) child;
                if (xmlNode.name().equals(name)) {
                    result.add(xmlNode);
                }
            }
        }
        return result;
    }

    public String attr(String name) {
        if (attributes.containsKey(name)) {
            return attributes.get(name);
        }
        return null;
    }

    @Override
    public String value() {
        StringBuilder builder = new StringBuilder();
        buildString(builder, children(), "");
        return builder.toString().trim();
    }

    @Override
    public XmlNode parent() {
        return this.parent;
    }

    @Override
    public void parent(XmlNode parent) {
        this.parent = parent;
    }

    private void buildString(StringBuilder builder, Iterable<Node> nodes, String indent) {
        for (Node node : nodes) {
            if (node instanceof TextNode) {
                builder.append(indent).append(node.value()).append("\n");
            }  else if (node instanceof XmlNode) {
                XmlNode xmlNode = (XmlNode) node;
                builder.append(indent).append(xmlNode.name()).append("\n");
                buildString(builder, xmlNode.children(), indent + "\t");
            }
        }
    }
}
