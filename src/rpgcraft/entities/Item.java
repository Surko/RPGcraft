/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities;

import java.awt.Image;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.map.SaveMap;
import rpgcraft.plugins.ItemGeneratorPlugin;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.StatResource.Stat;
import rpgcraft.resource.StringResource;

/**
 * Abstraktna trieda Item dediaca od Statickej entity ktora zdruzuje vsetky metody 
 * , premenne dolezite pre spravne fungovanie predmetov. Kedze dedi od StaticEntity a ta
 * od Entity tak predmet zaradujeme takisto medzi entity => predmety sa daju polozit na mapu a
 * takisto mozeme nanich posobit ({@link Entity#activate(rpgcraft.entities.Entity)}).
 * Narozdiel od entit je mozne instancie Itemu priradit do inventara/osobneho batohu 
 * entity. Trieda obsahuje metody na vytvorenie novych predmetov ako aj generovanie
 * predmetov z generatoru itemov, taktiez ma metody na ziskanie listu s moznymi operaciami
 * nad predmetom. Od tejto triedy sa rozvetvuju ostatne specificke triedy s predmetmi ako je
 * Brnenie, Zbrane, atd... (dedia od Item). Tieto specificke triedy sa daju konstruktorom vytvorit
 * a priradit do inventara ci na mapu.
 * @see StaticEntity
 */
public abstract class Item extends StaticEntity {                  
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy/enumy ">
    public enum Command {
        INFO(StringResource.getResource("_info")),
        USE(StringResource.getResource("_use")),
        EQUIP(StringResource.getResource("_equip")),
        UNEQUIP(StringResource.getResource("_unequip")),
        ACTIVE(StringResource.getResource("_active")),
        UNACTIVE(StringResource.getResource("_unactive")),
        DESTROY(StringResource.getResource("_destroy")),
        CLOSE(StringResource.getResource("_close")),
        PLACE(StringResource.getResource("_place")),
        DROP(StringResource.getResource("_drop"));
                
        private String sCommand;
        
        private Command(String sCommand) {
            this.sCommand = sCommand;
        }
        
        public String getValue() {
            return sCommand;
        }
    }
        
    /**
     * Interface pre rozne type predmetov. Implementuju ho enumy ktore sa nachadzaju v triedach
     * dediacich od Item
     */
    public interface TypeofItems {                                
        
        public String getValue();
        
    }
    
    public enum ItemType implements TypeofItems {
        WEAPON(StringResource.getResource("weapon")),
        ARMOR(StringResource.getResource("armor")),
        MISC(StringResource.getResource("misc")),
        TILE(StringResource.getResource("tile")),
        NOTYPE(StringResource.getResource("notype"));                
        
        private TypeofItems subType;
        private String value;
        
        private ItemType(String value) {
            this.value = value;
        }
        
        @Override
        public String getValue() {
            return value;
        }
        
        public void setSubType(TypeofItems subType) {
            this.subType = subType;
        }
        
        public TypeofItems getSubType() {
            return subType;
        }
        
        @Override
        public String toString() {
            return this.value + ":" +subType;
        }
              
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private long seed;
    
    // Sprity pre item
    protected ArrayList<Sprite> itemSprites;
        
    // Pocet itemov v stacku
    protected int count;
    
    // vlastnosti itemu - vacsinou z resource. Mozne zmenit.
    protected ItemType itemType;
    protected boolean equipable;
    protected boolean dropable;
    protected boolean usable;
    protected boolean activable;
    protected boolean equipped;
    protected boolean placeable;        
    
    protected boolean levelable = false;    

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Prazdny konstruktor ktory je dolezity pre vytvaranie instancie pri externalizacii.
     */
    public Item() {
    }   
    
