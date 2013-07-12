/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.xml;

import java.io.File;
import java.util.ArrayList;
import javax.xml.parsers.*;
import org.w3c.dom.*;
import rpgcraft.errors.MissingFile;
import rpgcraft.resource.StringResource;

/**
 * Trieda XMLReader ktora pri vytvoreni instancie zdruzuje metody na rozparsovanie
 * suboru ktory zadavame v konstruktore (alebo priamym parsovanim suboru). Vacsinou
 * sa vyuziva pri inicializovani xml suborov v GamePane, ktory vytvara pre kazdy xml subor
 * instanciu tejto triedy a rozparsovanim ziskame elementy, v ktorych sa nachadzaju data,
 * ktore potrebujeme. V triede takisto existuju rozne druhy parserov (rozparsuju iba plytke elementy,
 * vsetky elementy, od nejakeho elementu).
 */
public class XmlReader {
    
    private File file;
    private Document doc;
    private ArrayList<Element> elements = new ArrayList<>();   
    
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
        sounds(10);
               
        
        private int priority;
        
        /**
         * Privatny konstruktor pre vytvorenie priority pre enum
         * @param priority Priorita jednej polozky enumu
         */
        private XmlSavePriority(int priority) {
            this.priority = priority;
        }
        
        /**
         * Metoda ktora vrati prioritu pre jednu polozku enumu.
         * @return Priorita polozky enumu
         */
        public int getPriority() {
            return priority;                    
        }
    }
    
    /**
     * Konstruktory pre vytvorenie instancie XMLReaderu kde inicializujeme xml subor
     * na rozparsovanie parametrom <b>file</b>. Metody parseXmlFile sa postaraju
     * o nasledne rozparsovanie.
     * @param file Subor na rozparsovanie.
     */
    public XmlReader(String file) {
        this.file = new File(file);
    }

    /**
     * Prazdny konstruktor pre vytvorenie instancie XMLReaderu.
     */
    public XmlReader() {
        
    }
    
    /**
     * Metoda ktora rozparsuje subor ktoreho meno dostaneme ako parameter <b>sFile</b>.
     * Na rozparsovanie posluzi metoda parseXmlFile.
     * @param sFile Meno suboru ktory chceme rozparsovat
     */
    public void parseXmlFile(String sFile) {
        elements.clear();
        this.file = new File(sFile);
        parseXmlFile();
    }
    
    /**
     * Metoda ktora rozparsuje subor zadany parametrom <b>file</b>. Na rozparsovanie
     * sluzi metoda parseXmlFile.
     * @param file Subor na rozparsovanie
     */
    public void parseXmlFile(File file) {
        elements.clear();
        this.file = file;
        parseXmlFile();
    }
    
    /**
     * Metoda ktora rozparsuje xml subor dany parametrom <b>file</b>.  Na rozparsovanie
     * pouzivame DocumentBuilder.
     */
    private void parseXmlFile(){
	DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	try {
        	DocumentBuilder db = dbf.newDocumentBuilder();
	
        	doc = db.parse(file);

	}catch (Exception e) {
		new MissingFile(e, StringResource.getResource("_exmlread")).render();                                              
	}
    }
    
    /**
     * Metoda ktora vrati vrcholy s urcitym menom <b>tagName</b>. Prehladavame od 
     * root vrcholu rozparsovanim podla mena metodou getELementsByTagName.
     * @param tagName Meno pre element ktory hladame
     * @return List s vrcholmi vyhovujuce menu
     */
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
     * Metoda ktora prehlada zadany elementy a vrati vypis vsetkych atributov s menom
     * rovnym <b>tagName</b>.
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
    
    /**
     * Metoda ktora  vrati prvy vrchol v strome s menom <b>name</b>. Xml prechadzame 
     * odvrchu.
     * @param name Meno vrcholu ktory hladame
     * @return Vrchol s urcitym menom.
     */
    public Element getFirstElementByName(String name) {
        NodeList nl = doc.getElementsByTagName(name);
        if (nl.getLength()> 0) {
            return (Element)nl.item(0);
        }
        return null;
    } 
    
    /**
     * Metoda ktora vrati prvy vrchol v strome s menom <b>name</b>, ktoreho root element je zadany
     * parametrom <b>elem</b> 
     * @param name Meno vrcholu ktory hladame
     * @param elem Vrchol/Root element stromu v ktorom vyhladavame urcity tag.
     * @return Element s urcitym menom.
     */
    public static Element getFirstElementByName(String name, Element elem) {
        NodeList nl = elem.getElementsByTagName(name);
        if (nl.getLength()> 0) {
            return (Element)nl.item(0);
        }
        return null;
    } 
    
    /**
     * Metoda ktora vrati textovu hodnotu z tagu ktoreho meno je <b>tagName</b>.
     * Tag hladame vo vrchole zadanom parametrom <b>elem</b>
     * @param elem Vrchol v ktorom hladame text
     * @param tagName Nazov tagu
     * @return Text s hodnotou v urcitom tagu.
     */
    public String getTextValue(Element elem, String tagName) {
	String textVal = null;
	NodeList nl = elem.getElementsByTagName(tagName);
	if(nl != null && nl.getLength() > 0) {
		Element el = (Element)nl.item(0);
		textVal = el.getFirstChild().getNodeValue();
	}
	return textVal;
    }   
    
    /**
     * Metoda ktora vrati ciselnu reprezentaciu hodnoty ktora sa nachadza 
     * vo vrchole/tagu <b>ele</b s menom <b>tagName</b>.
     * @param ele Vrchol/tag xml suboru
     * @param tagName Meno tagu ktory chceme ziskat
     * @return Ciselna hodnota hodnoty v tagu
     */
    public int getIntValue(Element ele, String tagName) {		
        return Integer.parseInt(getTextValue(ele,tagName));
    }
    
    /**
     * Metoda ktora vrati atributu vo vrchole v xmlku zadanom parametrom <b>ele</b>.
     * Meno atributu je v parametri <b>tagName</b>.
     * @param ele Element/Vrchol v xml subore
     * @param tagName Meno atributu ktory chceme
     * @return Text s navratovym atributom
     */
    public String getAttribute(Element ele, String tagName) {
        return ele.getAttribute(tagName);
    }
    
    
}
