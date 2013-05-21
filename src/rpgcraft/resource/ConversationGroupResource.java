/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.util.HashMap;
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
    private String[] members;
    
    private ConversationGroupResource(Element elem) {
        parse(elem);
        validate();
        groupResources.put(id, this);
    }
    
    private void validate() {
        
    }
    
    public static ConversationGroupResource newBundledResource(Element elem) {
        return new ConversationGroupResource(elem);
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
                    members = eNode.getTextContent().split(DELIM);
                } break;                    
            }
        }
    }

    @Override
    protected void copy(ConversationGroupResource res) throws Exception {
        this.id = res.id;
        this.label = res.label;
        this.members = res.members;
    }
    
}
