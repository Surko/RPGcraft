/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.effects.Effect;
import rpgcraft.entities.Entity;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.StatResource;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.TextUtils;

/**
 * CharInfo je trieda ktora sa stara o vytvorenie a zobrazenie playerovho infa.
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu.
 * @author kirrie
 */
public class CharInfo extends AbstractInMenu<CharInfo> {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final String CHARINFOID = "_charinfo";
    private static final String TEST = "Testing";
    private static final String SECOND = StringResource.getResource("seconds");
    private static final String EFFECTS = StringResource.getResource("effects");
    private static final String HEALTH = StringResource.getResource("health"),
            STAMINA = StringResource.getResource("stamina"); 
    private static final String CHARINFO = StringResource.getResource(CHARINFOID); 
    private static final String DELIM = " : ", STATDELIM = "/";
    
    private static final int wGap = 5, hGap = 5;    
    private static final int infoWidth = 300, infoHeight = 400;
    
    private static final StatResource.Stat[] mainStats = new StatResource.Stat[] {        
        StatResource.Stat.STRENGTH,        
        StatResource.Stat.AGILITY,        
        StatResource.Stat.SPEED,        
        StatResource.Stat.ENDURANCE,
        StatResource.Stat.STRENGTHPER,
        StatResource.Stat.AGILITYPER,
        StatResource.Stat.SPEEDPER,
        StatResource.Stat.ENDURANCEPER,
    };
    
    private static final StatResource.Stat[] healthStats = new StatResource.Stat[] {        
        StatResource.Stat.HEALTHBONUS,        
        StatResource.Stat.HEALTHMAXPER,        
        StatResource.Stat.HEALTHREGEN,        
        StatResource.Stat.HEALTHREGENPER,
        StatResource.Stat.STAMINABONUS,
        StatResource.Stat.STAMINAMAXPER,
        StatResource.Stat.STAMINAREGEN,
        StatResource.Stat.STAMINAREGENPER
    };
    
    private static final StatResource.Stat[] atkStats = new StatResource.Stat[] { 
        StatResource.Stat.ATKRADIUS,
        StatResource.Stat.ATKRADIUSPER,
        StatResource.Stat.ATKRATING,        
        StatResource.Stat.ATKRATINGBONUS,        
        StatResource.Stat.ATKRATINGPER,        
        StatResource.Stat.DEFRATING,
        StatResource.Stat.DEFRATINGBONUS,
        StatResource.Stat.DEFRATINGPER,
        StatResource.Stat.DAMAGE,
        StatResource.Stat.DMGBONUS,
        StatResource.Stat.DAMAGEPER       
    };
    
    protected int width = infoWidth, height = infoHeight;
    protected Font font,boldTitleFont,italicFont, boldNormalFont;
    private int[] txtSize, boldSize, italicSize, boldNormalSize;
    private int modifCount = 1;
    private int itemInfoStartY = 0, starty;
    private BufferedImage fullCharInfoImage, charInfoImage;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory" >
    /**
     * Prazdny konstruktor pre character info volany pri newInstance metode.
     * Vytvarame nim instancie tohoto menu a nasledne volame initialize.
     */
    public CharInfo() {
        super(null, null);
    }
    
    /**
     * Konstruktor pre character info ktore vytvori character info pre entitu zadanu parametrom <b>e</b>.    
     * Menu spracovava vstup z InputHandlera input. Parameter <b>menu</b> sluzi
     * ako kontajner pre toto menu. Preinicializuje sa graphics metodou setGraphics
     * a nastavime changedState na true aby sa prekreslilo cele menu. Nakonci 
     * pridame toto menu do listu s menu.
     * @param e Entita pre ktoru vytvarame info
     * @param input Input podla ktoreho spracovavame udaje
     * @param menu Menu v ktorom je toto menu usadene.
     */
    public CharInfo(Entity e, InputHandle input, AbstractMenu menu) {
        super(e, input); 
        this.menu = menu;
        setGraphics();
        this.changedState = true;
        menuList.put(CHARINFOID, this);
    }
    // </editor-fold>
               
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora initializuje toto menu s novymi udajmi. 
     * Novymi udajmi je entita zadana parametrom pre ktoru vytvarame menu.
     * Parameter origMenu sluzi ako vzor pre toto menu z ktoreho ziskavame pozadie tohoto menu.
     * @return Initializovane CharInfo.
     */
    @Override
    public CharInfo initialize(CharInfo origMenu, Entity e1, Entity e2) { 
        this.entity = e1;
        this.input = origMenu.getInput();
        this.menu = origMenu.getMenu();
        this.changedState = true;
        this.toDraw = origMenu.toDraw;                
        this.boldNormalFont = origMenu.boldNormalFont;
        this.boldNormalSize = origMenu.boldNormalSize;
        this.boldTitleFont = origMenu.boldTitleFont;
        this.boldSize = origMenu.boldSize;
        this.italicFont = origMenu.italicFont;
        this.italicSize = origMenu.italicSize;
        this.font = origMenu.font;
        this.txtSize = origMenu.txtSize;
        return this;
    }            
          
