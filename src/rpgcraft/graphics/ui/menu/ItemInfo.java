package rpgcraft.graphics.ui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.resource.StatResource.Stat;
import rpgcraft.utils.Pair;
import rpgcraft.utils.TextUtils;

/**
 * ItemInfo je trieda ktora sa stara o vytvorenie a zobrazenie infa o predmete
 * ktorym inicializujeme toto menu.
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu.
 * @author kirrie
 */
public class ItemInfo extends AbstractInMenu {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // Odsadenie od okrajov menu
    private static final int wGap = 5, hGap = 5;
    // Textove hodnoty vyuzite v menu
    private static final String TEST = "Testing";
    private static final String DUR = "Durability";
    private static final String DELIM = " : ";
    private static final String STATDELIM = " / ";
    // Hlavne staty ktore sa budu vykreslovat do menu. Viac v StatResource.
    private Stat[] mainStats = new Stat[] {        
        Stat.STRENGTH,        
        Stat.AGILITY,        
        Stat.SPEED,        
        Stat.ENDURANCE,
        Stat.STRENGTHPER,
        Stat.AGILITYPER,
        Stat.SPEEDPER,
        Stat.ENDURANCEPER,
    };
    
    // Health staty ktore sa budu vykreslovat do menu
    private Stat[] healthStats = new Stat[] {        
        Stat.HEALTHBONUS,        
        Stat.HEALTHMAXPER,        
        Stat.HEALTHREGEN,        
        Stat.HEALTHREGENPER,
        Stat.STAMINABONUS,
        Stat.STAMINAMAXPER,
        Stat.STAMINAREGEN,
        Stat.STAMINAREGENPER
    };
    
    // Utocne staty ktore sa budu vykreslovat do menu
    private Stat[] atkStats = new Stat[] { 
        Stat.ATKRADIUS,
        Stat.ATKRADIUSPER,
        Stat.ATKRATING,        
        Stat.ATKRATINGBONUS,        
        Stat.ATKRATINGPER,        
        Stat.DEFRATING,
        Stat.DEFRATINGBONUS,
        Stat.DEFRATINGPER,
        Stat.DMGBONUS,
        Stat.DAMAGEPER       
    };
    
    // Fonty pouzite v menu
    private Font font,boldTitleFont,italicFont, boldNormalFont;
    // Vysky pre kazdy font. Aby sme predchadzali pri kazdom vykreslovani textu preinicializovaniu
    private int[] txtSize, boldSize, italicSize, boldNormalSize;
    // Vyska a sirka pre menu
    private int width = 300, height = 400;
    // Predmet pre ktory vytvarame menu
    private Item item;
    
