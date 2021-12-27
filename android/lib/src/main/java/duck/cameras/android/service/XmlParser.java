package duck.cameras.android.service;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;

import duck.cameras.android.model.TextNode;
import duck.cameras.android.model.XmlNode;

public class XmlParser {
    public static XmlNode parse(String input) {
        try {

            XmlPullParser xpp = Xml.newPullParser();
            xpp.setInput(new StringReader(input));

            XmlNode root = null;
            XmlNode current = null;

            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    if (current == null) {
                        root = new XmlNode(xpp.getName(), getAttributes(xpp));
                        current = root;
                    } else {
                        XmlNode node = new XmlNode(xpp.getName(), getAttributes(xpp));
                        current.add(node);
                        current = node;
                    }
                } else if (eventType == XmlPullParser.END_TAG) {
                    if (current != null) {
                        current = current.parent();
                    }
                } else if (eventType == XmlPullParser.TEXT) {
                    if (current != null) {
                        String text = xpp.getText();
                        if (text.trim().length() > 0) {
                            current.add(new TextNode(text));
                        }
                    }
                }
                eventType = xpp.next();
            }

            return root;

        } catch (XmlPullParserException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static HashMap<String, String> getAttributes(XmlPullParser xpp) {
        HashMap<String, String> result = new HashMap<>();
        for (int i = 0; i < xpp.getAttributeCount(); i++) {
            result.put(xpp.getAttributeName(i), xpp.getAttributeValue(i));
        }
        return result;
    }
}
