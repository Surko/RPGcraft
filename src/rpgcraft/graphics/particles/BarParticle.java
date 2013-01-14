/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.particles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import rpgcraft.entities.Entity;
import rpgcraft.graphics.Colors;

/**
 *
 * @author doma
 */
public class BarParticle extends Particle {
    
    protected Double value;
    
    private int barWidth;
    private int barHeight;
    
    private Color color;
    

    
    public BarParticle() {
        this.barHeight = 6;
        this.activation = false;
        this.barWidth = 24;
        toDraw = new BufferedImage(barWidth, barHeight, BufferedImage.TYPE_INT_RGB);
        setGraphics();
    }
    
    public BarParticle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.barWidth = width;
        this.barHeight = height;
        toDraw = new BufferedImage(barWidth, barHeight, BufferedImage.TYPE_INT_RGB);        
    }
    
    private void setGraphics() {
        Graphics g = toDraw.getGraphics();
        g.setColor(Colors.getColor(Colors.fullBlack));
        g.fillRect(x, y, barWidth, barHeight);
    }
    
    private void increaseBar() {
        Graphics g = toDraw.getGraphics();        
        g.setColor(color);
        g.fillRect(0, 0, value.intValue(), barHeight);
    }
    
    public void setValue(double value) {
        this.value = value;
    }    

    /**
     * Metoda update ktora simuluje pohyb baru nad entitou. Zatial nad hracom.
     * Keby chceme aj nad dalsimi entitami treba doplnit x-ove a y-ove suradnice
     * vzhladom k hracovi.
     * V tomto pripade testujeme podmienku activation, ktora simuluje drzanie 
     * tlacidla na utocenie (ked sa drzi tak je aktivovane) a zabrani vsetkym updatom
     * nad touto casticou ( mozne pouzit ked nebudeme chcet vykreslit casticu ale 
     * chceme s casticou pohybovat a potom hu zobrazit ). Tym ze nevraciame hodnotu 
     * false tak nikdy tuto casticu z listu castic nevymazeme. Cim hu budeme mat stale v pamati
     * a bude zavisla len na premennej activation. Tymto sa zabrani stalou alokaciou pamate 
     * pri vytvarani novych instancii tejto castice, kedze v konecnom dosledku
     * budeme mat len jednu instanciu pre Entitu.
     * @return 
     */
    @Override
    public boolean update() {
        if (activation) {
            increaseBar();
        }
        
        return true;
    }
    
    public void incBy(double value) {
        this.value+=value;
    }
    
    @Override
    public void setDeactivated() {
        super.setDeactivated();
        setGraphics();
    }
    
    public void setActivated(Color color) {
        super.setActivated();
        this.color = color;
    }
    
}