    // Modifikator ako velky je cely obrazok v ktorom su vykreslene staty. (modifCount * height)
    private int modifCount = 1;
    // Startovacia pozicia v obrazku odkial kreslime a lokalna startovacia pozicia alebo tiez maximalna vyska ktoru sme dosiahli pri kresleni statov do obrazku.
    private int itemInfoStartY = 0, starty;    
    // Plny obrazok so vsetkymi statmi.
    private BufferedImage fullItemInfoImage;
    // Skresany obrazok aby vyhovoval dlzke a vyske
    private BufferedImage itemInfoImage;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor pre item info ktore vytvori informacny panel o predmete pre predmet zadany parametrom <b>item</b>.    
     * AbstractInMenu ako parameter <b>source</b> sluzi ako zdrojove menu v ktorom bolo toto menu vytvorene.
     * Preinicializuje sa graphics metodou setGraphics
     * a nastavime changedState na true aby sa prekreslilo cele menu.     
     * @param source Source Menu z ktoreho bolo toto subMenu vytvorene.
     * @param item Predmet pre ktory sme vytvorili menu
     */
    public ItemInfo(AbstractInMenu source, Item item) {
        super(source.getEntity(), source.getInput());
        this.sourceMenu = source;
        this.item = item;                
        this.xPos = sourceMenu.getXPos() - width;
        this.yPos = sourceMenu.getYPos();
        this.changedState = true;
        setGraphics();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie + Update ">
    
    /**
     * Metoda ktora rekalkuluje pozicie menu v hracom paneli. Nastavene su 
     * aby bola vedla rodicovskeho menu.
     */
    @Override
    public void recalculatePositions() {
        this.yPos = sourceMenu.getYPos();
        this.xPos = sourceMenu.getXPos() - getWidth();
    }

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
     * Metoda ktora initializuje toto menu s novymi udajmi.
     * Kedze je toto menu subMenu a kazdu instanciu vytvarame z konstruktoru
     * tak je metoda dosti nevyuzitelna. Takze ked hu niekto vola tak mu pravdepodobne 
     * nebude robit co chce. Tak ci tak kopirujeme toDraw obrazok z originalneho 
     * menu kedze je spolocne pre vsetky ItemInfo menu.
     */
    @Override
    public AbstractInMenu initialize(AbstractInMenu origMenu, Entity e1, Entity e2) {
        this.toDraw = origMenu.getDrawImage();        
        this.changedState = true;
        return this;
    }

    /**
     * Metoda ktora aktualizuje staty o predmete a vypise ich do fullItemInfoImage.
     * Volana je len pri zmene stavu => vzdy na zaciatku a nasledne ked nieco zmeni stav.
     * Vykreslujeme informacie o predmete ako meno, zivot/durability a
     * postupne vykreslujeme staty ktore sme si definovali v premennych (mainStats,healthStats,...)
     * Po kazdom vykresleni statu zvysujeme starty poziciu.
     */
    private void updateImage() {
        fullItemInfoImage = new BufferedImage(getWidth() - wGap,
                (modifCount) * getHeight() - 2 * hGap, 
                BufferedImage.TRANSLUCENT);
        int w = fullItemInfoImage.getWidth(null);
        Graphics g = fullItemInfoImage.getGraphics();
        starty = 0;
        int textWidth;
        
        ArrayList<Pair<String,Integer>> parsedTexts;        
        g.setColor(Color.BLACK); 
        g.setFont(boldTitleFont);
                            
        // Vykreslenie nazvu predmety
        String itemText = item.getName();                
        textWidth = TextUtils.getTextWidth(boldTitleFont, itemText);
        starty += hGap + boldSize[1];
        fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);
        g = setNewGraphics(fullItemInfoImage, boldTitleFont);        
        g.drawString(itemText, (getWidth() - wGap - textWidth) / 2 , starty); 
        
        g.setFont(italicFont);
        itemText = item.getItemType().toString();                
        textWidth = TextUtils.getTextWidth(italicFont, itemText);
        starty += italicSize[1];
        fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);
        g = setNewGraphics(fullItemInfoImage, italicFont);        
        g.drawString(itemText, (getWidth() - wGap - textWidth) / 2 , starty);
        
