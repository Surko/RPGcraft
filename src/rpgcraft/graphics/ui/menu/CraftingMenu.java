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
import rpgcraft.entities.Item;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.resource.RecipeResource;
import rpgcraft.resource.RecipeResource.Type;

/**
 *
 * @author kirrie
 */
public class CraftingMenu extends AbstractInMenu {
    
    private static final Type[] types = new Type[] {Type.SHAPED, Type.SHAPELESS};
    private static final int cftWidth = 200, cftHeight = 200, wGap = 5, hGap = 5;
    private static final int lineThickness = 1;
    private static final int gridGap = 1;
    private static final int gridLength = 34;
    
    private int width = -1, height = -1;
    private int gridx, gridy;
    private Image cftBox;
    private int cftBoxX, cftBoxY;
    private Item[][] cells;
    private int cellFill,cftItemIndex;
    private ArrayList<Item> craftedItems;

    public CraftingMenu(AbstractInMenu source, int gridx, int gridy) {
        super(source.getEntity(), source.getInput());
        this.sourceMenu = source;
        this.cellFill = 0;
        this.cftItemIndex = 0;
        this.craftedItems = null;
        this.gridx = gridx;
        this.gridy = gridy;
        this.cells = new Item[gridy][gridx];
        setGraphics();
        updateImage();
        
    }
    
    
    
    @Override
    public void recalculatePositions() {
        this.xPos = wGap;
        this.yPos = hGap;
    }

    @Override
    protected void setGraphics() {
        toDraw = new BufferedImage(getWidth(), getHeight(), BufferedImage.TRANSLUCENT);
        Graphics g = toDraw.getGraphics();

        g.setColor(Colors.getColor(Colors.invBackColor));
        g.fillRoundRect(0, 0, getWidth() - wGap, getHeight() - hGap, wGap, hGap);

        g.setColor(Colors.getColor(Colors.invOnTopColor));
        g.fillRoundRect(wGap, hGap, getWidth() - wGap, getHeight() - hGap, wGap, hGap);
    }

    protected void updateImage() {
        cftBox = new BufferedImage(gridLength * gridx + 5*lineThickness, gridLength * gridy + 4*lineThickness + gridLength, 
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
        }
    }

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
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
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
    
    // </editor-fold>
    
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

    public boolean addItemToCell(Item item, int i, int j) {
        if (i >= gridy || j >= gridx || cells[i][j] != null) {
            return false;
        }
        cells[i][j] = item;
        cellFill++;
        return changedState = true;
    }
    
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
    
    /**
     * Metoda by mala vratit meno tohoto menu, ale kedze itemMenu moze byt len subMenu tak mu nepriradujem ziadne 
     * meno aby som donutil ukazanie tohoto menu len s doprovodom hlavneho menu.
     * @return null
     */
    @Override
    public String getName() {
        return null;
    }
    
    
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
     * Metoda ktora spracovava vstup pre craftingMenu. Mozu nastat tieto situacie :
     * - Ked je stlacene prave tlacidlo na klavesnici tak sa aktivuje source menu tohoto menu.
     * - Ked je stlacene enter v tomto menu tak sa vytvori vytvarany predmet a pripise sa do inventaru.
     */
    @Override
    public void inputHandling() {        
        if (input.clickedKeys.contains(InputHandle.right.getKeyCode())) {               
            if (sourceMenu != null) {                
                activated = false; 
                sourceMenu.activate();
                return;
            }            
        } 
        
        if (input.clickedKeys.contains(InputHandle.enter.getKeyCode())) {
            // Vytvori predmet, odoberie predmety z inventara a prida vyrobeny
            
            if (craftedItems != null) {                
                craftItem();
                sourceMenu.setState(true);
            }
            
        }
        
        if (input.clickedKeys.contains(InputHandle.escape.getKeyCode())) {
            exit();            
            if (sourceMenu != null) {
                sourceMenu.activate();
            }
        }
        
    }
    
    /**
     * Metoda ktora ma spracovavat eventy/udalosti z mysi. CraftingMenu take moznosti nepodporuje ale je ich mozne pridat.
     * @param e MouseEvent z mysi
     * @see MouseEvent
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
}
