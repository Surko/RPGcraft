/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import rpgcraft.entities.Entity;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.GameMenu;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.ImageUtils;
import rpgcraft.utils.Pair;
import rpgcraft.utils.TextUtils;

/**
 * EndMenu je trieda ktora sa stara o vytvorenie a zobrazenie menu pri stlaceni esc alebo
 * smrti playera. Menu nie je naviazane na ziadnu entitu
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu. 
 */
public final class EndMenu extends AbstractInMenu<EndMenu>{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Testovaci text pre urcenie fontov
     */
    private static final String TEST = "Testing";
    /**
     * ID tohoto menu
     */
    private static final String ENDID = "_endid";  
    
    /**
     * Mena pre tlacidla v menu
     */
    private static final String[] btnNames = new String[] {
        StringResource.getResource("backToGame"),
        StringResource.getResource("saveAndExit"),
        StringResource.getResource("exit")
    };
    
    /**
     * Sirka a vyska menu tlacidiel
     */
    private static final int btnWidth = 200, btnHeight = 50;
    /**
     * Odsadenie menu od okraju
     */
    private static final int wGap = 5, hGap = 5;
    /**
     * Sirka a vyska tohoto menu
     */
    private static final int endWidth = 500, endHeight = 400;
    
    /**
     * Aktualna sirka a vyska menu
     */
    protected int width = endWidth, height = endHeight;
    /**
     * Bonus k sirke a vyske
     */
    protected int wBonus = 0, hBonus = 0;
    /**
     * Font textov
     */
    protected Font font,boldTitleFont,italicFont, boldNormalFont;
    /**
     * Sirky a vysky textu pre rozny  font
     */
    private int[] txtSize, boldSize, italicSize, boldNormalSize;
    
    /**
     * Obrazok s menu
     */
    private BufferedImage endImage;
    /**
     * Obrazky tlacidiel v poli
     */
    private BufferedImage[] endBtnImages;    
    // </editor-fold>
    
    /**
     * Default konstruktor pre vytvorenie instancie end panela. Dolezity
     * pri volani newInstance.
     */
    public EndMenu() {
        super(null, null);
    }
    
    /**
     * Konstruktor pre vytvorenie end panelu. End panel nie je tvoreny medzi ziadnymi
     * entitami. Volame metodu setGraphics na nastavenie toDraw obrazku. Nakonci pridame end panel
     * do menuListu.
     * @param entity Entita ktora vyvolala endMenu    
     * @param input Vstup podla ktoreho spracovavame
     * @param menu Menu v ktorom sme toto menu vytvorili.
     */
    public EndMenu(Entity entity, InputHandle input, AbstractMenu menu) {
        super(entity, input);               
        this.menu = menu;        
        setGraphics();
        this.changedState = true;        
        menuList.put(ENDID, this);                
    }
    
