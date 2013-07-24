/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.resource.RecipeResource;
import rpgcraft.resource.RecipeResource.Type;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.TextUtils;

/**
 * CraftingMenu je trieda ktora sa stara o vytvorenie a zobrazenie crafting boxu v ktorom mozme
 * vytvarat predmet pomocou receptov.
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu. 
 */
public class CraftingMenu extends AbstractInMenu {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Meno tohoto menu
     */
    private static final String CRAFTING = StringResource.getResource("_crafting");
    /**
     * Typy receptov ake menu moze pouzit
     */
    private static final Type[] types = new Type[] {Type.SHAPED, Type.SHAPELESS};
    /**
     * Default sirky a vysky s odstupmi
     */
    private static final int cftWidth = 200, cftHeight = 200, wGap = 5, hGap = 5;
    /**
     * Hrubka ciary
     */
    private static final int lineThickness = 1;
    /**
     * Odstup medzi dvoma tabulkami
     */
    private static final int gridGap = 1;
    /**
     * Dlzka jednej tabulky
     */
    private static final int gridLength = 34;
    
    /**
     * Vyska a sirka tohoto menu. Na predefinovanie velkosti kedze toto menu pracuje s konstantami cftWidth a cftHeight.
     */
    private int width = -1, height = -1;
    /**
     * Velkost mriezky po x aj y-ovej suradnici.
     */
    private int gridx, gridy;
    /**
     * Crafting Box v ktorom sa nachadzaju predmety 
     */
    private Image cftBox;
    /**
     * X-ova a Y-ove pozicie craftingBoxu v paneli.
     */
    private int cftBoxX, cftBoxY;
    /**
     * Predmety v mriezke
     */
    private Item[][] cells;
    /**
     * Premenne s poctom vyplnenych predmetov v mriezky a aktualnym indexom vytvaraneho predmetu z listu craftedItems.
     */
    private int cellFill,cftItemIndex;
    /**
     * Vsetky predmety ktore su mozne vytvorit z predmetov nachadzajuce sa v mriezke/cells
     */
    private ArrayList<Item> craftedItems;

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Zakladny konstruktor dolezity pri volani newInstance metody pri vytvarani
     * kopii objektu.
     */
    public CraftingMenu() {
        super(null, null);
    }
    
    /**
     * Konstruktor tohoto submenu vytvara instanciu CraftingMenu. Ako je pri konstruktoroch subMenu
     * zvykom ma rodicovsky AbstractInMenu v ktorom bolo menu vytvorene. Dodatkovo obsahuje parametre gridx a gridy
     * ktore urcuju velkost mriezky do ktorej sa daju vkladat predmety.
     * Konstruktor vola metodu setGraphics ktora nastavi graficky kontext (obrazok toDraw) pre toto menu
     * a metodu updateImage ktora aktualizuje obrazok s obsahom predmetov v nom a mriezkou.
     * @param source Rodicovske AbstractInMenu v ktorom je toto menu
     * @param gridx X-ova velkost mriezky 
     * @param gridy Y-ova velkost mriezky
     */
    public CraftingMenu(AbstractInMenu source, int gridx, int gridy) {
        super(source.getEntity(), source.getInput());
        this.sourceMenu = source;
        this.cellFill = 0;
        this.cftItemIndex = 0;
        this.craftedItems = null;
        this.gridx = gridx;
        this.gridy = gridy;
        this.cells = new Item[gridy][gridx];
        this.changedState = true;
        setGraphics();               
    }
    
    /**
     * Metoda ktora initializuje toto menu s novymi udajmi. 
     * Novymi udajmi je entita zadana parametrom pre ktoru vytvarame menu.
     * Parameter origMenu sluzi ako vzor pre toto menu z ktoreho ziskavame pozadie tohoto menu.
     * @return Initializovane craftinMenu.
     */
    @Override
    public CraftingMenu initialize(AbstractInMenu origMenu, Entity e1, Entity e2) {               
        this.toDraw = origMenu.getDrawImage();        
        return this;
    }
    // </editor-fold>    
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Pozicia tohoto menu v hlavnom paneli. V tomto pripade trochu posunute
     * o dlzky wGap a hGap
     */
    @Override
    public void recalculatePositions() {
        this.xPos = wGap;
        this.yPos = hGap;
    }
    
