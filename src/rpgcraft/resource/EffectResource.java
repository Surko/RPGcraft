/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.awt.Image;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.effects.Effect.EffectType;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.panels.listeners.Action;
import rpgcraft.xml.EffectXML;

/**
 * EffectResource ktore dedi od AbstraktnehoResource je trieda ktora umoznuje
 * vytvorit effect resources z xml suborov, ktore sa daju pouzit v entitovych resource
 * ako mozne efekty ktore moze entita mat alebo poslat inej entite. Na vytvorenie nam pomaha metoda parse,
 * ktorej predavame jeden vrchol z xmlka na rozparsovanie. Vytvaranie noveho resource
 * prevadzame volanim metody newBundledResource. Navrat nejakeho resource pomocou metody
 * getResource. 
 * @see AbstractResource
 */
public class EffectResource extends AbstractResource<EffectResource>{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">   
    private static final Logger LOG = Logger.getLogger(EffectResource.class.getName());
    private static HashMap<String, EffectResource> effectResources = new HashMap<>();
    
    private String id;
    private String name;  
    private Image image;
    private EffectType type;
    private int lifeSpan;
    private ArrayList<Action> actions;            
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor (privatny) ktory vytvori instanciu EffecResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme effekt a vlozime ho do listu k dalsim.
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private EffectResource(Element elem) {
       parse(elem);
       validate();
       effectResources.put(id, this);       
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">        
    
    /**
     * Metoda ktora vrati EffectResource podla parametru <b>name</b>.
     * @param name Meno efektu ktory hladame (meno je id nachadzajuce sa v xml)
     * @return EffectResource s danym menom
     */
    public static EffectResource getResource(String name) {
        return effectResources.get(name);
    }
    
    /**
     * Metoda ktora vytvori novy objekt typu EffectResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu EffectResource.
     * @param elem Element z ktoreho vytvarame efekt
     * @return EffectResource z daneeho elementu
     */
    public static EffectResource newBundledResource(Element elem) {
        return new EffectResource(elem);                
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * (EffectXML.ACTION -> rozparsuje to co je medzi tagmi, a v atributoch. Z tychto informacii
     * vytvori akciu ktoru prida do resource.
     * @param elem Element z xml ktory rozparsovavame do ConversationGroupResource
     */
    @Override
    protected void parse(Element elem) {
        NodeList nl = elem.getChildNodes();        
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {
                case EffectXML.ID : {                       
                    this.id = eNode.getTextContent();
                } break;
                case EffectXML.NAME : {                       
                    this.name = eNode.getTextContent();
                } break;
                case EffectXML.TYPE : {
                    try {
                        type = EffectType.valueOf(eNode.getTextContent());
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, StringResource.getResource("_rparam", new String[] {EffectXML.TYPE, id}));                        
                        return;
                    }
                } break;
                case EffectXML.IMAGE : {
                    this.image = ImageResource.getResource(eNode.getTextContent()).getBackImage();
                } break;
                case EffectXML.LIFESPAN : {
                    this.lifeSpan = Integer.parseInt(eNode.getTextContent());
                } break;                
                case EffectXML.ACTIONS : {
                    actions = new ArrayList<>();
                    parse((Element)eNode);
                } break;
                case EffectXML.ACTION : {
                    Action action = new Action();                    
                    action.setAction(eNode.getTextContent());
                    action.setType(Action.Type.EVENT);
                                        
                    Element elemNode = (Element)eNode;                    
                    action.setMemorizable(Boolean.parseBoolean(elemNode.getAttribute(EffectXML.MEMORIZABLE)));
                    
                    
                    if (elemNode.hasAttribute(EffectXML.SCRIPTYPE)) {
                        try {
                            switch (ScriptType.valueOf(elemNode.getAttribute(EffectXML.SCRIPTYPE))) {
                                case LUA : {                                    
                                    action.setLua(true);
                                } break;
                                case LISTENER : {
                                    action.setLua(false);
                                    action.makeListener();
                                }
                                default : break;
                            }
                        } catch (Exception e) {
                            LOG.log(Level.WARNING,StringResource.getResource("_iattrib",
                                    new String[] {EffectXML.SCRIPTYPE, EffectXML.ACTION, toString()}));
                        }
                    }
                    
                    if (elemNode.hasAttribute(EffectXML.ACTIONTYPE)) {
                        try {
                            ActionType type = ActionType.valueOf(elemNode.getAttribute(EffectXML.ACTIONTYPE));
                            action.setClickType(type);

                        } catch (Exception e) {
                            LOG.log(Level.WARNING, StringResource.getResource("_iattrib", new String[] {EffectXML.ACTIONTYPE,
                                EffectXML.ACTION, toString()}));
                            action.setClickType(ActionType.START);
                        }
                    } else {
                        action.setClickType(ActionType.START);
                    }                    
                    actions.add(action);
                }
            }
        }
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati meno efektu
     * @return Meno efektu
     */
    public String getName() {
        return name;
    }
    
    /**
     * Metoda ktora vrati id efektu
     * @return Id efektu
     */
    public String getId() {
        return id;
    }
    
    /**
     * Metoda ktora vrati typ efektu
     * @return Typ efektu
     */
    public EffectType getType() {
        return type;
    }
    
    /**
     * Metoda ktora vrati ako dlho vydrzi efekt
     * @return Dlzka efektu
     */
    public int getLifeSpan() {
        return lifeSpan;
    }
    
    /**
     * Metoda ktora vrati obrazok efektu
     * @return Obrazok efektu.
     */
    public Image getImage() {
        return image;
    }
    
    /**
     * Metoda ktora vrati akcie na vykonanie pri aktivacii efektu.
     * @return List s akciami na vykonanie pri efekte.
     */
    public ArrayList<Action> getActions() {
        return actions;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    
    /**
     * Metoda ktora okopiruje efekt resource z parametra <b>res</b> do tohoto resource.
     * @param res Resource z ktoreho kopirujeme
     * @throws Exception Chyba pri kopirovanie
     */
     @Override
    protected void copy(EffectResource res) throws Exception {
        
    }
     
    /**
    * Validacna metoda ktora zvaliduje resource aby bol pouzitelny.
    */
    private void validate() {
        if (id == null) {
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_midres",
                    new String[] {this.getClass().getName()}), null).renderSpecific(StringResource.getResource("_label_resourcerror"));
        }
        if (type == null) {
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_rparam",
                    new String[] {EffectXML.TYPE, id}), null).renderSpecific(StringResource.getResource("_label_resourcerror"));            
        }
                
    }
    // </editor-fold>
    
}
