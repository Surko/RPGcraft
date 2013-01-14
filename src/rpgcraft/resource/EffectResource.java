/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.util.HashMap;
import org.w3c.dom.Element;

/**
 *
 * @author doma
 */
public class EffectResource extends AbstractResource<EffectResource>{

   
    public enum EffectEvent {
        ONUSE,
        ONEQUIP,
        ONSTRUCK,
        ONDROP,
        ONREAD,
        ONSELF
    }
    
    private static HashMap<String, EffectResource> effectResources = new HashMap<>();
    
    private String id;
    
    public static EffectResource getResource(String name) {
        return effectResources.get(name);
    }    
    
    private EffectResource(Element elem) {
       // parse(elem);
        
        effectResources.put(id, this);
    }
    
    public static EffectResource newBundledResource(Element elem) {
        return new EffectResource(elem);                
    }
    
    @Override
    protected void parse(Element elem) {
        
    }
    
     @Override
    protected void copy(EffectResource res) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
