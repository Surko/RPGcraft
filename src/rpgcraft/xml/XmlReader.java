/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.xml;

import java.awt.Color;
import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import rpgcraft.errors.MissingFile;
/**
 *
 * @author Kirrie
 */
public class XmlReader {
    
    private File file;
    private Document doc;
    private ArrayList<Element> elements = new ArrayList<Element>();   
    
    /**
     * Enum XmlSavePriority ktory ma definovane mena xml suborov s priradenymi cislami
     * ktore predstavuju priority. Priority su radene od najmensieho po najvacsie
     * kde najmensi znamena najvacia priorita. Priority zvacsujeme po jednom.
     * Metoda getPriority vrati priority pre enum.
     * 
     */
    public enum XmlSavePriority {
        data(0),
        images(1),
        ui(2),
        tiles(3),
        effects(4),
        conversations(5),        
        items(6),
        entities(7),
        recipes(8),        
        quests(9),
        groups(10);        
               
        
        private int priority;
        
        private XmlSavePriority(int priority) {
            this.priority = priority;
        }
        
        public int getPriority() {
            return priority;                    
        }
    }
    
    public XmlReader(String file) {
        this.file = new File(file);
    }

    public XmlReader() {
        
    }
    
    public void parseXmlFile(String sFile) {
        elements.clear();
        this.file = new File(sFile);
        parseXmlFile();
    }
    
    public void parseXmlFile(File file) {
        this.file = file;
        parseXmlFile();
    }
    
    private void parseXmlFile(){
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	try {
        	DocumentBuilder db = dbf.newDocumentBuilder();
	
        	doc = db.parse(file);

	}catch (Exception e) {
		new MissingFile(e, "Chyba pri citani XML").render();                                              
	}
    }
    
    public ArrayList<Element> parseElements(String tagName) {
        NodeList nl = doc.getElementsByTagName(tagName);
	if(nl != null && nl.getLength() > 0) {
            for(int i = 0 ; i < nl.getLength();i++) {
		elements.add((Element)nl.item(i));
            }
        }
        return elements;
    }
    
    /**
    * Metoda parseChildElements sluzi na vybratie takych prvkov z xml suboru, ktore su 
    * priamymi potomkami prvku zadaneho parametrom rootName a ich meno je rovnake ako parameter tagName
    * @param rootName Meno hlavneho prvku z ktoreho ziskavame priamych potomkov
    * @param tagName Meno prvkov na vyber
    * @return ArrayList prvkov zodpovedajucim parametru tagName
    */
    public ArrayList<Element> parseChildElements(String rootName, String tagName) {
        NodeList rl = doc.getElementsByTagName(rootName);
        
        NodeList nl = rl.item(0).getChildNodes();
        int l = nl.getLength();
        if (nl != null && nl.getLength() > 0) {
            for (int i = 1; i < nl.getLength(); i+=2) {
                Element elem = (Element)nl.item(i);
                if (elem.getNodeName().equals(tagName))
                    elements.add(elem);
            }        
        }
        return elements;
    }
    
    /**
    * Metoda parseChildElements sluzi na vybratie takych prvkov z xml suboru, ktore su 
    * priamymi potomkami korenoveho prvku a ich meno je rovnake ako parameter tagName
    * @param tagName Meno prvkov na vyber
    * @return ArrayList prvkov zodpovedajucim parametru tagName
    */
    public ArrayList<Element> parseRootElements(String tagName) {        
        NodeList nl = doc.getFirstChild().getChildNodes();
        if (nl != null && nl.getLength() > 0) {
            for (int i = 1; i < nl.getLength(); i+=2) {
                Element elem = (Element)nl.item(i);
                if (elem.getNodeName().equals(tagName))
                    elements.add(elem);
            }        
        }
        return elements;
    }
    
    
    
    /**
     * 
     * @param tagName
     * @return 
     */
    public ArrayList<String> parseAttributes(String tagName) {
        ArrayList<String> attributes = new ArrayList<>();
        for (Element elem:elements) {
            attributes.add(elem.getAttribute(tagName));
        }
        return attributes;        
    }
    
    /**
     * Metoda getTagValue sluzi na vratenie hodnoty pre vrchol ktory je potomkom
     * vrcholu zadaneho parametrom elem (struktura stromu) s menom zadanym
     * parametrom sTag
     * @param sTag Meno vrcholu ktory potrebujeme
     * @param elem Vrchol z ktoreho chcem ziskat nas prvok
     * @return Hodnota/meno vrcholu ziskaneho metodou
     */
    public static String getTagValue(String sTag, Element elem) {
        NodeList nl = elem.getElementsByTagName(sTag).item(0).getChildNodes();
        
        Node nValue = (Node)nl.item(0);
        
        return nValue.getNodeValue();
    }
    
    public Element getFirstElementByName(String name) {
        NodeList nl = doc.getElementsByTagName(name);
        if (nl.getLength()> 0) {
            return (Element)nl.item(0);
        }
        return null;
    } 
    
    public static Element getFirstElementByName(String name, Element elem) {
        NodeList nl = elem.getElementsByTagName(name);
        if (nl.getLength()> 0) {
            return (Element)nl.item(0);
        }
        return null;
    } 
    
    public String getTextValue(Element elem, String tagName) {
	String textVal = null;
	NodeList nl = elem.getElementsByTagName(tagName);
	if(nl != null && nl.getLength() > 0) {
		Element el = (Element)nl.item(0);
		textVal = el.getFirstChild().getNodeValue();
	}
	return textVal;
    }   
    
    public int getIntValue(Element ele, String tagName) {		
        return Integer.parseInt(getTextValue(ele,tagName));
    }
    
    public String getAttribute(Element ele, String tagName) {
        return ele.getAttribute(tagName);
    }
    
    
}
