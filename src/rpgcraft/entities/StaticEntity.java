/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import rpgcraft.graphics.spriteoperation.Sprite.Type;

/**
 * Abstraktna trieda StaticEntity dedi vsetky vlastnosti a metody, ktore boli definovane
 * v triede Entity. Kedze pochadza od Entity tak vsetko dediace od Statickej entity
 * bude mozne pridat na mapu a budeme moct s tym interagovat do urovne aku dovoluje
 * trieda Entity. 
 * <p>
 * StaticEntity ako nazov napoveda, vytvara kontajner pre staticke vacsinou nepohyblive entity.
 * Tymto padom je normalne ze kazdy predmet je potomok od tejto statickej entity.
 * Kedze to bude kontajner pre nepohyblive entity a vacsinou bez inteligencie (predmety)
 * tak metody, ktore maju aktualizovat koordinaty, ci interagovat s inymi entitami
 * vracaju default hodnoty.
 * </p>
 * @see Entity
 */
public abstract class StaticEntity extends Entity {    

    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor pre vytvaranie instancie StaticEntity kvoli externalizacii pri nacitani
     * suborov z disku
     */
    public StaticEntity() {
        
    }
    
    // </editor-fold >
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public abstract void unequip(Item e);

    /**
     * {@inheritDoc }
     * @param e {@inheritDoc }
     */
    @Override
    public abstract void equip(Item e);
    
    /**
     * {@inheritDoc }
     * @param item {@inheritDoc }
     */
    @Override
    public abstract void use(Item item);
    
    /**
     * {@inheritDoc }
     * @param item  {@inheritDoc }
     */
    @Override
    public abstract void drop(Item item);
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora porani entitu. Tato metoda vrati 0 kedze predmety neporanujeme.
     * Pravdaze je hu mozne pretazit aj v predmete pri implementacii trvanlivosti/durability
     * predmetu
     * @param damage Sila akou utocime na entitu
     * @param type SpriteType utoku na entitu
     * @return Hodnotu kolko sme ublizili entite (v tomto pripade 0)
     */
    @Override
    public double hit(double damage, Type type) {
        return 0d;
    }

    /**
     * Metoda ktora odsunie entitu zadanu parametrom <b>e</b>. Metoda je prazdna
     * kedze predmety neposuvaju entitami nikam. Keby vytvarame nejakeho potomka
     * od StaticEntity ktory by nebol predmet tak je mozne pretazit metodu
     * podla nasich zelani.
     * @param e Entita ktoru posuvame
     */
    @Override
    public void pushWith(Entity e) {
        
    }

    /**
     * Metoda ktora ma posobit na entity v rozsahu ktora je prvymi 4 parametrami.
     * Modifikator je dodatocny parameter ktory moze byt pouzity pri skalovani utoku
     * @param x0 {@inheritDoc }
     * @param y0 {@inheritDoc }
     * @param x1 {@inheritDoc }
     * @param y1 {@inheritDoc }
     * @param modifier {@inheritDoc }
     * @return {@inheritDoc }
     */
    @Override
    public double interactWithEntities(int x0, int y0, int x1, int y1, double modifier) {
        return 0;
    }

    /**
     * Kedze sa jedna o staticke entity tak ich mozme dat aj na miesta kde nemozu dojst
     * normalne entity => metoda je nevyuzitelna. Pri neskorsom vyuzity mozne 
     * pretazovat metodu.
     * @param tile Dlazdica ktora by sa pridala do listu.
     */
    @Override
    public void setImpassableTile(int tile) {
        
    }

    
    @Override
    public boolean updateCoordinates() {
        knockMove();
        return true;
    }
    
    /**
     * Metoda ktora posunie entitu do nejakej strany ktora je urcena z parametru <b>type</b>.
     * Posunutie je urcene premennou knockback co v konecnom dosledku znamena posunutie
     * o 2 * knockback pixelov do prislusnej strany.
     * @param type Strana podla ktorej urcujeme posunutie.
     */
    @Override
    public void knockback(Type type) {
        if (type == Type.RIGHT) {
            xKnock = knockback;
        }
        if (type == Type.LEFT) {
            xKnock = -knockback;
        }
        if (type == Type.DOWN) {
            yKnock = knockback;
        }
        if (type == Type.UP) {
            yKnock = -knockback;
        }
    }      

    /**
     * Metoda ktora aktualizuje entitu. Ked bola entita znicena tak vratime false
     * co znamena ze vymazavame entitu z mapy. Ked nie je znicena tak aktualizujeme koordinaty
     * a vratime true.     
     * @return True/false ci sa podaril update/aktualizacia
     */
    @Override
    public boolean update() {
        if (isDestroyed()) {
            return false;          
        }
        
        updateCoordinates();
        
        return true;
    }
         
    // </editor-fold>
    
}