    /**
     * Override metoda exit ma za ulohu spravne ukoncit toto menu s nastavenim 
     * viditelnosti a aktivovanosti na false a volanim metody getItemsBack na ziskanie predmetov
     * z mriezky naspat do inventara entity.
     */
    @Override
    public void exit() {
        if (subMenu != null) {
            subMenu.exit();
        }
        visible = false;
        activated = false;
        getItemsBack();
    }
    
    /**
     * Metoda ktora vyvola aktualizovanie tohoto menu. Ked sa zmenil stav tohoto menu
     * tak ziska recept z aktualnych predmetov (cells), z poctu predmetov v mriezke a moznych typov receptov (SHAPELESS, SHAPED,...)
     * a ked nejaky recept vyhovuje tak ziska z neho predmety do premennej <b>craftedItems</b>.
     * Nasledne zavola aktualizaciu tohoto menu a ukoncenie zmeny stavu.     
     */
    @Override
    public void update() {
        if (changedState) {
                        
            RecipeResource recipe = RecipeResource.getRecipe(cells, cellFill, types);
            if (recipe != null) {
                craftedItems = recipe.getCraftedItems();
            }
            
            updateImage();
            changedState = false;
        }
    }        
    
    /**
     * Metoda craftItem vytvori predmet nachadzajuci sa v poslednej mriezky/ v premennej craftedItems.
     * Kedze vytvorenych predmetov moze byt viac tak sa vytvori iba ten predmet ktory je akurat oznaceny (premenna
     * cftItemIndex). Po vytvoreni nastavi premenne tykajuce sa vytvarania predmetov na default.     
     */
    private void craftItem() {
        
        entity.addItem(craftedItems.get(cftItemIndex));
        
        for (int i = 0; i < gridy; i++) { 
            for (int j = 0; j < gridx; j++) {
                cells[i][j] = null;
            }
        }
        craftedItems = null;
        cftItemIndex = 0;
        cellFill = 0;
        changedState = true;
        
    }

    /**
     * Metoda ktora prida predmet dany parametrom <b>item</b> do mriezky/cells
     * na pozicie zadane dalsimi dvoma parametrami <b>i,j</b>
     * @param item Predmet na pridanie do mriezky
     * @param i y-ova pozicia predmetu v mriezke
     * @param j x-ova pozicia predmetu v mriezke
     * @return True/False ci sa zmenil stav.
     */
    public boolean addItemToCell(Item item, int i, int j) {
        if (i >= gridy || j >= gridx || cells[i][j] != null) {
            return false;
        }
        cells[i][j] = item;
        cellFill++;
        return changedState = true;
    }
    
    /**
     * Metoda ktora prida predmet dany parametrom <b>item</b> do mriezky/cells
     * na pozicie ktore si ziska z parametru <b>k</b> delenim a modulovanim s gridy.
     * @param item Predmet na pridanie do mriezky
     * @param k Pozicia kam sa pridava tento predmet
     * @return True/false ci sa zmenil stav.
     */
    public boolean addItemToCell(Item item, int k) {
        int i = k / gridy;
        int j = k % gridy;
        
        if (i >= gridy || j >= gridx || cells[i][j] != null) {
            return false;
        }
        
        cells[i][j] = item;
        cellFill++;
        return changedState = true;
    }        
    