    /**
     * <i>{@inheritDoc} </i>
     * Metoda ktora inicializuje end panel z originalneho menu zadaneho parametrom
     * <b>origMenu</b>.
     * @param origMenu Originalne menu z ktoreho cerpame vlastnosti
     * @param e1 Nepouzita entita (null)
     * @param e2 Nepouzita entita (null)
     * @return Novo vytvoreny konverzacny panel.
     */
    @Override
    public EndMenu initialize(EndMenu origMenu, Entity e1, Entity e2) {
        this.input = origMenu.getInput();
        this.menu = origMenu.getMenu();
        this.toDraw = origMenu.toDraw; 
        this.endBtnImages = origMenu.endBtnImages;
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

    /**
     * <i>{@inheritDoc }<i>
     * <p>
     * Pozicie su nastavene na stred okna
     * </p>
     * 
     */
    @Override
    public void recalculatePositions() {
        this.xPos = (menu.getGamePanel().getWidth() - width)/2;
        this.yPos = (menu.getGamePanel().getHeight() - height)/2;
        changedState = true;
    }

    @Override
    public void update() {
        if (changedState) {
            wBonus = xPos + (getWidth() - btnWidth)/2;
            hBonus = yPos + (getHeight() - (btnHeight + hGap) * endBtnImages.length)/2;
            changedState = false;
        }
    }

    @Override
    public void paintMenu(Graphics g) {
        if (visible && !changedState) {
            g.drawImage(toDraw, xPos, yPos, null);                                    
            for (int i = 0; i < endBtnImages.length; i++) {
                g.drawImage(endBtnImages[i], wBonus, hBonus + (btnHeight + hGap)*i, null);
            }
        }
    }

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
     * Metoda vrati sirku menu pre end menu
     * @return Sirka end menu
     */
    @Override
    public int getWidth() {
        if (width <= 0) {
            return endWidth;
        }
        return width;
    }
    
    /**
     * Metoda vrati vysku menu pre end menu
     * @return Vyska end menu
     */
    @Override
    public int getHeight() {
        if (height <= 0) {
            return endHeight;
        }
        return height;
    }

    /**
     * Metoda ktora vrati meno tohoto menu.
     * @return Meno EndMenu
     */
    @Override
    public String getName() {
        return ENDID;
    }
    // </editor-fold>
    
    /**
     * <i>{@inheritDoc } </i>
     * <p>
     * 
     * </p>
     */
    @Override
    protected final void setGraphics() {
        toDraw = new BufferedImage(getWidth(), getHeight(), BufferedImage.TRANSLUCENT);
        Graphics g = toDraw.getGraphics();
       
        g.setColor(Colors.getColor(Colors.transInvBackColor));
        g.fillRoundRect(0, 0, getWidth() - getWGap(), getHeight() - getHGap(), getWGap(), getHGap());
        
        setFonts(); 
        if (entity.getHealth() <= 0) {            
            endBtnImages = new BufferedImage[1]; 
            endBtnImages[0] = new BufferedImage(btnWidth, btnHeight, BufferedImage.OPAQUE);
            paintButton(endBtnImages[0], btnNames[2]);
        } else {
            endBtnImages = new BufferedImage[2];
            endBtnImages[0] = new BufferedImage(btnWidth, btnHeight, BufferedImage.OPAQUE);
            endBtnImages[1] = new BufferedImage(btnWidth, btnHeight, BufferedImage.OPAQUE);
            paintButton(endBtnImages[0], btnNames[0]);
            paintButton(endBtnImages[1], btnNames[1]);
        }                     
    }

    /**
     * Metoda ktora vykresli do obrazku tlacidla zadaneho parametrom <b>image</b>
     * text <b>name</b>
     * @param image Obrazok tlacidla
     * @param name Text tlacidla
     */
    public void paintButton(BufferedImage image, String name) {
        Graphics g = image.getGraphics();
        g.setColor(Colors.getColor(Colors.invBackColor));
        g.fillRoundRect(0, 0, btnWidth - getWGap(), btnHeight - getHGap(), getWGap(), getHGap());

        g.setColor(Colors.getColor(Colors.invOnTopColor));
        g.fillRoundRect(getWGap(), getHGap(), btnWidth - getWGap(), btnHeight - getHGap(), getWGap(), getHGap());
        
        g.setColor(Color.BLACK);
        g.setFont(boldTitleFont);
        int txtWidth = TextUtils.getTextWidth(boldTitleFont, name);
        g.drawString(name, (btnWidth - txtWidth)/2, (btnHeight - boldSize[1])/2);
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
    
    /**
     * Metoda ktora spracovava vstup od uzivatela. Pri stlacenie ESC ukoncujeme menu.     
     */
    @Override
    public void inputHandling() {
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) {
            exit();
            menu.setInMenu(null);
        }
    }

    /**
     * Metoda ktora spracovava vstup z mysi. Jedine InMenu ktore pouziva aj mys.
     * Pri stlaceni na jednotlive tlacidla sa vykonaju prislusne operacie. Vacsinou sa jedna
     * o vypnutie hry s ulozenim ci navratu do hry.
     * @param e 
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        int sx = wBonus,ex = wBonus + btnWidth,sy,ey;
        for (int i = 0; i < endBtnImages.length; i++) {            
            sy = hBonus + (btnHeight + hGap) * i;
            ey = sy + btnHeight;
            
            if (e.getX() < ex && e.getX() >= sx && e.getY() < ey && e.getY() >= sy) {
                if (endBtnImages.length == 1) {
                    if (menu instanceof GameMenu) {
                        GameMenu gameMenu = (GameMenu)menu;
                        exit();
                        gameMenu.setInMenu(null);
                        gameMenu.resetSave();
                        gameMenu.setMenu(AbstractMenu.getMenuByName("mainMenu"));
                    }
                }
                
                switch (i) {
                    case 0 : {
                       exit();
                       menu.setInMenu(null);
                       return; 
                    }
                    case 1 : {
                        if (menu instanceof GameMenu) {
                            GameMenu gameMenu = (GameMenu)menu;
                            exit();
                            gameMenu.setInMenu(null);
                            gameMenu.getSave().saveAndQuit(ImageUtils.makeThumbnailImage(gameMenu.getGameImage()));
                            gameMenu.resetSave();
                            gameMenu.setMenu(AbstractMenu.getMenuByName("mainMenu"));
                        }
                        /*
                        save.saveAndQuit(ImageUtils.makeThumbnailImage(screenImage));            
                        setMenu(AbstractMenu.getMenuByName("mainMenu"));
                        save = null;
                        saveMap = null;
                        * */
                        return;
                    }
                    default : return;
                }
            }
        }
    }
    
    /**
     * Metoda ktora vrati end menu z listu menuList.
     * @return Instancia endMenu
     */
    public static EndMenu getEndMenu() {
        return (EndMenu)menuList.get(ENDID);
    }
    
    
}
