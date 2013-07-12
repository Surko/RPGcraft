/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.xml;

/**
 * Interface ktory zdruzuje nazvy tagov v konverzacnch xml-kach
 */
public class ConversationXML {
    public static final String ROOT = "conversations";
    public static final String ELEMENT = "conversation";
    public static final String ID = "id";  
    public static final String TEXT = "text";
    public static final String GROUPS = "groups";
    public static final String CONDITIONS = "conditions";
    public static final String CONDITION = "condition";
    public static final String ANSWERS = "answers";
    public static final String ACTIONS = "actions";
    public static final String ACTION = "action";
    public static final String LUACTION = "lua-action";
    
    public static final String ACTIONTYPE = "type";
    public static final String SCRIPTYPE = "script-type";
    public static final String MEMORIZABLE = "memorizable";
}
