/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.particles;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;

/**
 * Trieda ktora dedi od Particle sa automaticky stava casticou ktora sa moze nachadzat v
 * liste castic na vykreslenie. Instancia triedy ma podedene vsetky metody z rodica
 * + ma premenne ktore vystihuju tuto textovu casticu. Najzakladnejsie su text na zobrazenie
 * a font ktorym vypisujeme.
 */
public class TextParticle extends Particle{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Zadana dlzka zivota na 2 sekundy
     */
    private static final int LIFE = 2;
    /**
     * Text na zobrazenie
     */
    private String msg;
    /**
     * Font s ktorym vykreslujeme textovu casticu
     */
    private Font font = new Font("Tahoma", Font.PLAIN, 15);
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor textovej castice ktory vytvori instanciu TextParticle. Inicializujeme
     * x-ovu a y-ovu poziciu vzhladom od hraca, spravuna zobrazenie, sirku a vysku castice
     * a obrazok na vykreslenie.
     * @param msg Sprava na ukazanie
     * @param x Relativna x-ova pozicia
     * @param y Relativna y-ova pozicia
     */
    public TextParticle(String msg, int x, int y) {
        this.x = x;
        this.y = y;
        this.msg = msg;
        this.height = 24;
        this.width = 24;
        this.activation = true;
        this.toDraw = new BufferedImage(width, height, BufferedImage.TRANSLUCENT);        
        setGraphics();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora nastavi vnutornu grafiku tejto textovej castice. Vykreslujeme 
     * podla zakladnych udajov s nastavenim fontu a farby s vypisom spravy.
     */
    private void setGraphics() {
        Graphics g = toDraw.getGraphics();
        g.setFont(font);
        g.setColor(Color.RED);
        g.drawString(msg, 3, 16);
    }
    
        
    /**
     * Metoda ktora simuluje zivot castice (premenna span) na obrazovke.  
     * Po prebehnuti zivota vrati ci sa podaril update.
     * @return True/False podla toho ci sa podaril update.
     */
    @Override
    public boolean update() {
        if (!activation) return false;
            
        span++;
        if (span > LIFE) {
            return false;
        }
        
        return true;
    }
    // </editor-fold>
    
}
