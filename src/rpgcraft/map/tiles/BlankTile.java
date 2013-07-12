/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import java.awt.Image;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Player;
import rpgcraft.resource.TileResource;

/**
 * Trieda ktora dedi od dlazdice, cim podeduje vsetky metody z Tile.
 * Instancia prazdnej dlazdice (alebo tiez SKY dlazdica) sa nachadza vsade tam kde sa nenachadza
 * ziadna dlazdica. Dlazdica je nerozbitna a ked cez nu prejde entita tak sa prepada
 * az na spodok.
 */
public class BlankTile extends Tile {

    /**
     * Konstruktor pre prazdnu dlazdicu. Inicializujeme volanim Tile konstruktoru,
     * ktory vsetko vybavi.    
     * @param id Id dlazdice
     * @param res TileResource zodpovedajuci dlazdici.
     */
    public BlankTile(Integer id, TileResource res) {
        super(id, res);        
    }

    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Metoda znizi level entity ktora narazila do dlazdice (pri hracovi znici aj level mapy
     * <=> kamera ktorou sledujeme vrstvu). Po znizeni upravi vysku (volanim moveInto
     * na novej dlazdici), cim sa moze znova entita prepadnut (entita pada)
     * </p>
     * @param e Entita ktora narazila do dlazdice.
     */
    @Override
    public void moveInto(Entity e) {
        e.decLevel();
        if (e instanceof Player) {
            e.getMap().setLevel(e.getLevel());
        }
        e.updateHeight();
    }

    /**
     * Metoda ktora vrati obrazok tejto prazdnej dlazdice a kedze je dlazdica prazdna
     * tak vrati null co znamena cierna dlazdica.
     * @param meta {@inheritDoc }
     * @return Obrazok dlazdice (null)
     */
    @Override
    public Image getImage(int meta) {
        return null;
    }
    
    /**
     * Metoda ktora vrati vrchny obrazok dlazdice. To je obrazok ktory vidime ked sme
     * o jeden level menej ako dlazdica. Takisto ako pri getImage vratime null.
     * @return Obrazok vyssej dlazdice videnej zo spoda
     */
    @Override
    public Image getUpperImage() {
        return null;
    }
    
    
    
    
    
}
