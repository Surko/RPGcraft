/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.panels.listeners.Action;
import rpgcraft.plugins.Listener;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.xml.ConversationXML;

/**
 * ConversationResource ktore dedi od AbstraktnehoResource je trieda ktora umoznuje
 * vytvorit konverzacne resources z xml suborov, ktore sa daju pouzit v conversationgroup resource
 * ako mozne konverzacie ktore moze mat grupa. Na vytvorenie nam pomaha metoda parse,
 * ktorej predavame jeden vrchol z xmlka na rozparsovanie. Vytvaranie noveho resource
 * prevadzame volanim metody newBundledResource. Navrat nejakeho resource pomocou metody
 * getResource. 
 * @see AbstractResource
 */
public final class ConversationResource extends AbstractResource<ConversationResource> {               
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(ConversationResource.class.getName());
    private static final String DELIM = "[,]";
    private static HashMap<String, ConversationResource> conversations = new HashMap();
    
    private ArrayList<String> answerConversations;
    private String id;
    private String text;
    // Stacia listenery kedze su to len kontroly ktore by nemali byt nijako menene
    private ArrayList<Listener> conditions;
    private ArrayList<Action> actions;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor (privatny) ktory vytvori instanciu ConversationResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme konverzaciu a vlozime hu do listu k dalsim.
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private ConversationResource(Element elem) {
        parse(elem);
        validate();
        conversations.put(id, this);
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vrati ConversationResource podla parametru <b>id</b>.
     * @param id Id Konverzacie ktore hladame
     * @return ConversationResource s danym menom
     */
    public static ConversationResource getResource(String id) {
        return conversations.get(id);
    }
    
    /**
     * Metoda ktora vytvori novy objekt typu ConversationResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu ConversationResource.
     * @param elem Element z ktoreho vytvarame konverzaciu
     * @return ConversationResource z daneeho elementu
     */
    public static ConversationResource newBundledResource(Element elem) {
        return new ConversationResource(elem);
    }        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati co musi byt splnene (list listenerov ktore sa vykonavaju a urcuje sa navratova hodnota)
     * aby sa konverzacia zobrazila v konverzacnom okne
     * @return List s listenermi
     */
    public ArrayList<Listener> getConditions() {
        return conditions;
    }
    
    /**
     * Metoda ktora vrati akcie ktore sa vykonaju pri ukazani tejto konverzacie.
     * @return Akcie na vykonanie
     */
    public ArrayList<Action> getActions() {
        return actions;
    }
    
    /**
     * Metoda ktora vrati text pri rozpravani
     * @return Text rozpravania/konverzacie
     */
    public String getText() {
        return text;                
    }
    
    /**
     * Metoda ktora vrati list s textovymi hodnotami co sa odpovede mozme pouzit pri 
     * ukazani tejto konverzacie.
     * @return List s textami/id grup ktore sa zobrazia v konverzacnom okne
     */
    public ArrayList<String> getAnswerConversations() {
        return answerConversations;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * (ConversationXML.CONDITIONS -> vytvara list s listenermi. Zavolame metodu parse
     * na ziskanie tychto listenerov z podelementov aktualneho elemetnu).
     * @param elem Element z xml ktory rozparsovavame do ConversationResource
     */
    @Override
    protected void parse(Element elem) {
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {
                case ConversationXML.ID : {
                    this.id = eNode.getTextContent();
                } break;                
                case ConversationXML.TEXT : {
                    this.text = eNode.getTextContent();
                } break;                
                case ConversationXML.CONDITIONS : {
                    conditions = new ArrayList<>();
                    parse((Element)eNode);                    
                } break;
                case ConversationXML.CONDITION : {
                    Element elemNode = (Element)eNode;
                    boolean memorizable = Boolean.parseBoolean(elemNode.getAttribute(ConversationXML.MEMORIZABLE));
                    Listener condition = ListenerFactory.getListener(eNode.getTextContent(), memorizable);
                    conditions.add(condition);
                } break;
                case ConversationXML.ANSWERS : {
                    answerConversations = new ArrayList<>();
                    String[] answers = eNode.getTextContent().split(DELIM);
                    answerConversations.addAll(Arrays.asList(answers));
                } break;
                case ConversationXML.ACTIONS : {
                    actions = new ArrayList<>();
                    parse((Element)eNode);
                } break;
                case ConversationXML.ACTION : {                                        
                    Action action = new Action();                    
                    action.setAction(eNode.getTextContent());
                    action.setType(Action.Type.EVENT);
                                        
                    Element elemNode = (Element)eNode;                    
                    action.setMemorizable(Boolean.parseBoolean(elemNode.getAttribute(ConversationXML.MEMORIZABLE)));
                    
                    if (elemNode.hasAttribute(ConversationXML.SCRIPTYPE)) {
                        try {
                            switch (ScriptType.valueOf(elemNode.getAttribute(ConversationXML.SCRIPTYPE))) {
                                case LUA : {                                    
                                    action.setLua(true);
                                } break;
                                case LISTENER : {
                                    action.setLua(false);
                                }
                                default : break;
                            }
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, StringResource.getResource("_iparam", new String[] {ConversationXML.SCRIPTYPE,
                                ConversationXML.ACTION, toString()}));                            
                        }
                    }
                    
                    if (elemNode.hasAttribute(ConversationXML.ACTIONTYPE)) {
                        try {
                            ActionType type = ActionType.valueOf(((Element)eNode).getAttribute(ConversationXML.ACTIONTYPE));
                            action.setClickType(type);
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, StringResource.getResource("_iparam", new String[] {ConversationXML.ACTIONTYPE,
                                ConversationXML.ACTION, toString()}));
                            action.setClickType(ActionType.START);
                        }
                    } else {
                       action.setClickType(ActionType.START);                     
                    }
                    

                    actions.add(action);
                } break;
                case ConversationXML.LUACTION : {
                    Action action = new Action();      
                    action.setLua(true);
                    action.setAction(eNode.getTextContent());
                    action.setType(Action.Type.EVENT);
                                        
                    Element elemNode = (Element)eNode;                    
                    action.setMemorizable(Boolean.parseBoolean(elemNode.getAttribute(ConversationXML.MEMORIZABLE)));
                    
                    if (elemNode.hasAttribute(ConversationXML.ACTIONTYPE)) {
                        try {
                            ActionType type = ActionType.valueOf(((Element)eNode).getAttribute(ConversationXML.ACTIONTYPE));
                            action.setClickType(type);
                        } catch (Exception e) {
                            LOG.log(Level.WARNING, StringResource.getResource("_iparam", new String[] {ConversationXML.ACTIONTYPE,
                                ConversationXML.LUACTION, toString()}));
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
     * Metoda ktora okopiruje konverzacne resource z parametra <b>res</b> do tohoto resource.
     * @param res Resource z ktoreho kopirujeme
     * @throws Exception Chyba pri kopirovanie
     */
    @Override
    protected void copy(ConversationResource res) throws Exception {
        this.id = res.id;
        this.text = res.text;
        this.conditions = new ArrayList(res.conditions);
        this.actions = new ArrayList(res.actions);
        this.answerConversations = new ArrayList(res.answerConversations);        
    }
    
    /**
     * Validacna metoda ktora zvaliduje resource aby bol pouzitelny.
     */
    private void validate() {
        
    }
    
    // </editor-fold>
}
