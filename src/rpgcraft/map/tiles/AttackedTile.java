/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.tiles;

import rpgcraft.map.chunks.Chunk;

/**
 * Trieda AttackedTile ktorej instancia zdruzuje info o dlazdici na ktorej hrac prave utoci.
 * Instancia zdruzuje x,y,z pozicie dlazdice na ktoru utocime s id-ckom dlazdice,zivotom
 * a aktualnym chunkom v ktorom je dlazdica. Na utocenie volame metodu hit s parametrom 
 * kolko chceme ubrat dlazdici (pri zivote <= 0 sme znicili dlazdicu a nastavujeme dlazdicu na BlankTIle) 
 */
public class AttackedTile {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * X-ova, Y-ova a Z-ova pozicia dlazdice
     */
    int x,y,level;
    /**
     * Id dlazdice
     */
    int tileId;
    /**
     * Zivot dlazdice po ktorom sa povazuje dlazdica za znicenu
     */
    double health;
    /**
     * Chunk v ktorom je dlazdica
     */
    Chunk chunk;
    // </editor-fold>

    /**
     * Metoda ktora vytvori instanciu AttackedTile ktora inicializuje tuto dlazdicu
     * podla parametrov. Zivot a id dlazdice ziskame z chunku ktory je taktiez v parametri.
     * @param chunk Chunk v ktorom je dlazdica
     * @param level Z/Level pozicia dlazdice
     * @param x X-ova pozicia dlazdice
     * @param y Y-ova pozicia dlazdice
     */
    public AttackedTile(Chunk chunk, int level, int x, int y) {        
        this.tileId = chunk.getTile(level, x, y);
        this.health = chunk.getMetaData(level, x, y);
        this.chunk = chunk;
        this.x = x;
        this.y = y;
        this.level = level;
    }

    /**
     * Metoda ktora zautoci na tuto dlazdicu. Sila utoku je predana parametrom <b>damage</b>.
     * Pri utoku nastavujeme meta data tejto dlazdice v chunku na zivot dlazdice.
     * Ked znicime dlazdicu (health <= 0) tak zavolame metodu destroyTile na chunk
     * ktora znici dlazdicu (nastavi dlazdicu na BlankTile). Nakonci vratim zivot dlazdice
     * @param damage Sila utoku vykonanej na dlazdicu
     * @return Kolko zivota zostava dlazdice
     */
    public double hit(double damage) {
        this.health -= damage; 
        //System.out.println(health);
        if (health <= 0) {
            chunk.destroyTile(level, x, y);
        } else {
            chunk.setMeta(level, x, y, (int)health);
        }
        return this.health;
    }

    /**
     * Metoda ktora vrati aku ma odolnost tato dlazdica
     * @return Odolnost dlazdice
     */
    public int getDurability() {
        return Tile.tiles.get(tileId).getTileStrength();
    }   
    
    /**
     * Metoda ktora vrati aky druh predmet musime na dlazdicu pouzit
     * @return Hodnota z WeaponType 
     */
    public int getItemType() {
        return Tile.tiles.get(tileId).getItemType();
    }
    
    /**
     * Metoda ktora vrati originalnu dlazdicu z tejto AttackedTile.
     * @return Dlazdica z tejto instancie.
     */
    public Tile getOriginTile() {
        return Tile.tiles.get(tileId);        
    }
    
    /**
     * Metoda ktora vrati id dlazdice na ktoru utocime
     * @return Id dlazdice
     */
    public int getId() {
        return tileId;
    }
    
    /**
     * Metoda ktora vrati zostavajuci zivot dlazdice na ktoru utocime
     * @return Zivot dlazdice
     */
    public double getHealth() {
        return health;
    }
    
    /**
     * Metoda ktora vrati ci je dlazdica znicena.
     * @return True/false ci je dlazdica znicena
     */
    public boolean isDestroyed() {
        return health <=0 ? true : false;
    }
    
    /**
     * Metoda ktora vrati x-ovu poziciu dlazdice v chunku
     * @return X-ova pozicia dlazdice v chunku
     */
    public int getX() {
        return x;
    }
    
    /**
     * Metoda ktora vrati y-ovu poziciu dlazdice v chunku
     * @return Y-ova pozicia dlazdice v chunku
     */
    public int getY() {
        return y;
    }        
    
    /**
     * Metoda ktora vrati z/level-ovu poziciu dlazdice v chunku
     * @return Z/Level-ova pozicia dlazdice v chunku
     */
    public int getLevel() {
        return level;
    }            
}
