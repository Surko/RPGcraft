/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.errors;

import java.awt.Color;
import rpgcraft.graphics.Colors;

/**
 *
 * @author Kirrie
 */
public class MissingFile extends ErrorWrn {
    
    
    public MissingFile(Exception e,String msg) {
        this.e = e;
        this.cl = Colors.getColor(Colors.missError);
        this.msg = msg;        
        
    }
    
    public void render() {
        super.renderSpecific("RPGcraft!! --- MissingFile Error");
    }
    
}
