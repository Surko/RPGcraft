/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import org.w3c.dom.Element;

/**
 *
 * @author Surko
 */
public abstract class AbstractResource<T extends AbstractResource> {
    
    public enum ScriptType {
        LISTENER,
        LUA
    }
    
    public enum ActionType {
        START,
        THROUGHT,
        END
    }
    
    protected abstract void parse(Element elem);
    protected abstract void copy(T res) throws Exception;
}
