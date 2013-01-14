/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

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
import rpgcraft.effects.EffectType;
import rpgcraft.entities.Entity;
import rpgcraft.entities.MovingEntity;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.spriteoperation.SpriteSheet;
import rpgcraft.manager.PathManager;
import rpgcraft.xml.EntityXML;

/**
 *
 * @author doma
 */
public class EntityResource extends AbstractResource<EntityResource> {
    
    private static Sprite _sprite = null;
    private static ArrayList<Sprite> _sprites = null;
    private static Sprite.Type _type = null;
    private static int iSprite = 0;
    
    private static HashMap<String, EntityResource> entityResources = new HashMap<>();
    
    private int _globalW = 0, _globalH = 0;  
            
    private String name;
    private String id;
    private SpriteSheet sheet;
    private HashMap<Sprite.Type, ArrayList<Sprite>> movingEntitySprites;
    private ArrayList<Effect> effects;
        
    private Entity activeItem;    
    
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
    private int damageMin;    
    private int damageMax;
    private int tileDamage;
    
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
        entityResources.put(id, this);
    }
    
    public static EntityResource newBundledResource(Element elem) {
        return new EntityResource(elem);                
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
    
    public Entity getActiveItem() {
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
                    if (sheet != null) {
                        _sprite.setImagefromSheet(sheet, _globalW, _globalH);
                    }
                    _sprites.add(_sprite);
                    System.out.println("Done Loading");
                } break;
                case EntityXML.TYPE : {
                    _type = Sprite.Type.valueOf(eNode.getTextContent());
                    
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
                    Logger.getLogger(MovingEntity.class.getName()).log(Level.SEVERE, null, ex);
                }
                } break;
                case EntityXML.NAME : {
                    name =eNode.getTextContent();
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
                case EntityXML.ABONUS : {
                    attRatingBonus = Integer.parseInt(eNode.getTextContent());
                } break; 
                case EntityXML.DBONUS : {
                    defRatingBonus = Integer.parseInt(eNode.getTextContent());
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
