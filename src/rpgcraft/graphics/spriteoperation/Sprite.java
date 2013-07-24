/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.spriteoperation;

import java.awt.Image;
import java.awt.image.BufferedImage;
import rpgcraft.graphics.ImageOperation;

/**
 * Trieda ktora ma v sebe zdruzuje zakladne operacie so Sprite. Nastavuje ich typ, trvacnost,
 * obrazok, x-ovu a y-ovu poziciu v SpriteSheet ako aj vysku a sirku vnom.
 * @see SpriteSheet
 */
public class Sprite {
    
    /**
     * Vypis vsetkych typov v akych sa moze vyskytnut Sprite. Tieto typy 
     * uzko suvisia s typmi definovanymi v triede Sprite
     * @see Sprite
     */
    public static enum Type {
        ITEM(0),
        TILE(0),        
        TILEDESTROYED(-1),
        UP(0),
        DOWN(2),
        LEFT(3),
        RIGHT(1),
        ATTACK(8),
        DEFENSE(9),
        CAST(10),
        WALKING(4),
        RUNNING(5),
        SNEAKING(6),
        CROUCHING(7),
        MINING(11),
        CRAFTING(13),
        TALKING(12);
        
        private int value;
        
        private Type(int value) {
            this.value = value;
        }
        
        public int getValue() {
            return value;
        }
        
    }
    
    /**
     * Obrazok pre Sprite a pre Sprite ktory moze byt nanom
     */
    private Image sprite;
    /**
     * Typ Sprite vybraty z enum Type v tejto triede
     */
    private Type type;   
    /**
     * Trvacnost jednej Sprite
     */
    private int duration;
    /**
     * x-ova pozicia v SpriteSheet
     */
    private int x = 0;
    /**
     * y-ova pozicia v SpriteSheet
     */
    private int y = 0;
    /**
     * Sirka Sprite
     */
    private int w = 0;
    /**
     * Vyska Sprite
     */
    private int h = 0;
    
    /**
     * Konstruktor pre Sprite ktory vytvori novu Sprite s nejakym typom z enum Type,
     * obrazkom zo SpriteSheet a trvacnostou Sprite.
     * @param type Typ Sprite z enum Type
     * @param sprite Obrazok Sprite
     * @param duration Trvacnost tohoto Sprite
     * 
     */
    public Sprite(Type type, Image sprite, int duration) { 
        this.sprite = sprite;
        this.type = type;
        this.duration = duration;
        this.w = sprite.getWidth(null);
        this.h = sprite.getHeight(null);
    }
    
    /**
     * Vseobecny konstrukto ktory vytvori novu Sprite bez typu, obrazku a s trvacnostou
     * nastavenou na 0. Na dodefinovanie sluzia potom metody set###
     */
    public Sprite() {
        this.duration = 0;
        this.sprite = null;
        this.type = null;
    }
    
    /**
     * Konstruktor ktory vytvori novy Sprite len s definovanym typom a s dalsimi hodnotami
     * nastavenymi na null ci 0. Na dodefinovanie sluzia potom metody set###
     * @param type Typ Sprite z enum Type
     */
    public Sprite(Type type) {
        this.type = type;
    }
    
    /**
     * Konstruktor ktory vytvori novy Sprite od zaciatku s tym ze nastavi typ Sprite,
     * trvacnost, no obrazok Sprite si ziska zo SpriteSheet ktory je predany ako parameter.
     * Pozicie v SpriteSheet su predane parametrami x,y a vyska a sirka tejto Sprite parametrami
     * w a h (vacsinou 32). Na ziskanie obrazku prislusnych rozmerov sluzi metoda getSubimage.
     * @param type Typ Sprite z enum Type
     * @param x X-ova pozicia v SpriteSheet
     * @param y Y-ova pozicia v SpriteSheet
     * @param w Sirka Sprite
     * @param h Vyska Sprite
     * @param sheet SpriteSheet z ktoreho ziska obrazok Sprite
     * @param duration Trvacnost tohoto Sprite
     */
    public Sprite(Type type, int x, int y, int w, int h, SpriteSheet sheet, int duration) {
        this.type = type;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.duration = duration;
        this.sprite = sheet.getImage().getSubimage(x, y, w, h);
    }        
    
    /**
     * Metoda ktora nastavi obrazok tohoto Sprite.
     * @param sprite Obrazok pre Sprite
     */
    public void setSprite(Image sprite) {
        this.sprite = sprite;
    }        
    
    /**
     * Metoda ktora nastavi/prenastavi trvacnost Sprite
     * @param duration Trvacnost tohoto Sprite
     */
    public void setDuration(int duration) {
        this.duration = duration;
    }
    