    /**
     * Konstruktor na vytvorenie predmetu. Konstruktor nie je mozne zavolat kedze trieda je abstraktna.     
     * No vyuzivame ho pri dalsich potomkoch ktori ho volaju pomocou super konstruktora a
     * kedze chceme aby vytvaranie predmetu prebiehalo vzdy rovnako tak je to jedina moznost.
     * @param name Meno noveho predmetu
     * @param res EntityResource z ktoreho vytvarame predmet
     */
    public Item(String name, EntityResource res) {
        this.name = name == null ? res.getName() : name;
        this.res = res;
        this.activable = true;
        this.id = res.getId();
        this.count = 1;
        this.spriteType = Sprite.Type.ITEM;
        this.itemSprites = res.getEntitySprites().get(spriteType);                        
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora inicializuje predmet po vytvoreni v konstruktore. Volame
     * super metodu initialize ktora inicializuje vsetk vlastnosti. V tejto metode inicializujeme
     * iba take co su vytvorene v Item triede.     
     */
    @Override
    public void initialize() {                
        super.initialize();        
        this.dropable = true;
        this.health = doubleStats.get(Stat.HEALTHMAX);
    }
    
    /**
     * Metoda ktora reinicializuje predmet po nacitani z disku. Reinicializacia 
     * prebieha tak ze volame super metodu reinitialize ktora zabezpeci ostatne vlastnosti
     * a v tejto metode reinicializujeme iba obrazky pre predmet.
     */
    @Override
    public void reinitialize() {
        super.reinitialize();
        if (res != null) {
            this.itemSprites = res.getEntitySprites().get(spriteType);
        }
    }
    
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    
    /**
     * Metoda ktora vygeneruje predmet. Nato aby sme vedeli co za generator pouzit
     * mame parameter <b>genName</b>. Parameter <b>name</b> sluzi ako nove meno pre vytvoreny predmet a
     * parameter <b>type</b> je typ novo vytvoreneho predmetu.
     * Na spravne fungovanie generatoru je nutne ziskat generator, potom zavolat
     * initialize aby sme prichystali generator na vytvorenie predmetu podla parametrov
     * a metodu generateAll ktora predmet vygeneruje.
     * @param genName Meno pre ziskanie seedu
     * @param name Meno predmetu
     * @param type Typ predmetu
     */
    public static Item randomGen(String genName, String name, ItemType type) {
        ItemGeneratorPlugin gen = ItemGeneratorPlugin.getGenerator(genName);
        gen.initialize(name, type);
        return gen.generateAll();
    }

    /**
     * Metoda ktora vytvori predmet podla metody createItem s parametrami name a res. Po 
     * vygenerovani nastavime predmetu aktualnu mapu v ktorej sa nachadza.
     * @param map Mapa v ktorom budem predmet
     * @param name Meno predmetu
     * @param res Resource z ktoreho vytvarame predmet
     * @return Vytvoreny predmet podla parametrov s nastavenou mapou.
     */
    public static Item createItem(String name, SaveMap map, EntityResource res) {        
        Item newItem = createItem(name, res);
        if (newItem != null) {
            newItem.setMap(map);
        }
        return newItem;
    }        
        
    /**
     * Metoda ktora vytvori presny predmet s menom zadanym ako parameter <b>name</b>.
     * Predmet sa vytvara z resource zadany parametrom <b>res</b>. Nato aby sme vedeli
     * co za predmet vytvarame vyuzivame resource a jeho metodu getItemType ktora nam vrati
     * typ predmetu ktory vytvarame.
     * @param name Meno predmetu ktory vytvarame
     * @param res EntityResource ktory sluzi ako vzor
     * @return Predmet s menom name a vyhovujuci Resource.
     */
    public static Item createItem(String name, EntityResource res) {
        if (res == null) {
            return null;
        }
        ArrayList<ItemType> types = res.getItemType();
        Item item = null;
        if (types.size() > 1) {            
            item = new CombinatedItem(name, res);
        }
        if (types.size() == 1) {
            switch (types.get(0)) {
                case ARMOR : {
                    item = new Armor(name, res);                     
                } break;
                case WEAPON : {
                    item = new Weapon(name, res);                                        
                } break;
                case MISC : {
                    item = new Misc(name, res);
                } break;
                case TILE : {
                    item = new TileItem(name, res);
                } break;
                default : return null;
            }           
        }
        
        if (item != null) {
            item.initialize();
        }
        return item;
    }  
    
    /**
     * Metoda ktora prida do jedneho recordu/zaznamu data zadane ako parameter <b>data</b>.
     * Pouzitie je priame z metody getItemInfo kde vyberame co predmet dokaze
     * a touto metodou pridavame pouzitie do listu resultInf.     
     * @param data Data ktore pridavame do listu
     * @param resultInf Vysledny list po pridany recordu/zaznamu.
     */
    private static void _addItemInfo(Object data, ArrayList<ArrayList<Object>> resultInf) {
        ArrayList<Object> record = new ArrayList<>(); 
        // Normalne data
        record.add(data);
        // Meno id
        record.add(data);
        resultInf.add(record);  
    }
    
    public static ArrayList<ArrayList<Object>> getItemInfo(Item item) {
        ArrayList<ArrayList<Object>> resultInf = new ArrayList<>();        
               
        // Info o predmetoch
        _addItemInfo(StringResource.getResource("_info"), resultInf);    
        
        if (item.isUsable()) {
        _addItemInfo(StringResource.getResource("_use"), resultInf);            
        }        
        if (item.isEquipable()) {
            if (item.isEquipped()) {
                _addItemInfo(StringResource.getResource("_unequip"), resultInf);            
            } else {
                _addItemInfo(StringResource.getResource("_equip"), resultInf);    
            }            
        }
        if (item.isActivable()) {
            if (item.isActive()) {
                _addItemInfo(StringResource.getResource("_unactive"), resultInf);            
            } else {
                _addItemInfo(StringResource.getResource("_active"), resultInf);    
            }            
        }
        if (item.isDropable()) {
        _addItemInfo(StringResource.getResource("_drop"), resultInf);            
        } 
        if (item.isPlaceable()) {
        _addItemInfo(StringResource.getResource("_place"), resultInf);            
        }
        _addItemInfo(StringResource.getResource("_destroy"), resultInf);          
        _addItemInfo(StringResource.getResource("_close"), resultInf);  
           
        return resultInf;          
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora by mala vyzliect predmetu iny predmet. Zatial nevyuzite.
     * @param e Predmet ktory vyzliekame
     */
    @Override
    public void unequip(Item e) {        
    }

    /**
     * Metoda ktora by mala obliect predmetu iny predmet. Zatial nevyuzite.
     * @param e Predmet ktory obliekame
     */
    @Override
    public void equip(Item e) {        
    }

    /**
     * Metoda ktora pouziva na predmetu iny predmet. Zatial nevyuzite.
     * @param item Predmet ktory pouzivame
     */
    @Override
    public void use(Item item) {        
    }

    /**
     * Metoda ktora vyhodi z predmetu iny predmet. Zatial nevyuzite.
     * @param item Predmet ktory vyhadzujeme
     */
    @Override
    public void drop(Item item) {        
    }
    
    /**
     * Metoda ktora zvysi pocet predmetov.
     */
    public void incCount() {
        this.count++;
    }

    /**
     * Metoda ktora znizi pocet predmetov.
     */
    public void decCount() {
        this.count--;
    }
    
    /**
     * Metoda ktora znizi pocet predmetov o parameter <b>value</b>
     * @param value O kolko znizujeme pocet
     */
    public void decCount(int value) {
        this.count -= value;
    }      
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati obrazok pre aktualny SpriteType. Obrazok moze byt animovany
     * co zarucuje telo metody. Animacie su mozne nastavit v xml suboru. Kazdy obrazok
     * ma nastaveny svoj maxTime ako dlho je obrazok v rovnakom stave.
     * Po dosiahnuti maxTime presuvame obrazok na dalsi az kym neprejdeme celu animaciu.
     * @return Aktualny obrazok predmetu
     */
    @Override
    public Image getTypeImage() {
        if (itemSprites == null) {
            return null;
        } else {
            if (System.currentTimeMillis() - waypointTime >= maxTime) {
                sprNum++;
                if (itemSprites.size() <= sprNum) {
                    sprNum = 0;
                }
                currentSprite = itemSprites.get(sprNum);
                waypointTime = System.currentTimeMillis();
                maxTime = currentSprite.getDuration();
            }
            //ImageUtils.writeImageToDisc(name, (BufferedImage)itemSprites.get(sprNum).getSprite());
            return currentSprite.getSprite();
        }
    }

    /**
     * Metoda ktora vrati pocet predmetov jedneho predmetu.
     * @return Pocet predmetov jedneho druhu
     */
    @Override
    public int getCount() {
        return count;
    }

    /**
     * Metoda ktora vrati typ entity v ramci funkcnosti. Typ tohoto predmetu su vacsinou nazvy pre potomkov 
     * co dedia od tejto triedy (WEAPON, ARMOR,...)
     * @return Typ entity.     
     * @see ItemType
     */    
    public ItemType getItemType() {      
        if (itemType == null) {
            return ItemType.NOTYPE;
        }
        return itemType;
    }
    
    /**
     * Metoda ktora vrati seed predmetu. Seed je long cislo ktore zarucuje
     * ze ked vytvorime predmet s rovnakym seedom tak predmet bude rovnaky.
     * @return Seed pre predmet
     */
    public long getSeed() {
        return seed;
    }
    
    /**
     * Metoda ktora vrati ci je predmet zniceny.
     * @return True/false ci je predmet zniceny
     */
    @Override
    public boolean isDestroyed() {
        return health == 0;
    }

    /**
     * Metoda ktora prida ci je predmet pouzitelny.
     * @return True/false ci je predmet pouzitelny. 
     */
    public boolean isUsable() {
        return usable;
    }

    /**
     * Metoda ktora prida ci je predmet mozne obliect.
     * @return True/false ci je predmet mozne obliect. 
     */
    public boolean isEquipable() {
        return equipable;
    }

    /**
     * Metoda ktora prida ci je predmet vyhoditelny.
     * @return True/false ci je predmet vyhoditelny. 
     */
    public boolean isDropable() {
        return dropable;
    }

    /**
     * Metoda ktora prida ci je predmet mozne aktivovat.
     * @return True/false ci je predmet mozne aktivovat. 
     */
    public boolean isActivable() {
        return activable;
    }

    /**
     * Metoda ktora prida ci je predmet mozne obliect.
     * @return True/false ci je predmet mozne obliect. 
     */
    public boolean isEquipped() {
        return equipped;
    }
    
    /**
     * Metoda ktora prida ci je predmet mozne polozit nazem
     * @return True/false ci je predmet mozne polozit nazem. 
     */
    public boolean isPlaceable() {
        return placeable;
    }   
    
    /**
     * Metoda ktora vrati text s info o predmete
     * @return Text s info o predmete.
     */
    @Override
    public String toString() {
        return count + " " + name + " " + (equipped == true ? StringResource.getResource("_equipped") : "") +
                (active == true ? StringResource.getResource("_activated") : "");
    }
    
    // </editor-fold>
            
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi predmet na obleceny.
     */
    public void equip() {
        this.equipped = true;
    }

    /**
     * Metoda ktora nastavi predmet na vyzleceny
     */
    public void unequip() {
        this.equipped = false;
    }
    
    /**
     * Metoda ktora prenastavi typ tohoto predmetu v ramci funkcnosti na typ zadany parametrom
     * <b>itemType</b>
     * @param itemType Typ predmetu
     * @see ItemType
     */
    public void setItemType(ItemType itemType) {
        this.itemType = itemType;
    }
    
    /**
     * Metoda ktora nastavi predmet ci je obleceny podla parametru.
     * @param equipped True/false ci je predmet obleceny
     */
    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    /**
     * Metoda ktora nastavi predmet na obliecti schopny :D.
     */
    public void setEquipable() {
        this.equipable = true;
    }

    /**
     * Metoda ktora nastavi predmet na vyhoditelny.
     */
    public void setDropable() {
        this.dropable = true;
    }

    /**
     * Metoda ktora nastavi predmet na pouzitelny.
     */
    public void setUsable() {
        this.usable = true;
    }

    /**
     * Metoda ktora nastavi ci je predmet schopny zvysovat svoju uroven.
     * @param levelable True/false podla schopnosti zvysovania urovne.
     */
    public void setLevelable(boolean levelable) {
        this.levelable = levelable;
    }

    /**
     * Metoda ktora nastavi meno predmetu podla parametru <b>name</b>.
     * @param name Meno noveho predmetu.
     */
    @Override
    public void setName(String name) {
        this.name = name;
    }   

    /**
     * Metoda ktora nastavi vytrvalost predmetu (alebo tiez aktualny zivot predmetu)
     * @param durability Aktualny zivot/vytrvalost predmetu
     */
    public void setDurability(int durability) {
        this.health = durability;
    }

    /**
     * Metoda ktora nastavi seed pre predmet.
     * @param seed Seed predmetu
     */
    public void setSeed(long seed) {
        this.seed = seed;
    }        

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    /**
     * Metoda ktora zapise predmet do suboru/vystupneho streamu ktory je zadany 
     * ako parameter <b>out</b>. Vyuzivame rodicovskej metody writeExternal ktora zapise
     * do suboru vsetky vlastnosti spolocne pre entity. V tejto metode zapiseme do suboru
     * iba take vlastnosti ktore su vytvorene v predmete a nie v inych entitach.
     * Nazvy napovedaju za vsetko.
     * @param out Vystupny stream/subor.
     * @throws IOException VYhodena pri neexistenci suboru
     */
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        super.writeExternal(out);
        out.writeObject(itemType);        
        out.writeBoolean(active);
        out.writeBoolean(activable);
        out.writeBoolean(equipped);
        out.writeBoolean(equipable);
        out.writeBoolean(placeable);
        out.writeBoolean(dropable);
        out.writeBoolean(usable);
        out.writeLong(seed);
        out.writeInt(count);
    }

    /**
     * Metoda ktora nacita predmet zo suboru/vstupneho streamu ktory je zadany 
     * ako parameter <b>in</b>. Vyuzivame rodicovskej metody readExternal ktora nacita zo
     * suboru vsetky vlastnosti spolocne pre entity. V tejto metode nacitame zo suboru
     * iba take vlastnosti ktore su vytvorene v predmete a nie v inych entitach.
     * Nazvy napovedaju za vsetko.
     * @param in Vstupny stream/subor.
     * @throws IOException VYhodena pri neexistenci suboru
     */
    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        super.readExternal(in);
        this.itemType = (ItemType) in.readObject();        
        active = in.readBoolean();
        activable = in.readBoolean();
        equipped = in.readBoolean();
        equipable = in.readBoolean();
        placeable = in.readBoolean();
        dropable = in.readBoolean();
        usable = in.readBoolean();
        this.seed = in.readLong();
        this.count = in.readInt();
        reinitialize();
    }               

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Hash Metody ">
    /**
     * Metoda ktora porovnava dva predmety ci su rovnake. Dva predmety su rovnake 
     * ked maju rovnake meno.
     * @param obj Objekt s ktorym porovnavame.
     * @return True/false ci su objekty rovnake.
     */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Item other = (Item) obj;

        if (!this.name.equals(other.name)) {
            return false;
        }
               
        return true;
    }

    /**
     * Metoda ktora vrati vygenerovany hash kod podla parametrov
     * @return Hashkod pre predmet.
     */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 23 * hash + (int) (this.seed ^ (this.seed >>> 32));
        hash = 23 * hash + (this.itemType != null ? this.itemType.hashCode() : 0);
        hash = 23 * hash + (this.equipable ? 1 : 0);
        hash = 23 * hash + (this.dropable ? 1 : 0);
        hash = 23 * hash + (this.usable ? 1 : 0);
        hash = 23 * hash + (this.activable ? 1 : 0);
        hash = 23 * hash + (this.placeable ? 1 : 0);
        hash = 23 * hash + (this.levelable ? 1 : 0);
        return hash;
    }
    
    // </editor-fold>
      
}
