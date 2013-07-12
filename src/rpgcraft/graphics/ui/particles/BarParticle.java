/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.particles;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import rpgcraft.graphics.Colors;

/**
 * Trieda ktora dedi od Particle sa automaticky stava casticou ktora sa moze nachadzat v
 * liste castic na vykreslenie. Instancia triedy ma podedene vsetky metody z rodica
 * + ma premenne ktore vystihuju tuto bar casticu. Najzakladnejsie su farba vyplnovaceho
 * obdlznika v bare a hodnota pokial vyplnujeme casticu.
 */
public class BarParticle extends Particle {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final int DEFWIDTH = 24, DEFHEIGHT = 6;
    protected Double value = 0d;
    
    private int barWidth;
    private int barHeight;
    
    private Color color;  
    // </editor-fold>
      
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Metoda ktora vytvori default instanciu bar castice. Inicializujeme sirku a vysku
     * castice podla default hodnot s prislusnym obrazkom s obsahom castice
     */
    public BarParticle() {
        this.barHeight = DEFHEIGHT;
        this.activation = false;
        this.barWidth = DEFWIDTH;
        toDraw = new BufferedImage(barWidth, barHeight, BufferedImage.TYPE_INT_RGB);
        setGraphics();
    }
    
    /**
     * Konstruktor ktory vytvori instanciu bar castice. Inicializujeme x-ove a y-ove pozicie
     * vzhladom k hracovej entity ako aj sirku a vysku castice s prislusnym obrazkom
     * s obsahom castice
     * @param x X-ova pozicia vzhladom k hracovej entite
     * @param y Y-ova pozicia vzhladom k hracovej entite
     * @param width Sirka castice
     * @param height Vyska castice
     */
    public BarParticle(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.barWidth = width;
        this.barHeight = height;
        toDraw = new BufferedImage(barWidth, barHeight, BufferedImage.TYPE_INT_RGB);         
    }
    // </editor-fold>            

    // <editor-fold defaultstate="collapsed" desc=" Update ">
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
     * @return True/false ci sa podarilo aktualizovat casticu
     */
    @Override
    public boolean update() {
        if (activation) {
            increaseBar();
        }
        
        return true;
    }    
    
    /**
     * Metoda ktora zvysi hodnotu v bar castici o hodnotu <b>value</b>.
     * Metoda nemeni vnutornu grafiku castice => pri zmene a nahlom vykresleni castica neukaze
     * zmeny, treba volat resetGraphics.
     * @param value Hodnota o ktoru zvysujeme casticu
     */
    public void incBy(double value) {        
        this.value+=value;
    }
    
    /**
     * Metoda ktora zresetuje vnutornu grafiku castice volanim metod setGraphics
     * a increaseBar.
     */
    public void resetGraphics() {
        setGraphics();
        increaseBar();
    }
        
    /**
     * Metoda ktora graficky ukaze ako sa zvysila bar castica podla novych hodnot.
     */
    private void increaseBar() {
        Graphics g = toDraw.getGraphics();        
        g.setColor(color);
        g.fillRect(0, 0, value.intValue(), barHeight);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">            
    /**
     * Metoda ktora nastavi vnutornu grafiku tejto bar castice. Vykreslujeme 
     * podla zakladnych udajov (metoda resetuje graficky kontext na default)
     */
    private void setGraphics() {
        Graphics g = toDraw.getGraphics();
        g.setColor(Colors.getColor(Colors.fullBlack));
        g.fillRect(0, 0, barWidth, barHeight);
    }
        
    /**
     * Metoda ktora nastavi hodnotu bar castice na <b>value</b>. Ked je value 
     * mensia ako zadana tak resetneme casticu a vykreslime donej nove udaje.
     * @param value 
     */
    public void setValue(double value) {
        if (value < this.value) {
            setGraphics();
        }
        this.value = value;
        increaseBar();
    }
    
    /**
     * Metoda ktora deaktivuje casticu a resetne obrazok castice na default
     */
    @Override
    public void setDeactivated() {
        super.setDeactivated();
        setGraphics();
    }
    
    /**
     * Metoda ktora aktivuje casticu a nastavi farbu pozadia castice.
     * @param color 
     */
    public void setActivated(Color color) {
        super.setActivated();
        this.color = color;
    }
    // </editor-fold>
    
}
