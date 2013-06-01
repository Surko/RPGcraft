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
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.quests.Quest;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class QuestInfo extends AbstractInMenu {
    private static final int wGap = 5, hGap = 5;   
    
    private int width = 300;
    private int height = 300;
    
    private Quest quest;
    
    public QuestInfo() {
        super(null, null);
    }
    
    public QuestInfo(AbstractInMenu source,Quest quest, int x, int y) {
        super(source.getEntity(), source.getInput());
        this.sourceMenu = source;
        this.quest = quest;
        this.xPos = x;
        this.yPos = y;
        setGraphics();        
        this.changedState = true;
    }
    
    /**
     * Metoda ktora by mala initializovat menu s novymi udajmi
     * ale pre subtypy tato metoda straca zmysel kedze kazde nadtyp menu si vytvara
     * vzdy nove instancie.
     */
    @Override
    public QuestInfo initialize(AbstractInMenu origMenu, Entity e1, Entity e2) {        
        this.toDraw = origMenu.getDrawImage();               
        return this;
    }
    
    @Override
    public void recalculatePositions() {
        
    }

    @Override
    protected void setGraphics() {
        super.setGraphics();
        Graphics g = toDraw.getGraphics();
        g.setColor(Color.BLACK);
        
        setQuestInfo(g);
    }
    
    /**
     * Metoda ktora nastavi info o ulohe do grafickeho kontextu zadaneho parametrom.
     * Metoda vyuziva kreslenie textu aby sa zmestil do tohoto menu => parsovanie textu
     * do viacerych riadkov pomocou TextUtils.
     * Kreslenie prebieha tak ze navrch vypise o aky quest sa jedna (questLabel),
     * potom vypisuje o queste zakladne info (questText) a nakonci vypise do kontextu
     * text o konkretnejsom stave quest tzv. stateofQuest.
     * @param g Graficky kontext do ktoreho kreslime.
     */
    private void setQuestInfo(Graphics g) {
        int starty = 0;
        int[] txtSize;
        ArrayList<String> parsedTexts;
        Font font = TextUtils.DEFAULT_FONT;
        g.setColor(Color.BLACK);      
        
        // Vykreslenie nazvu questu
        String questText = quest.getLabel();
        font = TextUtils.DEFAULT_FONT.deriveFont(Font.BOLD, 15f);
        txtSize = TextUtils.getTextSize(font, questText);     
        starty = hGap + txtSize[1];
        g.setFont(font);
        g.drawString(questText, (getWidth() - wGap - txtSize[0]) / 2 , starty); 
        
        // Vykreslenie hlavneho textu questu
        questText = quest.getText();
        font = TextUtils.DEFAULT_FONT.deriveFont(Font.ITALIC, 13f);
        txtSize = TextUtils.getTextSize(font, questText);
        // Rozparsovanie textu aby sa zmestil do infa.        
        parsedTexts = TextUtils.parseToSize(getWidth() - 2 *wGap, questText, font);
        starty += hGap + txtSize[1];        
        g.setFont(font);
        for (String s : parsedTexts) {
            g.drawString(s, wGap , starty);
            starty += txtSize[1];
        }
        
    }

    @Override
    public void update() {
        
    }

    @Override
    public void paintMenu(Graphics g) {
        if (visible) {
            g.drawImage(toDraw, xPos, yPos, null);        
                                    
            if (!activated) {
                g.setColor(Colors.getColor(Colors.selectedColor));
                g.fillRoundRect(xPos, yPos, getWidth(), getHeight(), wGap, hGap);
            }
        }
    }

    @Override
    public void exit() {
        if (subMenu != null) {
            subMenu.exit();
        }
        this.visible = false;
        this.activated = false;
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
    
    @Override
    public int getWidth() {
        return width;
    }

    @Override
    public int getHeight() {
        return height;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public void inputHandling() {
        if (input.clickedKeys.contains(InputHandle.escape.getKeyCode())) { 
            if (sourceMenu != null) {
                exit();
                sourceMenu.activate();                                
                return;
            }            
        }
    }

    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
}
