/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.panels.listeners.Action;
import rpgcraft.xml.QuestXML;

/**
 * QuestResource ktore dedi od AbstraktnehoResource je trieda ktora umoznuje
 * vytvorit quest resources z xml suborov, ktore sa daju pouzit v akciach.
 * Na vytvorenie nam pomaha metoda parse,
 * ktorej predavame jeden vrchol z xmlka na rozparsovanie. Vytvaranie noveho resource
 * prevadzame volanim metody newBundledResource. Navrat nejakeho resource pomocou metody
 * getResource. 
 * @see AbstractResource
 */
public class QuestsResource extends AbstractResource<QuestsResource> {        
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(QuestsResource.class.getName());
    private static HashMap<String, QuestsResource> questsResources = new HashMap();
    
    private String id, text, label;    
    private ArrayList<Action> actions;
    private int stateId = -1;
    private TreeMap<Integer, ArrayList<Action>> stateActions;
    private HashMap<Integer, String> stateTexts;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor (privatny) ktory vytvori instanciu QuestResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme konverzacnu grupu a vlozime hu do listu k dalsim.
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private QuestsResource(Element elem) {
        parse(elem);       
        validate();
        questsResources.put(id, this);
        actions = null;
        stateId = -1;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vytvori novy objekt typu QuestResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu QuestResource.
     * @param elem Element z ktoreho vytvarame quest resource
     * @return QuestResource z daneeho elementu
     */
    public static QuestsResource newBundledResource(Element elem) {                
        return new QuestsResource(elem);
    }                 
    
    /**
     * Metoda ktora vrati QuestResource podla parametru <b>questId</b>.
     * @param questId Id questoveho resource ktory hladame
     * @return QuestResource s danym menom
     */
    public static QuestsResource getQuest(String questId) {
        return questsResources.get(questId);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati id questu
     * @return Id ulohu
     */
    public String getId() {
        return id;
    }
    
    /**
     * Metoda ktora vrati nazov questu
     * @return Nazov ulohy
     */
    public String getLabel() {
        return label;
    }
    
    /**
     * Metoda ktora vrati textu quest
     * @return Text ulohy
     */
    public String getQuestText() {
        return text;
    }
    
    /**
     * Metoda ktora vrati text stavu zadaneho parametrom <b>stateId</b>
     * @param stateId Stav ktory chceme vratit
     * @return Text so stavom
     */
    public String getStateText(int stateId) {
        return stateTexts.get(stateId);
    }
            
    /**
     * Metoda ktora vrati vsetky mozne stavy ktore moze uloha dostat
     * @return Kolekcia vsetky textov pre questy
     */
    public Collection<String> getAllStateTexts() {
        return stateTexts.values();
    }
    
    /**
     * Metoda ktora vrati vsetky stavove akcie zo stavu zadaneho parametrom <b>stateId</b>
     * @param stateId Stav ktory chceme
     * @return Akcie na vykonanie z urciteho stavu
     */
    public ArrayList<Action> getStateActions(int stateId) {                
        return stateActions.floorEntry(stateId).getValue();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * (QuestXML.STATE -> ziska info z atributu a zavola rekurzivne parse na aktualny element
     * na ziskanie ostatnych quest stavov)
     * @param elem Element z xml ktory rozparsovavame do ConversationGroupResource
     */
    @Override
    protected void parse(Element elem) {
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {
                case QuestXML.ID : {
                    this.id = eNode.getTextContent();
                } break;  
                case QuestXML.QUESTSTATES : {
                    stateActions = new TreeMap();
                    stateTexts = new HashMap();
                    parse((Element)eNode);
                } break;
                case QuestXML.LABEL : {
                    this.label = eNode.getTextContent();
                } break;
                case QuestXML.STATE : {                    
                    Element state = (Element)eNode;                    
                    try {
                        stateId = Integer.parseInt(state.getAttribute(QuestXML.STATEID));                             
                    } catch (NumberFormatException e) {
                        LOG.log(Level.SEVERE, StringResource.getResource("_iparam", new String[] {
                            QuestXML.STATE, this.getClass().getName(), id}));
                        new MultiTypeWrn(e, Color.RED, StringResource.getResource("_iparam", new String[] {
                            QuestXML.STATE, this.getClass().getName(), id}),null).renderSpecific(
                                StringResource.getResource("_label_resourcerror"));
                    }                    
                    parse(state);
                    stateActions.put(stateId, actions);                    
                } break;
                case QuestXML.QUESTEXT : {
                    this.text = eNode.getTextContent();
                } break;
                case QuestXML.STATETEXT : {   
                    if (stateId != -1) {
                        stateTexts.put(stateId, eNode.getTextContent());
                    }
                } break;
                case QuestXML.STATEACTIONS : {
                    actions = new ArrayList<>();
                    parse((Element)eNode);
                } break; 
                case QuestXML.LUACTION : {
                    Action action = new Action();                    
                    action.setAction(eNode.getTextContent());
                    action.setType(Action.Type.EVENT);
                    action.setLua(true);
                                        
                    Element elemNode = (Element)eNode;                    
                    action.setMemorizable(Boolean.parseBoolean(elemNode.getAttribute(QuestXML.MEMORIZABLE)));                                                            
                    
                    if (elemNode.hasAttribute(QuestXML.ACTIONTYPE)) {
                        try {
                            ActionType type = ActionType.valueOf(elemNode.getAttribute(QuestXML.ACTIONTYPE));
                            action.setClickType(type);

                        } catch (Exception e) {
                            LOG.log(Level.WARNING, StringResource.getResource("_iparam", new String[] {QuestXML.ACTIONTYPE,
                                QuestXML.LUACTION, toString()}));
                            action.setClickType(ActionType.START);
                        }
                    } else {
                        action.setClickType(ActionType.START);
                    }
                    action.makeListener();
                    actions.add(action);
                } break;
                case QuestXML.ACTION : {
                    Action action = new Action();                    
                    action.setAction(eNode.getTextContent());
                    action.setType(Action.Type.EVENT);
                                        
                    Element elemNode = (Element)eNode;                    
                    action.setMemorizable(Boolean.parseBoolean(elemNode.getAttribute(QuestXML.MEMORIZABLE)));
                    
                    
                    if (elemNode.hasAttribute(QuestXML.SCRIPTYPE)) {
                        try {
                            switch (ScriptType.valueOf(elemNode.getAttribute(QuestXML.SCRIPTYPE))) {
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
                                    new String[] {QuestXML.SCRIPTYPE, QuestXML.ACTION, toString()}));
                        }
                    }
                    
                    if (elemNode.hasAttribute(QuestXML.ACTIONTYPE)) {
                        try {
                            ActionType type = ActionType.valueOf(elemNode.getAttribute(QuestXML.ACTIONTYPE));
                            action.setClickType(type);

                        } catch (Exception e) {
                            LOG.log(Level.WARNING, StringResource.getResource("_iattrib", new String[] {QuestXML.ACTIONTYPE,
                                QuestXML.ACTION, toString()}));
                            action.setClickType(ActionType.START);
                        }
                    } else {
                        action.setClickType(ActionType.START);
                    }                    
                    actions.add(action);
                } break;
            }
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    /**
     * Metoda toString vrati text s vypisom/zakladnymi informaciami o tomto resource.
     * Meno triedy + id.
     * @return String s popiskom objektu
     */
    @Override
    public String toString() {
        return this.getClass().getName() + ":" + id;
    }
   
    /**
     * Metoda ktora okopiruje quest resource z parametra <b>res</b> do tohoto resource.
     * @param res Resource z ktoreho kopirujeme
     * @throws Exception Chyba pri kopirovanie
     */
    @Override
    protected void copy(QuestsResource res) throws Exception {
        
    }        
    
    /**
     * Validacna metoda ktora zvaliduje resource aby bol pouzitelny.
     */
    private void validate() {
        
    }
    
    // </editor-fold>
    
}
