/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics;

import java.awt.Color;

/**
 * Trieda ktorej instancia ma na starosti vratit ake farby svetla sa maju pouzivat pre jednotlive
 * casy v ramci dna. Vacsinou vyuzivane v Render objektoch ktore pracuju so svetlom.
 */
public class DayLighting {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">    
    /**
     * Gradienty pre rozne useky dna
     */
    private static final float MORNING = 0.5f,LUNCH = 1f,EVENING = 0.5f, NIGHT = 0.0f;
    
    /**
     * Pole Farieb pouzite pre svetlo
     */
    private Color[] colors;
    /**
     * Pole pokial siaha gradient pre jednotlive farby. Percentualne rozdelenie
     */
    private float[] fractions;
    /**
     * Modifikator ako daleko dosiahne svetlo
     */
    private int radBuff = 0;
    // </editor-fold>
            
    /**
     * Konstruktor ktory vytvori instanciu triedy pricom nastavi aktualny cas podla
     * parametru.
     * @param gameTime Aktualny cas (0-24)
     */
    public DayLighting(int gameTime) {
        init(gameTime);
    }
    
    /**
     * Metoda ktora zinicializuje farby a fractions ktore urcuju pokial jednotlive farby maju dosah
     * (napr. v GradientBush). Parameter radBuff urcuje o kolko pixelov vidi entita dalej ci menej.
     * @param time Aktualny cas podla ktoreho inicializujeme farby.
     */
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

    /**
     * Metoda ktora vrati modifikator videnia
     * @return Modifikator videnia (v pixeloch)
     */
    public int getRadiusBuff() {
        return radBuff;
    }
    
    /**
     * Metoda ktora vrati farby pre aktualny cas v hre
     * @return Farby pre aktualny cas
     */
    public Color[] getColors() {
        return colors;
    }
    
    /**
     * Metoda ktora vrati fractions (ako daleko farby maju dosah) pre aktualny
     * cas v hre
     * @return Fractions pre aktualny cas
     */
    public float[] getFractions() {
        return fractions;
    }
}