    // </editor-fold>
    
    // <editor-fold defaultstate="Collapsed" desc=" Update ">
    
    /**
     * Metoda ktora zavola aktualizovanie menu. Kontroluje sa zmena stavu ako je tomu
     * aj pri inych menu a nasledne vykona aktualizovanie obrazku ked sa stav zmenil.
     */
    @Override
    public void update() {
        if (changedState) {
            updateImage();
            changedState = false;
        }
    }
    
    /**
     * Metoda ktora rekalkuluje pozicie tohoto menu v hracom paneli.
     */
    @Override
    public void recalculatePositions() {
        this.xPos = wGap;
        this.yPos = hGap;
    }

    /**
     * Metoda ktora aktualizuje staty o playerovi a vypise ich do fullCharInfoImage.
     * Volana je len pri zmene stavu => vzdy na zaciatku a nasledne ked nieco zmeni stav.
     * Vykreslujeme informacie o hracovi ako meno, zivot, stamina a
     * postupne vykreslujeme staty ktore sme si definovali v premennych (mainStats,healthStats,...)
     * Po kazdom vykresleni statu zvysujeme starty poziciu.
     */
    private void updateImage() {
        fullCharInfoImage = new BufferedImage(getWidth() - wGap,
                (modifCount) * getHeight() - 2 * hGap, 
                BufferedImage.TRANSLUCENT);
        int w = fullCharInfoImage.getWidth(null);
        Graphics g = fullCharInfoImage.getGraphics();
        
        int txtWidth = TextUtils.getTextWidth(boldTitleFont, CHARINFO);   
        starty = hGap + boldSize[1];
        g.setColor(Color.BLACK);
        g.setFont(boldTitleFont);        
        g.drawString(CHARINFO, (getWidth() - txtWidth)/2, starty);  
        
        int textWidth;        
        ArrayList<String> parsedTexts;        
        g.setColor(Color.BLACK); 
        g.setFont(boldTitleFont);
                            
        // Vykreslenie nazvu entity
        String itemText = entity.getName();                
        textWidth = TextUtils.getTextWidth(boldTitleFont, itemText);
        starty += 2 * hGap + boldSize[1];        
        g = setNewGraphics(fullCharInfoImage, boldTitleFont);        
        g.drawString(itemText, (getWidth() - wGap - textWidth) / 2 , starty); 
                
        // Vykreslenie obrazku predmetu
        Image itemImg = entity.getTypeImage(); 
        if (itemImg != null) {  
            g.drawImage(itemImg, (getWidth() - wGap - itemImg.getWidth(null)) / 2 , starty, null);
            starty += 2 * hGap + itemImg.getHeight(null);             
            g = setNewGraphics(fullCharInfoImage, italicFont);                       
        }                        
        
        g.setFont(boldNormalFont);
        // Odsadenie od textu a statov
        starty += 2 * hGap;                   
        g.drawString(HEALTH + DELIM + (int)entity.getHealth() + STATDELIM +
                entity.getStatValue(StatResource.Stat.HEALTHMAX, true), wGap , starty);               
        // Odsadenie durability od statov
        starty += 2 * hGap + boldNormalSize[1];
        g.drawString(STAMINA + DELIM + (int)entity.getStamina() + STATDELIM +
                entity.getStatValue(StatResource.Stat.STAMINAMAX, true), wGap , starty);        
        starty += 2 * hGap + boldNormalSize[1];
        
        // Staty o zivote :D
        for (StatResource.Stat stat : healthStats) {
            g.drawString(stat.getName() + DELIM + entity.getStatValue(stat), wGap , starty);
            starty += boldNormalSize[1];
            fullCharInfoImage = controlImageHeight(fullCharInfoImage, starty);
            g = setNewGraphics(fullCharInfoImage, boldNormalFont);
        }
        
        // Odsadenie dvoch rozlicnych statov 
        starty += 2 * hGap;
        
        //
        for (StatResource.Stat stat : mainStats) {
            g.drawString(stat.getName() + DELIM + entity.getStatValue(stat), wGap , starty);
            starty += boldNormalSize[1];
            fullCharInfoImage = controlImageHeight(fullCharInfoImage, starty);            
            g = setNewGraphics(fullCharInfoImage, boldNormalFont);
        }
        
        // Odsadenie dvoch rozlicnych statov 
        starty += 2 * hGap;
        
        for (StatResource.Stat stat : atkStats) {
            g.drawString(stat.getName() + DELIM + entity.getStatValue(stat), wGap , starty);
            starty += boldNormalSize[1];
            fullCharInfoImage = controlImageHeight(fullCharInfoImage, starty);  
            g = setNewGraphics(fullCharInfoImage, boldNormalFont);
        }
        
        starty += 2 * hGap + boldSize[1];   
        
        txtWidth = TextUtils.getTextWidth(boldTitleFont, EFFECTS);
        g.setColor(Color.BLACK);
        g.setFont(boldTitleFont);        
        g.drawString(EFFECTS, (getWidth() - txtWidth)/2, starty);
        
        starty += 2 * hGap; 
        
        g.setFont(boldNormalFont);
        for (Effect effect : entity.getActiveEffects()) {
            Image effectImage = effect.getEffectImage();
            if (effectImage != null) {
                g.drawImage(effectImage, wGap, starty, null);
                int textStarty = starty + (effectImage.getHeight(null) + boldNormalSize[1])/2;
                g.drawString(effect.getName() + DELIM + (effect.getMaxSpan() - effect.getLifeSpan()) + SECOND
                        , effectImage.getWidth(null) + 2*wGap, textStarty);
                starty += effectImage.getHeight(null) + wGap;                                
            } else {
                g.drawString(effect.getName() + DELIM + (effect.getMaxSpan() - effect.getLifeSpan()) + SECOND
                        , wGap, starty);
                starty += boldNormalSize[1] + wGap;                
            }
        }
        
        charInfoImage = fullCharInfoImage.getSubimage(0, 0, getWidth() - wGap, getHeight() - 2 * hGap);
        
    }
    
