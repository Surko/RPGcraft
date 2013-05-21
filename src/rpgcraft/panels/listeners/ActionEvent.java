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
    private Action action;
    private Object returnValue;
    private int jumpValue = 1;
    
    public ActionEvent(Object source, int id, int clicks, String command, Object param) {
        super(source, id, command);
        this.clicks = clicks;
        this.param = param;        
    }
    
    public void setReturnValue(Object value) {
        this.returnValue = value;
    }
    
    public void setAction(Action action) {
        this.action = action;
    }
    
    public void setJumpValue(int jumpValue) {
        this.jumpValue = jumpValue;
    }
    
    public int getJumpValue() {        
        return jumpValue; 
    }
    
    public int getClicks() {
        return clicks;
    }    
    
    public Action getAction() {
        return action;
    }
    
    public Object getParam() {
        return param;
    }
    
    public Object getReturnValue() {
        return returnValue;
    }
    
}
