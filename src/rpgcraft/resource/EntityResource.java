/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.effects.Effect;
import rpgcraft.effects.EffectType;
import rpgcraft.entities.Armor;
import rpgcraft.entities.Armor.ArmorType;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Entity.EntityType;
import rpgcraft.entities.Item;
import rpgcraft.entities.Item.ItemType;
import rpgcraft.entities.Misc.MiscType;
import rpgcraft.entities.MovingEntity;
import rpgcraft.entities.TileItem.TileType;
import rpgcraft.entities.Weapon.WeaponType;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.SpriteSheet;
import rpgcraft.manager.PathManager;
import rpgcraft.xml.EntityXML;

/**
 *
 * @author doma
 */
public class EntityResource extends AbstractResource<EntityResource> {
    private static final Logger LOG = Logger.getLogger(MovingEntity.class.getName());
    private static final String DELIM = "[,]", PDELIM = "[:]";
    
    private static Sprite _sprite = null;
    private static ArrayList<Sprite> _sprites = null;
    private static Sprite.Type _type = null;    
    private static int iSprite = 0;
    
    private static HashMap<String, EntityResource> entityResources = new HashMap<>();
    
    private int _globalW = 0, _globalH = 0;  
            
    private String name;
    private String id;
    private EntityType entType;
    private SpriteSheet sheet;
    private HashMap<Sprite.Type, ArrayList<Sprite>> movingEntitySprites;
    private ArrayList<Effect> effects;
    private ArrayList<Item.ItemType> itemTypes;
        
    private Item activeItem;    
    private String ai;
    
    private double healthMin;
    private double healthMax;
    private double healthRegen;
    private double staminaMin;
    private double staminaMax;
    private double staminaRegen;
    private int strengthMin;
    private int strengthMax;
    private int agilityMin;     
    private int agilityMax;
    private int speedMin;   
    private int speedMax;    
    private double dmgBonus;
    
    private int aggresivity;
    private int group;
    private double doubleFactor;
    private double interruptionChance;
    private double concentration;
    
    private double attack;
    private double defenseA;
    private double defenseP;
    private int attackRadius;
    private double attackRating;
    private double defenseRating;
    private int attRatingBonus;
    private int defRatingBonus;
    private int attackRater;
    private int defenseRater;
    private int speedRater;
    
    private boolean moveable;
    private boolean attackable;
    
    
    public static EntityResource getResource(String name) {
        return entityResources.get(name);
    }    
    
    private EntityResource(Element elem) {
        movingEntitySprites = new HashMap<>();
        parse(elem);        
        validate();
        entityResources.put(id, this);
    }
    
    public static EntityResource newBundledResource(Element elem) {
        return new EntityResource(elem);                
    }
    
