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
public class MultiTypeWrn extends ErrorWrn {
    
    public MultiTypeWrn(Exception e,Color cl,String msg, String[] param) {
        this.e = e;
        this.cl = cl;
        this.msg = String.format(msg, param);        
    }

    @Override
    public void renderSpecific(String errorType) {
        super.renderSpecific(errorType);
    }
    
    
    
    
}
