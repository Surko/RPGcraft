/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import rpgcraft.entities.Entity;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class CharInfo extends AbstractInMenu {
    private static final String CHARINFOID = "_charinfo";
    private static final String CHARINFO = StringResource.getResource(CHARINFOID); 
    private static final int wGap = 5, hGap = 5;    
    private static final int infoWidth = 200, infoHeight = 400;
    
    private int width = -1, height = -1;
    
    public CharInfo() {
        super(null, null);
    }
    
    public CharInfo(Entity e, InputHandle input, AbstractMenu menu) {
        super(e, input); 
        this.menu = menu;
        
        menuList.put(CHARINFOID, this);
    }
    
    /**
     * Metoda ktora initializuje toto menu s novymi udajmi. 
     * Novymi udajmi je entita zadana parametrom pre ktoru vytvarame menu.
     * Parameter origMenu sluzi ako vzor pre toto menu z ktoreho ziskavame pozadie tohoto menu.
     * @return Initializovane CharInfo.
     */
    @Override
    public CharInfo initialize(AbstractInMenu origMenu, Entity e) { 
        this.entity = e;
        this.input = origMenu.getInput();
        this.menu = origMenu.getMenu();
        this.changedState = true;
        this.toDraw = origMenu.getDrawImage();        
        return this;
    }
    
    @Override
    public void recalculatePositions() {
        this.xPos = wGap;
        this.yPos = hGap;
    }

    /**
     * {@inheritDoc }
     * Takisto ako pri inych menu vykreslujeme navrch tohoto menu informaciu v akom
     * menu sa nachadzame.
     */
    @Override
    protected void setGraphics() {
        super.setGraphics();
        Graphics g = toDraw.getGraphics();
        g.setColor(Color.BLACK);
        int[] txtSize = TextUtils.getTextSize(TextUtils.DEFAULT_FONT, CHARINFO);
        g.drawString(CHARINFO, (getWidth() - txtSize[0])/2, txtSize[1] + hGap);
    }

    @Override
    public void update() {
        
    }

    @Override
    public void paintMenu(Graphics g) {
        
    }

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

    @Override
    public String getName() {
        return CHARINFOID;
    }

    @Override
    public void inputHandling() {
        
    }

    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
    
    public static CharInfo getCharacterMenu() {
        return (CharInfo)menuList.get(CHARINFOID);
    }
}
