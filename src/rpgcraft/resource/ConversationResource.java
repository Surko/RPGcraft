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
import rpgcraft.panels.listeners.Listener;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.xml.ConversationXML;

/**
 *
 * @author kirrie
 */
public final class ConversationResource extends AbstractResource<ConversationResource> {        
    
    private static final Logger LOG = Logger.getLogger(ConversationResource.class.getName());
    private static final String DELIM = "[,]";
    private static HashMap<String, ConversationResource> conversations = new HashMap();
    
    private ArrayList<String> answerConversations;
    private String id;
    private String text;
    // Stacia listenery kedze su to len kontroly ktore by nemali byt nijako menene
    private ArrayList<Listener> conditions;
    private ArrayList<Action> actions;
    
    
    private ConversationResource(Element elem) {
        parse(elem);
        validate();
        conversations.put(id, this);
    }
    
    public static ConversationResource newBundledResource(Element elem) {
        return new ConversationResource(elem);
    }
    
    private void validate() {
        
    }
    
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

    /**
     * Metoda toString vrati text s vypisom/zakladnymi informaciami o tomto resource.
     * Meno triedy + id.
     * @return String s popiskom objektu
     */
    @Override
    public String toString() {
        return this.getClass().getName() + ":" + id;
    }
    
    @Override
    protected void copy(ConversationResource res) throws Exception {
        this.id = res.id;
        this.text = res.text;
        this.conditions = new ArrayList(res.conditions);
        this.actions = new ArrayList(res.actions);
        this.answerConversations = new ArrayList(res.answerConversations);        
    }
    
}
