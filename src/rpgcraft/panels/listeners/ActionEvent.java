/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.listeners;

/**
 *
 * @author kirrie
 */
public class ActionEvent extends java.awt.event.ActionEvent{

    private int clicks;
    private Object param;
    
    public ActionEvent(Object source, int id, int clicks, String command, Object param) {
        super(source, id, command);
        this.clicks = clicks;
        this.param = param;
    }
    
    public int getClicks() {
        return clicks;
    }    
    
    public Object getParam() {
        return param;
    }
    
}
