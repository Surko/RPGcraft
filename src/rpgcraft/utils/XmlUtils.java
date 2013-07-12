/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.util.ArrayList;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * Utility trieda ktora v sebe zdruzuje rozne metody pre pracu s xml subormi. Trieda je cela staticka =>
 * mozne k nej pristupovat z kazdej inej triedy ci instancie. 
 * Metoda dokaze rozparsovavat vrcholi podla urciteho mena vratit rozparsovane elementy.
 */
public class XmlUtils {
    
    /**
     * Metoda ktora rozparsuje vrchol zadany parametrom <b>node</b> a vrati take elementy
     * ktorych meno sa rovna <b>tagName</b>.
     * @param node Vrchol ktory parsujeme podla mena
     * @param tagName Meno vrcholov ktore chceme vybrat pri parsovanie
     * @return List s elementmi s menom ktore sa nachadzaju niekde vo vrchole .
     */
    public static ArrayList<Element> parseRootElements(Node node, String tagName) {
        ArrayList<Element> _elements = new ArrayList<>();
        Node n = node.getFirstChild();
        while ((n = n.getNextSibling()) != null) {                        
            if (!(n instanceof Element)) {continue;}
            Element elem = (Element)n;            
            if (elem.getNodeName().equals(tagName)) {
                _elements.add(elem);
            }
        }
        return _elements;
    }
    
    
}
