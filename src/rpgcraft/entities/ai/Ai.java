/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities.ai;

import java.util.HashMap;
import java.util.Random;
import rpgcraft.entities.Entity;
import rpgcraft.entities.MovingEntity;

/**
 *
 * @author kirrie
 */
public abstract class Ai {          
    
    public static final Random random = new Random();    
    protected static HashMap<String, Ai> aiList = new HashMap();
    
    public abstract String getName();        
    
    public abstract boolean aiMove(MovingEntity e);            
    
}