    /**
     * Metoda ktora nastavi novy graficky kontext do ktoreho vykreslujeme staty playera.
     * Metoda je volana ked uz nie je miesto pre vykreslenie statov a vytvarame novy obrazok
     * modifCount-krat vacsi.
     * @param img Obrazok z ktoreho ziskavame graficky kontext
     * @param font Font ktory nastavime po ziskany kontextu
     * @return Graficky kontext do ktoreho budeme kreslit.
     */
    private Graphics setNewGraphics(Image img, Font font) {
        Graphics _g = img.getGraphics();
        _g.setFont(font);
        _g.setColor(Color.BLACK);
        return _g;
    }
    
    /**
     * Metoda ktora kontroluje vysku obrazku. Ked vyska presiahla tu zadanu parametrom <b>height</b>
     * tak vytvarame novu intanciu obrazku do ktoreho vykreslime originalny obrazok.
     * @param img Obrazok ktoreho vysku kontrolujeme
     * @param height Vyska ktoru kontrolujeme
     * @return Obrazok s novou vyskou a prekopirovanym obrazkom.
     */
    private BufferedImage controlImageHeight(BufferedImage img, int height) {        
        if (height > img.getHeight(null)) {
            modifCount++;
            BufferedImage newImg = new BufferedImage(getWidth() - wGap,
                modifCount * getHeight() - hGap, 
                BufferedImage.TRANSLUCENT);            
            newImg.getGraphics().drawImage(img, 0, 0, null);            
            return newImg;
        }
        return img;
    } 
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * <i>{@inheritDoc } </i>
     * <p>
     * V tomto menu nastavujeme vysky a fonty pre texty.
     * </p>
     */
    @Override
    protected void setGraphics() {
        super.setGraphics();
        Graphics g = toDraw.getGraphics();        
        setFonts();               
    }

    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Fonty su v tomto menu dolezite pri aktualizovani obrazku so statmi.
     * </p>
     */
    @Override
    public void setFonts() {
        font = TextUtils.DEFAULT_FONT;        
        boldTitleFont = TextUtils.DEFAULT_FONT.deriveFont(Font.BOLD, 15f);        
        boldNormalFont = TextUtils.DEFAULT_FONT.deriveFont(Font.BOLD, 13f);
        italicFont = TextUtils.DEFAULT_FONT.deriveFont(Font.ITALIC, 13f); 
        txtSize = TextUtils.getTextSize(font, TEST);
        italicSize = TextUtils.getTextSize(italicFont, TEST);
        boldSize = TextUtils.getTextSize(boldTitleFont, TEST);
        boldNormalSize = TextUtils.getTextSize(boldNormalFont, TEST);        
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    
    /**
     * Metoda ktora vykresli info o playerovi. Kedze cele menu sa sklada z dvoch nabufferovanych
     * obrazkov toDraw a charInfoImage tak nam staci iba vykreslit tieto dva obrazky.
     * Pri neaktivovanom menu prekryjeme menu tmavsou farbou
     * @param g Graficky kontext kde vykreslujeme menu
     */
    @Override
    public void paintMenu(Graphics g) {
        if (visible) {
            g.drawImage(toDraw, xPos, yPos, null);        
            g.drawImage(charInfoImage, xPos + wGap, yPos + hGap, null);                        
            if (!activated) {
                g.setColor(Colors.getColor(Colors.selectedColor));
                g.fillRoundRect(xPos, yPos, getWidth(), getHeight(), wGap, hGap);
            }
        }
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int getWGap() {
        return wGap;
    }

    /**
     * {@inheritDoc}
     * @return {@inheritDoc}
     */
    @Override
    public int getHGap() {
        return hGap;
    }
    
    /**
     * Metoda vrati sirku menu pre info menu
     * @return Sirka info menu
     */
    @Override
    public int getWidth() {
        if (width <= 0) {
            return infoWidth;
        }
        return width;
    }
    
    /**
     * Metoda vrati vysku menu pre info menu
     * @return Vyska info menu
     */
    @Override
    public int getHeight() {
        if (height <= 0) {
            return infoHeight;
        }
        return height;
    }

    /**
     * Metoda ktora vrati meno tohoto menu.
     * @return 
     */
    @Override
    public String getName() {
        return CHARINFOID;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handling ">
    /**
     * Metoda ktora spracovava vstup uzivatela.
     */
    @Override
    public void inputHandling() {
        if (input.runningKeys.contains(InputHandle.down.getKeyCode())) {            
            if (getHeight() > starty || itemInfoStartY >= starty) {
                return;
            }                
            if (itemInfoStartY + 10 >= starty - 1) {
                itemInfoStartY = starty - 1;                
            } else {
                itemInfoStartY+= 10;                        
            } 
            
            try {
                charInfoImage = fullCharInfoImage.getSubimage(0, itemInfoStartY,
                        getWidth() - wGap, getHeight() - 2 * hGap);
            } catch (Exception e) {
                charInfoImage = null;
            }
        }
        
        if (input.runningKeys.contains(InputHandle.up.getKeyCode())) {  
            if (itemInfoStartY <= 0) {
                return;
            }
            if (itemInfoStartY - 10 < 0) {
                itemInfoStartY = 0;                
            } else {
                itemInfoStartY-= 10;                
            }
            
            try {
                charInfoImage = fullCharInfoImage.getSubimage(0, itemInfoStartY,
                        getWidth() - wGap, getHeight() - 2 * hGap);
            } catch (Exception e) {
                charInfoImage = null;
            }
        }
        
        if (input.clickedKeys.contains(InputHandle.escape.getKeyCode()) || 
                input.clickedKeys.contains(InputHandle.character.getKeyCode())) {
            exit();   
            menu.setInMenu(null);
        }
    }   
    
    /**
     * <i>{@inheritDoc} </i>     
     * @param e Event z ktoreho spracovavame vstup
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }    

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    public static CharInfo getCharacterMenu() {
        return (CharInfo)menuList.get(CHARINFOID);
    }
    
    // </editor-fold>
}
