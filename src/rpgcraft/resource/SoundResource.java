/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.*;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.xml.SoundXML;

/**
 * -- NOT DONE --
 * Trieda ktora dedi po abstraktnej triede AbstractResource. Sluzi pre uchovanie
 * udajov o vsetkych zvukoch ktore sa v hre mozu vyskytnut.
 * V tomto stadiu nebude implementovana, ale v buducnosti to bude mozne.
 * -- NOT DONE --
 * @author Surko
 */
public class SoundResource extends AbstractResource<SoundResource> {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(RecipeResource.class.getName());
    private static HashMap<String, SoundResource> soundResources = new HashMap<>();
    private static final String DELIM = "[,]", BLANK = "";
    
    private String id;
    private ArrayList<String> soundName;
    private boolean repeat, shuffle;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor (privatny) ktory vytvori instanciu SoundResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme zvuk a vlozime ho k dalsim.
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private SoundResource(Element elem) {
        parse(elem);        
        soundResources.put(id, this);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vrati SoundResource podla parametru <b>name</b>. Name
     * je id v xml.
     * @param name Meno SoundResource ktore hladame
     * @return SoundResource s danym menom
     */
    public static SoundResource getResource(String name) {
        return soundResources.get(name);
    }    
            
    /**
     * Metoda ktora vytvori novy objekt typu SoundResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu SoundResource.
     * @param elem Element z ktoreho vytvarame zvuk
     * @return SoundResource z daneeho elementu
     */
    public static SoundResource newBundledResource(Element elem) {
        return new SoundResource(elem);                
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * Zatial nepouzite.
     * @param elem Element z xml ktory rozparsovavame do SoundResource
     */
    @Override
    protected void parse(Element elem) {
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {   
                case SoundXML.ID : {
                    this.id = eNode.getTextContent();
                } break;
                case SoundXML.PATH : {
                    this.soundName = new ArrayList<>();
                    String[] pPaths = eNode.getTextContent().split(DELIM);                    
                    for (String path : pPaths) {
                        if (path == null || path.equals(BLANK)) {
                            continue;
                        }
                        this.soundName.add(path);
                    }
                } break;
                case SoundXML.REPEAT : {
                    this.repeat = Boolean.parseBoolean(eNode.getTextContent());
                } break;
                case SoundXML.SHUFFLE : {
                    this.shuffle = Boolean.parseBoolean(eNode.getTextContent());
                } break;
                default : {
                    // Nepoznany prikaz ---> preskocenie
                    LOG.log(Level.INFO, StringResource.getResource(""));
                }                
            }
        }
    }
    // </editor-fold>   
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati meno/id nadefinovanych zvukov.
     * @return List s menami zvukov
     */
    public ArrayList<String> getSoundNames() {
        return soundName;
    }
    
    /**
     * Metoda ktora vrati ci sa bude zvuk opakovat.
     * @return True/false podla toho ci sa bude opakovat
     */
    public boolean isRepeatable() {
        return repeat;
    }
    
    /**
     * Metoda ktora vrati ci sa bude vykonavat shuffle/nahodne prehadzovanie pesniciek
     * @return Ture/false ci sa bude nahodne prehadzovat 
     */
    public boolean isShuffle() {
        return shuffle;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    /**
     * Metoda ktora zvaliduje entitu podla danych konceptov.
     */
    private void validate() {
        
    }
    
    /**
     * Metoda ktora skopiruje jeden SoundResource zadany v parametri <b>res</b>
     * do novo vytvoreneho resource.
     * @param res Resource z ktoreho kopirujeme do nasho resource udaje.
     * @throws Exception Vynimka pri nepodareni kopirovania.
     */
    @Override
    protected void copy(SoundResource res) throws Exception {
       
    }        
    // </editor-fold>
}