    private void validate() {  
        if (entType == null) {
            LOG.log(Level.SEVERE, StringResource.getResource("_mparam",
                                            new String[] {EntityXML.ENTITYTYPE, this.getClass().getName(), id}));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_mparam",
                new String[] {EntityXML.ENTITYTYPE, this.getClass().getName(), id}), null).renderSpecific("_label_resourcerror");
        }
    }
    
    public String getName() {
        return name;
    }
    
    public String getId() {
        return id;
    }
    
    public SpriteSheet getSheet() {
        return sheet;
    }
    
    public int getGroup() {
        return group;
    }
    
    public String getAi() {
        return ai;
    }
    
    public EntityType getEntityType() {
        return entType;
    }
    
    public ArrayList<ItemType> getItemType() {
        return itemTypes;
    }
    
    public double getConcentration() {
        return concentration;
    }
    
    public HashMap<Sprite.Type, ArrayList<Sprite>> getEntitySprites() {
        return movingEntitySprites;
    }
    
    public ArrayList<Effect> getEntityEffects() {
        return effects;
    }
    
    public Double getMinHealth() {
        return healthMin;
    }
    
    public Double getMaxHealth() {
        return (healthMax == 0 ? 1 : healthMax);
    }
    
    public Double getHealthRegen() {
        return healthRegen;
    }
    
    public Double getMinStamina() {
        return staminaMin;
    }
    
    public Double getMaxStamina() {
        return staminaMax;
    }
    
    public Double getStaminaRegen() {
        return staminaRegen;
    }        
    
    public int getAttackRater() {
        return attackRater;
    }
    
    public int getDefenseRater() {
        return defenseRater;
    }
    
    public int getSpeedRater() {
        return speedRater;
    }
    
    public int getAttRatingBonus() {
        return attRatingBonus;
    }
    
    public int getDefRatingBonus() {
        return defRatingBonus;
    }
    
    public Double getDmgBonus() {
        return dmgBonus;
    }
    
    public int getMinStr() {
        return strengthMin;
    }
    
    public int getMaxStr() {
        return strengthMax;
    }
    
    public int getMinAgi() {
        return agilityMin;
    }
    
    public int getMaxAgi() {
        return agilityMax;
    }
    
    public int getMinSpd() {
        return speedMin;
    }
    
    public int getMaxSpd() {
        return speedMax;
    }
    
    public Item getActiveItem() {
        return activeItem;
    }
    
    public double getDoubleFactor() {
        return doubleFactor;
    }
    
    public double getInterChance() {
        return interruptionChance;
    }
    
    public int getAttackRadius() {
        return attackRadius == 0 ? 32 : attackRadius;
    }
    
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
                    System.out.println("Type: " +_sprite.getType().toString());
                    System.out.println("Loading sprite" + ++iSprite );
                    
                    // Globalne nastavene vysky a sirky sprite 
                                                      
                    parse((Element)eNode);                    
                    
                    if (sheet != null && _sprite.getSprite() == null) {
                        _sprite.setImagefromSheet(sheet, _globalW, _globalH);
                    }
                    _sprites.add(_sprite);
                    System.out.println("Done Loading");
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
                    System.out.println("Error with sheets");
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
                case EntityXML.NAME : {
                    name =eNode.getTextContent();
                } break;
                case EntityXML.AI : {
                    ai = eNode.getTextContent();
                } break;
                case EntityXML.ID : {
                    System.out.println(eNode.getTextContent());
                    id = eNode.getTextContent();                    
                } break;
                case EntityXML.DURATION : {
                    System.out.println(eNode.getTextContent());
                    _sprite.setDuration(Integer.parseInt(eNode.getTextContent()));
                } break;
                case EntityXML.X : {                    
                    System.out.println(eNode.getTextContent());
                    _sprite.setSpriteX(Integer.parseInt(eNode.getTextContent()));
                } break;
                case EntityXML.Y : {
                    System.out.println(eNode.getTextContent());
                    _sprite.setSpriteY(Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case EntityXML.W : {                    
                    _sprite.setSpriteW(Integer.parseInt(eNode.getTextContent()));
                } break;
                case EntityXML.H : {
                    _sprite.setSpriteH(Integer.parseInt(eNode.getTextContent()));                    
                } break;
                case EntityXML.GLOBALW : {
                    System.out.println(eNode.getTextContent());
                    _globalW = Integer.parseInt(eNode.getTextContent());
                } break;
                case EntityXML.GLOBALH : {
                    System.out.println(eNode.getTextContent());
                    _globalH = Integer.parseInt(eNode.getTextContent());
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
                case EntityXML.HEALTH : {
                    String[] values = eNode.getTextContent().split("-");
                    healthMin = Double.parseDouble(values[0]);
                    healthMax = Double.parseDouble(values[1]);
                } break;   
                case EntityXML.STAMINA : {
                    String[] values = eNode.getTextContent().split("-");
                    staminaMin = Double.parseDouble(values[0]);
                    staminaMax = Double.parseDouble(values[1]);
                } break;                
                case EntityXML.STAMINAREGEN : {
                    staminaRegen = Double.parseDouble(eNode.getTextContent());
                } break;                
                case EntityXML.HEALTHREGEN : {
                    healthRegen = Double.parseDouble(eNode.getTextContent());
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
                case EntityXML.ATKRADIUS : {
                    attackRadius = Integer.parseInt(eNode.getTextContent());
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
                case EntityXML.DMGBONUS : {                    
                    dmgBonus = Double.parseDouble(eNode.getTextContent());                    
                } break;    
                case EntityXML.EFFECTS : {
                    effects = new ArrayList<>();
                    parse((Element)eNode);                    
                } break;
                case EntityXML.EFFECT : {
                    Effect effect = new Effect(EffectResource.getResource(eNode.getTextContent())); 
                    effects.add(effect);
                } break;
                default : break;
            }
        }
    }

    @Override
    protected void copy(EntityResource res) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