    /**
     * Metoda ktora nastavi Typ tohoto Sprite
     * @param type Typ Sprite z enum Sprite
     */
    public void setType(Type type) {
        this.type = type;
    }
    
    /**
     * Metoda ktora nastavi Typ tohoto Sprite z textovej hodnoty pomocou 
     * metody valueOf. 
     * @param type Textovy typ Sprite
     */
    public void setType(String type) {
        this.type = Type.valueOf(type);
    }
    
    /**
     * Metoda ktora nastavi x-ovu poziciu v SpriteSheet
     * @param x X-ova pozicia v SpriteSheet
     */
    public void setSpriteX(int x) {
        this.x = x;
    }
    
    /**
     * Metoda ktora nastavi y-ovu poziciu v SpriteSheet
     * @param y Y-ova pozicia v SpriteSheet
     */
    public void setSpriteY(int y) {
        this.y = y;
    }
    
    /**
     * Metoda ktora nastavi sirku Sprite
     * @param w Sirka Sprite
     */
    public void setSpriteW(int w) {
        this.w = w;
    }
    
    /**
     * Metoda ktora nastavi vysku Sprite
     * @param h Vyska Sprite
     */
    public void setSpriteH(int h) {
        this.h = h;
    }
    
    /**
     * Metoda ktora vrati obrazok pre Sprite
     * @return Obrazok Sprite
     */
    public Image getSprite() {
        return sprite;
    }
    
    /**
     * Metoda ktora vrati trvacnost Sprite
     * @return Trvacnost Sprite
     */
    public int getDuration() {
        return duration;                
    }
    
    /**
     * Metoda ktora vrati x-ovu poziciu v SpriteSheet
     * @return X-ova pozicia.
     */
    public int getSpriteX() {
        return x;
    }
    
    /**
     * Metoda ktora vrati y-ovu poziciu v SpriteSheet
     * @return Y-ova pozicia.
     */
    public int getSpriteY() {
        return y;
    }
    
    /**
     * Metoda ktora vrati typ tohoto Sprite
     * @return Typ sprite z enum.
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Metora ktora ma za ulohu ziskat Sprite danej sirky a vysky zo SpriteSheet. 
     * Nato sluzia predane parametre sheet, w a h ktorymi vyberieme potrebnu cast z SpriteSheet.
     * Na zaciatku testuje ci boli nastavene nejake vysky a sirky pre aktualnu Sprite. Ked nie
     * tak sa nastavia a nasledne iba vyberie pod obrazok zo SpriteSheet pomocou x-ovej a y-ovej pozicie.
     * Ked boli nastavene tak to znamena ze zo SpriteSheet musime vybrat obrazok tychto hodnot a podla parametrov
     * w a h ich vyskalovat. Na skalovanie pouzivame metody definovane v nasej triede ImageOperation.
     * @param sheet SpriteSheet z ktoreho ziskava obrazok
     * @param w Sirka obrazku
     * @param h Vyska obrazku
     * @see ImageOperation
     */
    public void setImagefromSheet(SpriteSheet sheet, int w, int h) {
        if (this.w == 0) {
            this.w = w;
        }
        if (this.h == 0) {
            this.h = h;
        }
        if (((w != this.w)&&(this.w != 0))||((h != this.h)&&(this.h != 0))) {
           Image subImage = sheet.getImage().getSubimage(x * this.w, y * this.h, this.w, this.h);
           ImageOperation op = new ImageOperation(subImage);
           op.createBufferedImages(BufferedImage.TYPE_INT_ARGB);           
           op.betterresizeImage(w, h, BufferedImage.SCALE_FAST);
           this.sprite = op.getShowImg();                      
        } else {
            this.sprite = sheet.getImage().getSubimage(x * w, y * h, w, h);
        }
    }
    
    /**
     * Staticka metoda pristupna odvsadial, ktora zo SpriteSheet/sheet pomocou ostatnych parametrov
     * ziska obrazok z pozicie x a y danej sirky a vysky.
     * @param sheet SpriteSheet z ktoreho ziskava obrazok
     * @param x X-ova pozicia v SpriteSheet
     * @param y Y-ova pozicia v SpriteSheet
     * @param w Sirka obrazku
     * @param h Vyska obrazku
     * @return Obrazok danych rozmerov zo SpriteSheet
     */
    public static Image imageFromSheet(BufferedImage sheet, int x, int y, int w, int h) {
        return sheet.getSubimage(x, y, w, h);        
    }
}
