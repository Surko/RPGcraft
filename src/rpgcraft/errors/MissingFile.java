/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.errors;

import java.awt.Color;

/**
 *
 * @author Kirrie
 */
public class MissingFile extends ErrorWrn {
    
    
    public MissingFile(Exception e,Color cl,String msg) {
        this.e = e;
        this.cl = cl;
        this.msg = msg;        
        
    }
    
    public void render() {
        super.renderSpecific("RPGcraft!! --- MissingFile Error");
    }
    
}
