/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.panels.listeners.Action;
import rpgcraft.xml.QuestXML;

/**
 *
 * @author kirrie
 */
public class QuestsResource extends AbstractResource<QuestsResource> {        
    
    private static final Logger LOG = Logger.getLogger(QuestsResource.class.getName());
    private static HashMap<String, QuestsResource> questsResources = new HashMap();
    
    private String id, text, label;    
    private ArrayList<Action> actions;
    private int stateId = -1;
    private HashMap<Integer, ArrayList<Action>> stateActions;
    private HashMap<Integer, String> stateTexts;
    
    private QuestsResource(Element elem) {
        parse(elem);       
        validate();
        questsResources.put(id, this);
        actions = null;
        stateId = -1;
    }
    
    public static QuestsResource newBundledResource(Element elem) {                
        return new QuestsResource(elem);
    }
    
    private void validate() {
        
    }
            
    public static QuestsResource getQuest(String questId) {
        return questsResources.get(questId);
    }
    
    public String getId() {
        return id;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getQuestText() {
        return text;
    }
    
    public String getStateText(int stateId) {
        return stateTexts.get(stateId);
    }
            
    public Collection<String> getAllStateTexts() {
        return stateTexts.values();
    }
    
    public ArrayList<Action> getStateActions(int stateId) {
        return stateActions.get(stateId);
    }
    
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
                    stateActions = new HashMap();
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
                    action.makeListener();
                    actions.add(action);
                } break;
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
    protected void copy(QuestsResource res) throws Exception {
        
    }        
    
}
