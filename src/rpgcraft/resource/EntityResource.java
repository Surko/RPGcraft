/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.effects.Effect;
import rpgcraft.effects.Effect.EffectEvent;
import rpgcraft.entities.Armor.ArmorType;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Entity.EntityType;
import rpgcraft.entities.Item;
import rpgcraft.entities.Item.ItemType;
import rpgcraft.entities.Misc.MiscType;
import rpgcraft.entities.MovingEntity;
import rpgcraft.entities.TileItem.TileType;
import rpgcraft.entities.Weapon.WeaponType;
import rpgcraft.entities.types.ItemLevelType;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.SpriteSheet;
import rpgcraft.manager.PathManager;
import rpgcraft.panels.listeners.Action;
import rpgcraft.xml.EntityXML;

/**
 * EntityResource ktore dedi od AbstraktnehoResource je trieda ktora umoznuje
 * vytvorit entitove resources z xml suborov, ktore sa daju pouzit ako vzory
 * pre nase vytvorene entity v hre. Na vytvorenie nam pomaha metoda parse,
 * ktorej predavame jeden vrchol z xmlka na rozparsovanie. Vytvaranie noveho resource
 * prevadzame volanim metody newBundledResource. Navrat nejakeho resource pomocou metody
 * getResource. 
 * @see AbstractResource
 */
public class EntityResource extends AbstractResource<EntityResource> {    
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(MovingEntity.class.getName());
    private static final String DELIM = "[,]", PDELIM = "[:]";
    
    private static ArrayList<String> spawnableEntities;    
    
    private static Sprite _sprite = null;
    private static ArrayList<Sprite> _sprites = null;
    private static Sprite.Type _type = null;    
    private static int iSprite = 0;
    
    private static HashMap<String, EntityResource> entityResources = new HashMap<>();
    
    private int _globalW = 0, _globalH = 0;  
            
    private boolean despawnable, luaAi;
    private String name;
    private String[] inventory;
    private String id;
    private String info;
    private EntityType entType;
    private SpriteSheet sheet;
    private HashMap<Sprite.Type, ArrayList<Sprite>> movingEntitySprites;
    private ArrayList<ConversationGroupResource> groupResources;
    private ArrayList<Effect> effects;
    private ArrayList<Action> activationActions;
    private ArrayList<Item.ItemType> itemTypes;
    private int itemLevelType;
        
    private Item activeItem;    
    private String ai;
    
    protected double healthMin, healthMax, healthRegen, healthRegPer, healthPer;
    protected double staminaBonus, healthBonus;
    protected double staminaMin,staminaMax, staminaRegen, staminaRegPer, staminaPer;
    protected double damageMin,damageMax, damagePer, dmgBonus;    
    protected int strengthMin,strengthMax;
    protected int agilityMin,agilityMax;
    protected int speedMin,speedMax;
    protected int enduranceMin,enduranceMax;
    protected double endurancePer, speedPer, strengthPer, agilityPer;
    
    private int knockback;
    private int aggresivity;
    private int group;
    private double doubleFactor;
    private double interruptionChance;
    private double concentration;
    
    private double attack, defenseA,defenseP;
    private double atkRatingPer, defRatingPer, atkRadiusPer;
    private int attackRadius;
    private int attackRating,defenseRating;
    private int attRatingBonus, defRatingBonus;
    private int attackRater,defenseRater, damageRater, speedRater, rater;
    
