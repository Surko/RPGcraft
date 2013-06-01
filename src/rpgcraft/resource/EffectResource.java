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
 *
 * @author doma
 */
public class EffectResource extends AbstractResource<EffectResource>{
       
    private static final Logger LOG = Logger.getLogger(EffectResource.class.getName());
    private static HashMap<String, EffectResource> effectResources = new HashMap<>();
    
    private String id;
    private String name;  
    private Image image;
    private EffectType type;
    private int lifeSpan;
    private ArrayList<Action> actions;
    
    public static EffectResource getResource(String name) {
        return effectResources.get(name);
    }    
    
    private EffectResource(Element elem) {
       parse(elem);
       validate();
       effectResources.put(id, this);       
    }
    
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
    
    public static EffectResource newBundledResource(Element elem) {
        return new EffectResource(elem);                
    }
    
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
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }
    
    public EffectType getType() {
        return type;
    }
    
    public int getLifeSpan() {
        return lifeSpan;
    }
    
    public Image getImage() {
        return image;
    }
    
    public ArrayList<Action> getActions() {
        return actions;
    }
    
     @Override
    protected void copy(EffectResource res) throws Exception {
        
    }
    
}
