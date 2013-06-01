/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.xml.ConversationGroupXML;

/**
 *
 * @author kirrie
 */
public final class ConversationGroupResource extends AbstractResource<ConversationGroupResource> {
    private static final Logger LOG = Logger.getLogger(ConversationGroupResource.class.getName());
    private static final String DELIM = "[,]";
    private static HashMap<String, ConversationGroupResource> groupResources = new HashMap();
    
    private String id, label;
    private ArrayList<ConversationResource> members;
    
    private ConversationGroupResource(Element elem) {
        parse(elem);
        validate();
        groupResources.put(id, this);
    }
    
    private void validate() {
        
    }
    
    public static ConversationGroupResource getResource(String id) {
        return groupResources.get(id);
    }
    
    public static ConversationGroupResource newBundledResource(Element elem) {
        return new ConversationGroupResource(elem);
    }
    
    public ArrayList<ConversationResource> getMembers() {
        return members;
    }
    
    public String getLabel() {
        return label;
    }
    
    public String getId() {
        return id;
    }
    
    @Override
    protected void parse(Element elem) {
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {
                case ConversationGroupXML.ID : {
                    this.id = eNode.getTextContent();
                } break;
                case ConversationGroupXML.LABEL : {
                    this.label = eNode.getTextContent();
                } break;
                case ConversationGroupXML.MSGS : {
                    String[] sMembers = eNode.getTextContent().split(DELIM);
                    members = new ArrayList<>();
                    for (String s : sMembers) {
                        members.add(ConversationResource.getResource(s));
                    }
                } break;                    
            }
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final ConversationGroupResource other = (ConversationGroupResource) obj;

        if (!this.id.equals(other.id)) {
            return false;
        }
        return true;                 
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.id);
        return hash;
    }

    
    
    @Override
    protected void copy(ConversationGroupResource res) throws Exception {
        this.id = res.id;
        this.label = res.label;
        this.members = res.members;
    }
    
}