        // Vykreslenie obrazku predmetu
        Image itemImg = item.getTypeImage(); 
        if (itemImg != null) {  
            g.drawImage(itemImg, (getWidth() - wGap - itemImg.getWidth(null)) / 2 , starty, null);
            starty += 2 * hGap + itemImg.getHeight(null); 
            fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);
            g = setNewGraphics(fullItemInfoImage, italicFont);                       
        }
                
        // Vykreslenie hlavneho textu predmetu
        itemText = item.getInfo();                
        // Rozparsovanie textu aby sa zmestil do infa.        
        parsedTexts = TextUtils.parseToSize(w - 2 *wGap, itemText, font);
        starty += 2 * hGap + txtSize[1];
        fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);
        g = setNewGraphics(fullItemInfoImage, italicFont);
        
        for (Pair<String,Integer> pair : parsedTexts) {
            g.drawString(pair.getFirst(), wGap , starty);
            starty += italicSize[1];
            fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);
            g = setNewGraphics(fullItemInfoImage, boldNormalFont);
        }
        
        g.setFont(boldNormalFont);
        // Odsadenie od textu a statov
        starty += 2 * hGap;                   
        g.drawString(DUR + DELIM + item.getHealth() + STATDELIM + 
                item.getStatValue(Stat.HEALTHMAX), wGap , starty);
        fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);
        g = setNewGraphics(fullItemInfoImage, boldNormalFont);
        // Odsadenie durability od statov
        starty += 2 * hGap + boldNormalSize[1];
        
        // Staty o zivote :D
        for (Stat stat : healthStats) {
            g.drawString(stat.getName() + DELIM + item.getStatValue(stat), wGap , starty);
            starty += boldNormalSize[1];
            fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);
            g = setNewGraphics(fullItemInfoImage, boldNormalFont);
        }
        
        // Odsadenie dvoch rozlicnych statov 
        starty += 2 * hGap;
        
        //
        for (Stat stat : mainStats) {
            g.drawString(stat.getName() + DELIM + item.getStatValue(stat), wGap , starty);
            starty += boldNormalSize[1];
            fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);            
            g = setNewGraphics(fullItemInfoImage, boldNormalFont);
        }
        
        // Odsadenie dvoch rozlicnych statov 
        starty += 2 * hGap;
        
        for (Stat stat : atkStats) {
            g.drawString(stat.getName() + DELIM + item.getStatValue(stat), wGap , starty);
            starty += boldNormalSize[1];
            fullItemInfoImage = controlImageHeight(fullItemInfoImage, starty);  
            g = setNewGraphics(fullItemInfoImage, boldNormalFont);
        }
        
        itemInfoImage = fullItemInfoImage.getSubimage(0, 0, getWidth() - wGap, getHeight() - 2 * hGap);
        
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
     * <i>{@inheritDoc } </i>
     * <p>
     * V metode navyse Nastavujeme fonty a vysky textov.
     * </p>
     */
    @Override
    public void setGraphics() {
        super.setGraphics();
        font = TextUtils.DEFAULT_FONT;        
        boldTitleFont = TextUtils.DEFAULT_FONT.deriveFont(Font.BOLD, 15f);        
        boldNormalFont = TextUtils.DEFAULT_FONT.deriveFont(Font.BOLD, 13f);
        italicFont = TextUtils.DEFAULT_FONT.deriveFont(Font.ITALIC, 13f); 
        txtSize = TextUtils.getTextSize(font, TEST);
        italicSize = TextUtils.getTextSize(italicFont, TEST);
        boldSize = TextUtils.getTextSize(boldTitleFont, TEST);
        boldNormalSize = TextUtils.getTextSize(boldNormalFont, TEST);
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
       
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    /**
     * Metoda ktora vykresli info o predmete. Kedze cele menu sa sklada z dvoch nabufferovanych
     * obrazkov toDraw a itemInfoImage tak nam staci iba vykreslit tieto dva obrazky.
     * Pri neaktivovanom menu prekryjeme menu tmavsou farbou
     * @param g Graficky kontext kde vykreslujeme menu
     */
    @Override
    public void paintMenu(Graphics g) {
        if (visible) {
            g.drawImage(toDraw, xPos, yPos, null);        
            g.drawImage(itemInfoImage, xPos + wGap, yPos + hGap, null);                        
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
     * Metoda vrati sirku menu pre item menu
     * @return Sirka inventara
     */
    @Override
    public int getWidth() {
        return width;
    }
    
    /**
     * Metoda vrati vysku menu pre item menu
     * @return Vyska inventara
     */
    @Override
    public int getHeight() {
        return height;
    }

    /**
     * Metoda ktora vrati meno tohoto menu
     * @return Meno pre ItemInfo
     */
    @Override
    public String getName() {
        return null;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handling ">
    /**
     * Metoda ktora sa stara o spracovanie uzivatelskeho vstupu. Kedze ItemInfo menu
     * je nenarocne na vstup tak obsahuje iba 3 kontroly vstupu : <p>
     * - Kontrola tlacidla Up => Kladne posunutie startovacej y-pozicie vykreslovania. <br>
     * - Konstrola tlacidla Down => Zaporne posunutie startovacej y-pozicie vykreslovania. <br>
     * - Kontrola tlacidla escape => Zrusenie tohoto menu a vratenie do sourcemenu </p>
     * 
     */
    @Override
    public void inputHandling() {         
        if (input.runningKeys.contains(InputHandle.DefinedKey.DOWN.getKeyCode())) {            
            if (getHeight() > starty || itemInfoStartY >= starty) {
                return;
            }                
            if (itemInfoStartY + 10 >= starty - 1) {
                itemInfoStartY = starty - 1;                
            } else {
                itemInfoStartY+= 10;                        
            } 
            
            try {
                itemInfoImage = fullItemInfoImage.getSubimage(0, itemInfoStartY,
                        getWidth() - wGap, getHeight() - 2 * hGap);
            } catch (Exception e) {
                itemInfoImage = null;
            }
        }
        
        if (input.runningKeys.contains(InputHandle.DefinedKey.UP.getKeyCode())) {  
            if (itemInfoStartY <= 0) {
                return;
            }
            if (itemInfoStartY - 10 < 0) {
                itemInfoStartY = 0;                
            } else {
                itemInfoStartY-= 10;                
            }
            
            try {
                itemInfoImage = fullItemInfoImage.getSubimage(0, itemInfoStartY,
                        getWidth() - wGap, getHeight() - 2 * hGap);
            } catch (Exception e) {
                itemInfoImage = null;
            }
        }
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) {
            exit();
            sourceMenu.activate();
        }
    }

    /**
     * <i> {@inheritDoc }</i>
     * @param e MouseEvent podla urcujeme co spravime.
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
    // </editor-fold>
    
}