    /**
     * Metoda ktora vrati vsetky predmety z crafting boxu naspat do inventaru. Pouzivane
     * pri neocakavanom zatvoreni menu a stale sa v boxe nachadzali itemy.
     */
    public void getItemsBack() {
        
        for (int i = 0; i < gridy; i++) {
            for (int j =0; j < gridx; j++) {
                if (cells[i][j] != null) {
                    entity.addItem(cells[i][j]);
                }
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
     * Metoda vrati sirku menu pre crafting menu
     * @return Sirka inventara
     */
    @Override
    public int getWidth() {
        if (width <= 0) {
            return cftWidth;
        }
        return width;
    }
    
    /**
     * Metoda vrati vysku menu pre crafting menu
     * @return Vyska inventara
     */
    @Override
    public int getHeight() {
        if (height <= 0) {
            return cftHeight;
        }
        return height;
    }
    
    /**
     * Metoda by mala vratit meno tohoto menu, ale kedze itemMenu moze byt len subMenu tak mu nepriradujem ziadne 
     * meno aby som donutil ukazanie tohoto menu len s doprovodom hlavneho menu.
     * @return null
     */
    @Override
    public String getName() {
        return null;
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    /**
     * Metoda ktora vykresluje toto menu do grafickeho kontextu. Vykreslenie prebieha iba ked je menu
     * viditelne. Pri viditelnosti menu vykresli obrazok toDraw a cftBox do kontextu. 
     * Dodatocne ked nie je menu aktivovane tak ho prekryje tmavsou farbou.     
     * @param g Graficky kontext kam sa menu vykresluje
     */
    @Override
    public void paintMenu(Graphics g) {    
        if (visible) {
            g.drawImage(toDraw, xPos, yPos, null);

            g.drawImage(cftBox, cftBoxX, cftBoxY, null);       
            
            if (!activated) {
                g.setColor(Colors.getColor(Colors.selectedColor));
                g.fillRoundRect(0, 0, getWidth() - wGap, getHeight() - hGap, wGap, hGap);
            }
        }
    }    
    
    /**
     * {@inheritDoc }
     * Na vrchu tohoto menu sa nachadza vypis co za menu mame otvorene.     
     */
    @Override
    protected void setGraphics() {
        super.setGraphics();
        Graphics g = toDraw.getGraphics();
        g.setColor(Color.BLACK);
        int[] cftSize = TextUtils.getTextSize(TextUtils.DEFAULT_FONT, CRAFTING);
        g.drawString(CRAFTING, (getWidth() - cftSize[0])/2, cftSize[1] + hGap + gridGap);
        
    }

    /**
     * Metoda updateImage ma za ulohu aktualizovat obrazok crafting okna s obrazkami predmetov 
     * ktore sa v nom nachadzaju. Nazaciatku si vytvori tento craftingBox v ktorom sa budu nachadzat vsetky predmety na vytvorenie
     * noveho predmetu, nastavi poziciu tohoto boxu a zavola metodu paintGridLines na vytvorenie
     * mriezky. Nasledne prechadzame premennou <b>cells</b> a vykreslujeme obrazky tychto predmetov
     * do vnutra mriezky. Nakonci vykreslime vytvoreny predmet do naspodnejsej mriezky a
     * vedla tejto mriezky sa nachadza text s poziciou vytvaraneho predmetu (jeden recept moze vytvorit viacero
     * predmetov a pocet tychto predmetov je vhodne vidiet).
     */
    protected void updateImage() {
        cftBox = new BufferedImage(gridLength * gridx + lineThickness,
                gridLength * gridy + gridLength + lineThickness, 
                BufferedImage.TRANSLUCENT);        
        cftBoxX = (getWidth() - cftBox.getWidth(null)) / 2;
        cftBoxY = (getHeight() - cftBox.getHeight(null)) / 2;

        Graphics2D g2d = (Graphics2D)cftBox.getGraphics();
        
        paintGridLines(g2d);
        
        for (int i = 0; i < gridy; i++) {
            for (int j = 0; j < gridx; j++) {             
                
                if (cells[i][j] != null) {
                    g2d.drawImage(cells[i][j].getTypeImage(), j * gridLength + lineThickness + gridGap , 
                            i * gridLength + lineThickness + gridGap, null);
                }
            }
        }
        
        if (craftedItems != null) {
            g2d.drawImage(craftedItems.get(cftItemIndex).getTypeImage(), (cftBox.getWidth(null) - gridLength)/2 + lineThickness + gridGap,
                    gridy * gridLength + lineThickness + gridGap, null);
            g2d.drawString((cftItemIndex + 1) + "/" + craftedItems.size(), (cftBox.getWidth(null) - gridLength)/2 + gridLength + lineThickness + gridGap , 
                    (gridy + 1)* gridLength);
        }
    }
    
    /**
     * Metoda vykresli mriezku do grafickeho kontextu zadaneho parametrom <b>g2d</b>. 
     * V mriezke sa budu nachadzat obrazky predmetov ktore su aktualne v tomto crafting menu.
     * V metode figuruju premenne :
     * <b>gridx</b> - pocet mriezok po x-ovej suradnici
     * <b>gridy</b> - pocet mriezok po y-ovej suradnici.
     * <b>gridLength</b> - dlzka jednej mriezky.     
     * Stavba mriezky ma vacsinou taku stavbu ze navrchu je gridx X gridy mriezka a hned
     * podnou sa nachadza jeden stvorec presne v prostriedku v ktorom sa bude nachadzat
     * vytvoreny predmet.
     * @param g2d Graficky kontext kam vykreslujeme mriezku.
     */
    private void paintGridLines(Graphics2D g2d) {
        g2d.setColor(Color.BLACK);
        g2d.setStroke(new BasicStroke(lineThickness));        
        
        g2d.drawRect(0, 0, gridx * gridLength, gridy * gridLength);
        g2d.drawRect((cftBox.getWidth(null) - gridLength)/2, gridy * gridLength, gridLength, gridLength);
        
        for (int i = 1; i < gridx; i++) {
            g2d.drawLine(i * gridLength, 0, i * gridLength, gridy * gridLength);
        }
        
        for (int i = 1; i < gridy; i++) {
            g2d.drawLine(0, i * gridLength, gridx * gridLength, i * gridLength);
        }
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Input + Mouse Handling ">        
    
    /**
     * Metoda ktora spracovava vstup pre craftingMenu. Mozu nastat tieto situacie :
     * - Ked je stlacene prave tlacidlo na klavesnici tak sa aktivuje source menu tohoto menu.
     * - Ked je stlacene enter v tomto menu tak sa vytvori vytvarany predmet a pripise sa do inventaru.
     */
    @Override
    public void inputHandling() {        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.RIGHT.getKeyCode())) {               
            if (sourceMenu != null) {                
                activated = false; 
                sourceMenu.activate();
                return;
            }            
        } 
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ENTER.getKeyCode())) {
            // Vytvori predmet, odoberie predmety z inventara a prida vyrobeny
            
            if (craftedItems != null) {                
                craftItem();
                sourceMenu.setState(true);
            }
            
        }

        if (craftedItems != null && craftedItems.size() > 0) {
            if (input.clickedKeys.contains(InputHandle.DefinedKey.UP.getKeyCode())) {
                if (cftItemIndex <= 0) {
                    cftItemIndex = craftedItems.size() - 1;
                } else {
                    cftItemIndex--;
                }
                updateImage();
            }

            if (input.clickedKeys.contains(InputHandle.DefinedKey.DOWN.getKeyCode())) {
                if (cftItemIndex >= craftedItems.size() - 1) {
                    cftItemIndex = 0;
                } else {
                    cftItemIndex++;
                }  
                updateImage();
            }
        }
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) {
            exit();            
            if (sourceMenu != null) {
                sourceMenu.activate();
            }
        }
        
    }
    
    /**
     * <i>{@inheritDoc }</i>
     * @see MouseEvent {@inheritDoc }
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
    // </editor-fold>
}