    private boolean moveable;
    private boolean attackable;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor (privatny) ktory vytvori instanciu EntityResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme entitu a vlozime hu do listu s entitami.
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private EntityResource(Element elem) {
        despawnable = true;
        movingEntitySprites = new HashMap<>();
        parse(elem);        
        validate();
        entityResources.put(id, this);
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vrati EntityResource podla parametru <b>name</b>. Name
     * je id v xml.
     * @param name Meno EntityResource ktore hladame
     * @return EntityResource s danym menom
     */
    public static EntityResource getResource(String name) {
        return entityResources.get(name);
    }    
        
    /**
     * Metoda ktora vytvori novy objekt typu EntityResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu EntityResource.
     * @param elem Element z ktoreho vytvarame entitu
     * @return EntityResource z daneeho elementu
     */
    public static EntityResource newBundledResource(Element elem) {
        return new EntityResource(elem);                
    }
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda ktora vrati meno entity ziskany z xml
     * @return Meno entity
     */
    public String getName() {
        return name;
    }
    
    /**
     * Metoda ktora vrati Id entity ziskany z xml
     * @return Id entity
     */
    public String getId() {
        return id;
    }
    
    /**
     * Metoda ktora vrati ci je entita mozna pohybu.
     * @return True/false ci je entita mozna pohybu
     */
    public boolean isMoveable() {
        return moveable;
    }
    
    /**
     * Metoda ktora vrati, ci je entitu mozne despawnut z planu.
     * @return 
     */
    public boolean isDespawnable() {
        return despawnable;
    }
    
    /**
     * Metoda ktora vrati Info o entite ziskany z xml
     * @return Info entity
     */
    public String getInfo() {
        return info;
    }
    
    /**
     * Metoda ktora vrati Sheet z ktoreho sme ziskali obrazok o entite.
     * @return Sheet pre entitu
     */
    public SpriteSheet getSheet() {
        return sheet;
    }
    
    /**
     * Metoda ktora vrati grupu entity ziskany z xml
     * @return Grupa entity
     */
    public int getGroup() {
        return group;
    }
    
    /**
     * Metoda ktora vrati meno inteligencie pouzitej pri entite.
     * @return Meno inteligencie pre entitu
     */
    public String getAi() {
        return ai;
    }
    
    /**
     * Metoda ktora vrati ci je inteligencia skript lua.
     * @return True/false ci je inteligencia lua skript
     */
    public boolean isLuaAi() {
        return luaAi;
    }
    
    /**
     * Metoda ktora vrati typ entity ziskany z xml
     * @return Typ entity
     */
    public EntityType getEntityType() {
        return entType;
    }
    
    /**
     * Metoda ktora vrati silovy typ entity ziskany z xml
     * @return Silovy typ entity
     */
    public ArrayList<ItemType> getItemType() {
        return itemTypes;
    }
    
    /**
     * Metoda ktora vrati mozne odsunutie entity ziskany z xml
     * @return Odsuvanie entity
     */
    public int getKnockback() {
        return knockback;
    }
    
    /**
     * Metoda ktora vrati koncentraciu entity ziskany z xml
     * @return Koncentracia entity
     */
    public double getConcentration() {
        return concentration;
    }        
    
    /**
     * Metoda ktora vrati grupove konverzacie entity ziskany z xml
     * @return Grupove konverzacie entity
     */
    public ArrayList<ConversationGroupResource> getGroupConversations() {
        return groupResources;
    }
    
    /**
     * Metoda ktora vrati Sprity entity ziskany z xml
     * @return Sprity entity
     */
    public HashMap<Sprite.Type, ArrayList<Sprite>> getEntitySprites() {
        return movingEntitySprites;
    }
    
    /**
     * Metoda ktora vrati akcie pri aktivovani entity ziskany z xml
     * @return Akcie pri aktivovani entity
     */
    public ArrayList<Action> getActivateActions() {
        return activationActions;
    }
    
    /**
     * Metoda ktora vrati efekty entity ziskany z xml
     * @return Efekty entity
     */
    public ArrayList<Effect> getEntityEffects() {
        return effects;
    }
    
    /**
     * Metoda ktora vrati typ entity.
     * @return Typ entity z ItemLevelType
     */
    public int getItemLevelType() {
        return itemLevelType;
    }
    
    /**
     * Metoda ktora vrati minimalny zivot entity ziskany z xml
     * @return Minimalny zivot entity
     */
    public double getMinHealth() {
        return healthMin;
    }
    
    /**
     * Metoda ktora vrati maximalny zivot entity ziskany z xml
     * @return Maximalny zivot entity
     */
    public double getMaxHealth() {
        return healthMax;
    }
    
    /**
     * Metoda ktora vrati regeneraciu entity ziskany z xml
     * @return Regeneracia entity
     */
    public double getHealthRegen() {
        return healthRegen;
    }
    
    /**
     * Metoda ktora vrati bonus k zivotu entity ziskany z xml
     * @return Bonus k zivotu entity
     */
    public double getHealthBonus() {
        return healthBonus;
    }
    
    /**
     * Metoda ktora vrati minimalnu staminu entity ziskany z xml
     * @return Minimalna stamina entity
     */
    public double getMinStamina() {
        return staminaMin;
    }
    
    /**
     * Metoda ktora vrati maximalna stamina entity ziskany z xml
     * @return Maximalna stamina entity
     */
    public double getMaxStamina() {
        return staminaMax;
    }
    
    /**
     * Metoda ktora vrati stamina bonus u entity ziskany z xml
     * @return Stamina bonus entity
     */
    public double getStaminaBonus() {
        return staminaBonus;
    }
    
    /**
     * Metoda ktora vrati minimalnu silu utoku entity ziskany z xml
     * @return Minimalna sila utoku entity
     */
    public double getMinDamage() {
        return damageMin;
    }
    
    /**
     * Metoda ktora vrati maximalna sila utoku entity ziskany z xml
     * @return Maximalna sila utoku entity
     */
    public double getMaxDamage() {
        return damageMax;
    }
    
    /**
     * Metoda ktora vrati regeneraciu staminy entity ziskany z xml
     * @return Regeneracia staminy entity
     */
    public double getStaminaRegen() {
        return staminaRegen;
    }        
    
    /**
     * Metoda ktora vrati utocny odhadovac (ako moc sa berie do uvahy atkRating) entity ziskany z xml
     * @return Utocny odhadovac entity
     */
    public int getAttackRater() {
        return attackRater;
    }
    
    /**
     * Metoda ktora vrati defenzivny odhadovac (ako moc sa berie do uvahy defRating) entity ziskany z xml
     * @return Defenzivny odhadovac entity
     */
    public int getDefenseRater() {
        return defenseRater;
    }
    
    /**
     * Metoda ktora vrati odhadovac sily utoku(ako moc sa berie do uvahy strength) entity ziskany z xml
     * @return Odhadoc sily utoku entity
     */
    public int getDamageRater() {
        return damageRater;
    }
    
    /**
     * Metoda ktora vrati rychlostny odhadovac (ako moc sa berie do uvahy speed) entity ziskany z xml
     * @return Rychlostny odhadovac entity
     */
    public int getSpeedRater() {
        return speedRater;
    }
    
    public int getRater() {
        return rater;
    }
    
    /**
     * Metoda ktora vrati utok bonus entity ziskany z xml
     * @return Utok bonus entity
     */
    public int getAttRatingBonus() {
        return attRatingBonus;
    }
    
    /**
     * Metoda ktora vrati defenzivny bonus entity ziskany z xml
     * @return Defenzivny bonus entity
     */
    public int getDefRatingBonus() {
        return defRatingBonus;
    }        
    
    /**
     * Metoda ktora vrati minimalne endurance entity ziskany z xml
     * @return Min Endurance entity
     */
    public int getMinEnd() {
        return enduranceMin;
    }
    
    /**
     * Metoda ktora vrati maximalne endurance entity ziskany z xml
     * @return Max Endurance entity
     */
    public int getMaxEnd() {
        return enduranceMax;
    }
    
    /**
     * Metoda ktora vrati minimalne strength entity ziskany z xml
     * @return Min Strength entity
     */
    public int getMinStr() {
        return strengthMin;
    }
    
    /**
     * Metoda ktora vrati maximalne Strength entity ziskany z xml
     * @return Max Strength entity
     */
    public int getMaxStr() {
        return strengthMax;
    }
    
    /**
     * Metoda ktora vrati minimalne Agility entity ziskany z xml
     * @return Min Agility entity
     */
    public int getMinAgi() {
        return agilityMin;
    }
    
    /**
     * Metoda ktora vrati maximalne Agility entity ziskany z xml
     * @return Max Agility entity
     */
    public int getMaxAgi() {
        return agilityMax;
    }
    
    /**
     * Metoda ktora vrati minimalne Speed entity ziskany z xml
     * @return Min Speed entity
     */
    public int getMinSpd() {
        return speedMin;
    }
    
    /**
     * Metoda ktora vrati maximalne Speed entity ziskany z xml
     * @return Max Speed entity
     */
    public int getMaxSpd() {
        return speedMax;
    }
    
    /**
     * Metoda ktora vrati aktivny predmet v entite.
     * @return Aktivny predmet pre entitu
     */
    public Item getActiveItem() {
        return activeItem;
    }
    
    /**
     * Metoda ktora vrati faktor rychlosti
     * @return Faktor rychlosti
     */
    public double getDoubleFactor() {
        return doubleFactor;
    }
    
    /**
     * Metoda ktora vrati sancu na vyrusenie inej entity
     * @return Sanca na vyrusenie
     */
    public double getInterChance() {
        return interruptionChance;
    }
    
    /**
     * Metoda ktora vrati ako daleko dociahneme pri utoku
     * @return Ako daleko dosiahne utok
     */
    public int getAttackRadius() {
        return attackRadius == 0 ? 16 : attackRadius;
    }
    
    /**
     * Metoda ktora vrati percentualny bonus k sile utoku
     * @return Percentualny bonus k sile utoku
     */
     public Double getDamagePer() {
        return damagePer;
    }

     /**
      * Metoda ktora vrati percentualny bonus k vzdialenosti utoku
      * @return Percentualny bonus k utoku
      */
    public Double getAtkRadiusPer() {
        return atkRadiusPer;
    }

    /**
     * Metoda ktora vrati percentualny bonus k utoku
     * @return Percentualny bonus k utoku
     */
    public Double getAtkRatingPer() {
        return atkRatingPer;
    }

    /**
     * Metoda ktora vrati percentualny bonus k defenzive
     * @return Percentualny bonus k defenzive
     */
    public Double getDefRatingPer() {
        return defRatingPer;
    }

    /**
     * Metoda ktora vrati regeneracny bonus k stamine
     * @return Percentualny regeneracny bonus k stamine
     */
    public Double getStaminaRegenPer() {
        return staminaRegPer;
    }

    /**
     * Metoda ktora vrati percentualny bonus k zivotu
     * @return Percentualny bonus k zivotu
     */
    public Double getHealthPer() {
        return healthPer;
    }

    /**
     * Metoda ktora vrati percentualny bonus k stamine
     * @return Percentualny bonus k stamine
     */
    public Double getStaminaPer() {
        return staminaPer;
    }

    /**
     * Metoda ktora vrati percentualny bonus k rychlosti
     * @return Percentualny bonus k rychlosti
     */
    public Double getSpeedPer() {
        return speedPer;
    }

    /**
     * Metoda ktora vrati percentualny bonus k sile
     * @return Percentualny bonus k sile
     */
    public Double getStrengthPer() {
        return strengthPer;
    }

    /**
     * Metoda ktora vrati percentualny bonus k agility
     * @return Percentualny bonus k agilite.
     */
    public Double getAgilityPer() {
        return agilityPer;
    }

    /**
     * Metoda ktora vrati percentualny bonus k endurance
     * @return Percentualny bonus k endurance
     */
    public Double getEndurancePer() {
        return endurancePer;
    }

    /**
     * Metoda ktora vrati percentualny regeneracny bonus k zivotu
     * @return Percentualny regeneracny bonus k zivotu
     */
    public Double getHealthRegenPer() {
        return healthRegPer;
    }
    
    /**
     * Metoda ktora vrati resource s entitami ktore mozme spawnovat na mape
     * @return List s entitami na spawnovanie
     */
    public static ArrayList<String> getSpawnableEntities() {
        return spawnableEntities;
    }
    
    /**
     * Metoda ktora vrati zakladny inventar entity
     * @return Inventar entity
     */
    public String[] getInventory() {
        return inventory;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * (EntityXML.MOVE -> vytvori list so spritmi a rekurzivne zavola metodu na parse na podelementy, ktore
     * do listu pridaju potrebne sprity. Pri navrate pridame sprity do mapy so spritmi)
     * @param elem Element z xml ktory rozparsovavame do EntityResource
     */
    @Override
    protected void parse(Element elem) {
              
        NodeList nl = elem.getChildNodes();        
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {
                case EntityXML.MOVE : {                       
                    _sprites = new ArrayList<>();
                    parse((Element)eNode);  
                    movingEntitySprites.put(_type, _sprites);
                } break;
                case EntityXML.ANIM : {
                    _sprite = new Sprite(_type);
                    //System.out.println("Type: " +_sprite.getType().toString());
                    //System.out.println("Loading sprite" + ++iSprite );
                    
                    // Globalne nastavene vysky a sirky sprite 
                                                      
                    parse((Element)eNode);                    
                    
                    if (sheet != null && _sprite.getSprite() == null) {
                        _sprite.setImagefromSheet(sheet, _globalW, _globalH);
                    }
                    _sprites.add(_sprite);
                    //System.out.println("Done Loading");
                } break;
                case EntityXML.CANSPAWN : {
                    if (Boolean.parseBoolean(eNode.getTextContent())) {
                        if (spawnableEntities == null) {
                            spawnableEntities = new ArrayList<>();
                        }
                        spawnableEntities.add(id);
                    }
                } break;
                case EntityXML.CANDESPAWN : {
                    this.despawnable = Boolean.parseBoolean(eNode.getTextContent());                    
                } break;
                case EntityXML.MOVEABLE : {
                    this.moveable = Boolean.parseBoolean(eNode.getTextContent());
                } break;
                case EntityXML.ITEMLEVELTYPE : {
                    this.itemLevelType = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.INFO : {
                    info = eNode.getTextContent();
                } break;
                case EntityXML.GROUP : {
                    group = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.INVENTORY : {
                    inventory = eNode.getTextContent().split(DELIM);                    
                } break;
                case EntityXML.TYPE : {
                    try {
                        _type = Sprite.Type.valueOf(eNode.getTextContent());
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, StringResource.getResource("_iparam",
                                            new String[] {EntityXML.TYPE, this.getClass().getName(), id}));
                        new MultiTypeWrn(e, Color.red, StringResource.getResource("_iparam",
                            new String[] {EntityXML.TYPE, this.getClass().getName(), id}), null).renderSpecific("_label_resourcerror");
                    }
                } break;
                case EntityXML.ENTITYTYPE : {
                    try {
                        entType = Entity.EntityType.valueOf(eNode.getTextContent());                    
                    } catch (Exception e) {
                        LOG.log(Level.SEVERE, StringResource.getResource("_iparam",
                                            new String[] {EntityXML.ENTITYTYPE, this.getClass().getName(), id}));
                        new MultiTypeWrn(e, Color.red, StringResource.getResource("_iparam",
                            new String[] {EntityXML.ENTITYTYPE, this.getClass().getName(), id}), null).renderSpecific("_label_resourcerror");
                    }
                } break;
                case EntityXML.TYPES : {
                    if (entType != null) {
                        String[] _types = eNode.getTextContent().split(DELIM);
                        switch (entType) {
                            case ITEM : {
                                itemTypes = new ArrayList<>();
                                for (String type : _types) {
                                    String[] _parsedTypes = type.split(PDELIM);
                                    try {                                        
                                        ItemType itemType = ItemType.valueOf(_parsedTypes[0]);
                                        switch (itemType) {
                                            case ARMOR : itemType.setSubType(ArmorType.valueOf(_parsedTypes[1]));
                                                break;
                                            case MISC : itemType.setSubType(MiscType.valueOf(_parsedTypes[1]));
                                                break;
                                            case TILE : itemType.setSubType(TileType.valueOf(_parsedTypes[1]));
                                                break;
                                            case WEAPON : itemType.setSubType(WeaponType.valueOf(_parsedTypes[1]));
                                                break;
                                        }                                        
                                        itemTypes.add(itemType);
                                    } catch (Exception e) {
                                        LOG.log(Level.SEVERE, StringResource.getResource("_iparam",
                                            new String[] {EntityXML.TYPES, this.getClass().getName(), id}));
                                        new MultiTypeWrn(e, Color.red, StringResource.getResource("_iparam",
                                            new String[] {EntityXML.TYPES, this.getClass().getName(), id}),
                                                null).renderSpecific("_label_resourcerror");                                                                            
                                    }
                                }                                
                            }    
                        }
                    }
                } break;
                case EntityXML.SHEET : {
                try {
                    String sheetName = eNode.getTextContent();
                    sheet = SpriteSheet.getSheet(sheetName);
                    if (sheet == null) {
                        sheet = new SpriteSheet(ImageIO.read(new File(PathManager.getInstance().getSheetPath(),
                                sheetName)));
                        SpriteSheet.putSheet(sheetName, sheet);
                    }
                } catch (Exception ex) {
                    //System.out.println("Error with sheets");
                    LOG.log(Level.SEVERE, null, ex);
                }
                } break;
                case EntityXML.IMAGE : {
                    if (_sprite == null) {
                        LOG.log(Level.WARNING, StringResource.getResource("_misplacedparam",
                                new String[] {EntityResource.class.getName(), id }));                        
                    } else {
                        Image img = ImageResource.getResource(eNode.getTextContent()).getBackImage();
                                                
                        if (img == null) {
                            LOG.log(Level.WARNING, StringResource.getResource("_mimage", new String[] {id}));
                        } else {
                            _sprite.setSprite(img);
                        }
                    }
                } break;
                case EntityXML.CONVERSATIONS : {
                    String[] _convMembers = eNode.getTextContent().split(DELIM);
                    groupResources = new ArrayList<>();
                    for (String s : _convMembers) {
                        groupResources.add(ConversationGroupResource.getResource(s));
                    }
                } break;
                case EntityXML.NAME : {
                    name =eNode.getTextContent();
                } break;
                case EntityXML.AI : {
                    String lua = ((Element)eNode).getAttribute(EntityXML.LUA);
                    if (lua == null) {
                        luaAi = Boolean.parseBoolean(lua);
                    }
                    ai = eNode.getTextContent();                    
                } break;                
                case EntityXML.ID : {                    
                    id = eNode.getTextContent();                    
                } break;
                case EntityXML.DURATION : {                    
                    _sprite.setDuration(Integer.parseInt(eNode.getTextContent()));
                } break;
                case EntityXML.X : {                                        
                    _sprite.setSpriteX(Integer.parseInt(eNode.getTextContent()));
                } break;
                case EntityXML.Y : {                    
                    _sprite.setSpriteY(Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case EntityXML.W : {                    
                    _sprite.setSpriteW(Integer.parseInt(eNode.getTextContent()));
                } break;
                case EntityXML.H : {
                    _sprite.setSpriteH(Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case EntityXML.GLOBALW : {                    
                    _globalW = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.GLOBALH : {                    
                    _globalH = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.KNOCKBACK : {
                    this.knockback = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.STATS : {
                    parse((Element)eNode);
                } break;    
                case EntityXML.STRENGTH : {
                    String[] values = eNode.getTextContent().split("-");
                    strengthMin = Integer.parseInt(values[0]);
                    strengthMax = Integer.parseInt(values[1]);
                } break;
                case EntityXML.AGILITY : {
                    String[] values = eNode.getTextContent().split("-");
                    agilityMin = Integer.parseInt(values[0]);
                    agilityMax = Integer.parseInt(values[1]);
                } break;   
                case EntityXML.SPEED : {
                    String[] values = eNode.getTextContent().split("-");
                    speedMin = Integer.parseInt(values[0]);
                    speedMax = Integer.parseInt(values[1]);
                } break;
                case EntityXML.ENDURANCE : {
                    String[] values = eNode.getTextContent().split("-");
                    enduranceMin = Integer.parseInt(values[0]);
                    enduranceMax = Integer.parseInt(values[1]);
                } break;
                case EntityXML.STRENGTHPER : {                    
                    strengthPer = Double.parseDouble(eNode.getTextContent());
                } break;
                case EntityXML.AGILITYPER : {                    
                    agilityPer = Double.parseDouble(eNode.getTextContent());
                } break;  
                case EntityXML.ENDURANCEPER : {                    
                    endurancePer = Double.parseDouble(eNode.getTextContent());
                } break;
                case EntityXML.SPEEDPER : {                    
                    speedPer = Double.parseDouble(eNode.getTextContent());
                } break;    
                case EntityXML.HEALTH : {
                    String[] values = eNode.getTextContent().split("-");
                    healthMin = Double.parseDouble(values[0]);
                    healthMax = Double.parseDouble(values[1]);
                } break;
                case EntityXML.HEALTHPER : {                    
                    healthPer = Double.parseDouble(eNode.getTextContent());
                } break; 
                case EntityXML.HEALTHBONUS : {
                    healthBonus = Double.parseDouble(eNode.getTextContent());
                } break;
                case EntityXML.STAMINABONUS : {
                    staminaBonus = Double.parseDouble(eNode.getTextContent());
                } break;
                case EntityXML.STAMINA : {
                    String[] values = eNode.getTextContent().split("-");
                    staminaMin = Double.parseDouble(values[0]);
                    staminaMax = Double.parseDouble(values[1]);
                } break;
                case EntityXML.STAMINAPER : {                    
                    staminaPer = Double.parseDouble(eNode.getTextContent());
                } break;
                case EntityXML.DAMAGE : {
                    String[] values = eNode.getTextContent().split("-");
                    damageMin = Double.parseDouble(values[0]);
                    damageMax = Double.parseDouble(values[1]);
                } break;
                case EntityXML.DAMAGEPER : {
                    damagePer = Double.parseDouble(eNode.getTextContent());
                } break;    
                case EntityXML.STAMINAREGEN : {
                    staminaRegen = Double.parseDouble(eNode.getTextContent());
                } break;
                case EntityXML.STAMINAREGENPER : {
                    staminaRegPer = Double.parseDouble(eNode.getTextContent());
                } break;
                case EntityXML.HEALTHREGEN : {
                    healthRegen = Double.parseDouble(eNode.getTextContent());
                } break;
                 case EntityXML.HEALTHREGENPER : {
                    healthRegPer = Double.parseDouble(eNode.getTextContent());
                } break;
                case EntityXML.ATKRATER : {
                    attackRater = Integer.parseInt(eNode.getTextContent());
                } break;  
                case EntityXML.DEFRATER : {
                    defenseRater = Integer.parseInt(eNode.getTextContent());
                } break;     
                case EntityXML.SPDRATER : {
                    speedRater = Integer.parseInt(eNode.getTextContent());
                } break;     
                case EntityXML.DMGRATER : {
                    damageRater = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.HPSPRATER : {
                    rater = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.ATKRADIUS : {
                    attackRadius = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.ATKRADIUSPER : {
                    atkRadiusPer = Double.parseDouble(eNode.getTextContent());
                } break;    
                case EntityXML.DBLFACTOR : {
                    doubleFactor = Double.parseDouble(eNode.getTextContent());
                } break;                    
                case EntityXML.INTCHANCE : {
                    interruptionChance = Double.parseDouble(eNode.getTextContent());
                } break;        
                case EntityXML.ATKBONUS : {
                    attRatingBonus = Integer.parseInt(eNode.getTextContent());
                } break; 
                case EntityXML.DEFBONUS : {
                    defRatingBonus = Integer.parseInt(eNode.getTextContent());
                } break;                   
                case EntityXML.EFFECTS : {
                    effects = new ArrayList<>();
                    parse((Element)eNode);                    
                } break;
                case EntityXML.EFFECT : {
                    String effectType = ((Element)eNode).getAttribute(EntityXML.EFTYPE);
                    try {
                        EffectEvent type = EffectEvent.valueOf(effectType);
                        Effect effect = new Effect(EffectResource.getResource(eNode.getTextContent()), type); 
                        effects.add(effect);
                    } catch (Exception e) {
                        LOG.log(Level.WARNING, StringResource.getResource("_rparam", new String[] {EntityXML.EFTYPE, id}));                        
                        continue;
                    }                                        
                } break;
                case EntityXML.ACTIVATEACTIONS : {
                    activationActions = new ArrayList<>();
                    parse((Element)eNode);
                } break; 
                case EntityXML.LUACTION : {
                    Action action = new Action();                    
                    action.setAction(eNode.getTextContent());
                    action.setType(Action.Type.EVENT);
                    action.setLua(true);
                                        
                    Element elemNode = (Element)eNode;                    
                    action.setMemorizable(Boolean.parseBoolean(elemNode.getAttribute(EntityXML.MEMORIZABLE)));                                                            
                    
                    if (elemNode.hasAttribute(EntityXML.ACTIONTYPE)) {
                        try {
                            ActionType type = ActionType.valueOf(elemNode.getAttribute(EntityXML.ACTIONTYPE));
                            action.setClickType(type);

                        } catch (Exception e) {
                            LOG.log(Level.WARNING, StringResource.getResource("_iparam", new String[] {EntityXML.ACTIONTYPE,
                                EntityXML.LUACTION, toString()}));
                            action.setClickType(ActionType.START);
                        }
                    } else {
                        action.setClickType(ActionType.START);
                    }
                    action.makeListener();
                    activationActions.add(action);
                } break;
                case EntityXML.ACTION : {
                    Action action = new Action();                    
                    action.setAction(eNode.getTextContent());
                    action.setType(Action.Type.EVENT);
                                        
                    Element elemNode = (Element)eNode;                    
                    action.setMemorizable(Boolean.parseBoolean(elemNode.getAttribute(EntityXML.MEMORIZABLE)));
                    
                    
                    if (elemNode.hasAttribute(EntityXML.SCRIPTYPE)) {
                        try {
                            switch (ScriptType.valueOf(elemNode.getAttribute(EntityXML.SCRIPTYPE))) {
                                case LUA : {                                    
                                    action.setLua(true);
                                } break;
                                case LISTENER : {
                                    action.setLua(false);
                                    action.makeListener();
                                }
                                default : break;
                            }
                        } catch (Exception e) {
                            LOG.log(Level.WARNING,StringResource.getResource("_iattrib",
                                    new String[] {EntityXML.SCRIPTYPE, EntityXML.ACTION, toString()}));
                        }
                    }
                    
                    if (elemNode.hasAttribute(EntityXML.ACTIONTYPE)) {
                        try {
                            ActionType type = ActionType.valueOf(elemNode.getAttribute(EntityXML.ACTIONTYPE));
                            action.setClickType(type);

                        } catch (Exception e) {
                            LOG.log(Level.WARNING, StringResource.getResource("_iattrib", new String[] {EntityXML.ACTIONTYPE,
                                EntityXML.ACTION, toString()}));
                            action.setClickType(ActionType.START);
                        }
                    } else {
                        action.setClickType(ActionType.START);
                    }                    
                    activationActions.add(action);
                } break;    
                default : break;
            }
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    /**
     * Metoda ktora skopiruje jeden EntityResource zadany v parametri <b>res</b>
     * do novo vytvoreneho resource.
     * @param res Resource z ktoreho kopirujeme do nasho resource udaje.
     * @throws Exception Vynimka pri nepodareni kopirovania.
     */
    @Override
    protected void copy(EntityResource res) throws Exception {
        this.id = res.id;       
    }  
    
    /**
     * Metoda ktora zvaliduje entitu podla danych konceptov.
     */
    private void validate() {  
        if (entType == null) {
            LOG.log(Level.SEVERE, StringResource.getResource("_mparam",
                                            new String[] {EntityXML.ENTITYTYPE, this.getClass().getName(), id}));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_mparam",
                new String[] {EntityXML.ENTITYTYPE, this.getClass().getName(), id}), null).renderSpecific("_label_resourcerror");
        }
        if (itemLevelType == 0) {
            itemLevelType = 1;
        }
    }
    
    // </editor-fold>
}
