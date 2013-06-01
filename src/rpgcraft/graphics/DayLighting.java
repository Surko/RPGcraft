/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics;

import java.awt.Color;

/**
 *
 * @author doma
 */
public class DayLighting {
    private static final float MORNING = 0.5f;
    private static final float LUNCH = 1f;
    private static final float EVENING = 0.5f;
    private static final float NIGHT = 0.0f;
    
    private Color[] colors;
    private float[] fractions;
    private int radBuff = 0;
            
    public DayLighting(int gameTime) {
        init(gameTime);
    }
    
    public final void init(int time) {
        if ((time < 6)||(time >= 20)) {
            this.colors = new Color[] {Colors.getColor(Colors.eveningColor),
                Colors.getColor(Colors.nightColor),Colors.getColor(Colors.fullBlack)};
            this.fractions = new float[] {0.1f, 0.5f, 1.0f};
            this.radBuff = -96;
            return;
        }
        if (time < 10) {
            this.colors = new Color[] {Colors.getColor(Colors.morningColor),
                Colors.getColor(Colors.eveningColor),Colors.getColor(Colors.fullBlack)};
            this.fractions = new float[] {0.1f, 0.5f, 1.0f};
            this.radBuff = -64;
            return;
        }
        if (time < 16) {
            this.colors = new Color[] {Colors.getColor(Colors.transparentColor),
                Colors.getColor(Colors.morningColor),Colors.getColor(Colors.fullBlack)};
            this.fractions = new float[] {0.4f, 0.5f, 1.0f};
            this.radBuff = 0;
            return;
        }
        
        if (time < 20) {
            this.colors = new Color[] {Colors.getColor(Colors.transparentColor),
                Colors.getColor(Colors.eveningColor),Colors.getColor(Colors.fullBlack)};
            this.fractions = new float[] {0.4f, 0.5f, 1.0f};
            this.radBuff = -64;

        }
        
    }    

    
    public int getRadiusBuff() {
        return radBuff;
    }
    
    public Color[] getColors() {
        return colors;
    }
    
    public float[] getFractions() {
        return fractions;
    }
}
