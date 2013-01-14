/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import com.sun.org.apache.xerces.internal.dom.DeferredTextImpl;
import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author kirrie
 */
public class XmlUtils {
    
    public static ArrayList<Element> parseRootElements(Node node, String tagName) {
        ArrayList<Element> _elements = new ArrayList<>();
        Node n = node.getFirstChild();
        while ((n = n.getNextSibling()) != null) {                        
            if (n instanceof DeferredTextImpl) {continue;}
            Element elem = (Element)n;            
            if (elem.getNodeName().equals(tagName)) {
                _elements.add(elem);
            }                   
        }
        return _elements;
    }
    
    
}
