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
    
    protected abstract void parse(Element elem);
    protected abstract void copy(T res) throws Exception;
}
