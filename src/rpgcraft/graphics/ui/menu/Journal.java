/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import rpgcraft.plugins.AbstractInMenu;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Player;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.quests.Quest;

/**
 *
 * @author kirrie
 */
public class Journal extends AbstractInMenu {    
    public static final String JOURNAL = "journal";
    
    private static final int jrnWidth = 200, jrnHeight = 400, wGap = 5, hGap = 5;
    private static final int questsWidth = jrnWidth - 40, questsHeight = jrnHeight - 40;
    private static final int questHeight = 40;
    private static final int questsToShow = questsHeight / questHeight;
    
    private Image questBox;
    /**
     * Oznaceny quest v menu
     */
    private int selection = 0;
    /**
     * x, y pozicie, kde sa bude vykreslovat QuestInfo.
     */
    private int selectPosX, selectPosY;
    
    private static Journal instance;
    
    public Journal(Entity entity, InputHandle input, AbstractMenu menu) {
        super(entity, input);
        this.menu = menu;
        this.activated = true;
        setGraphics();
        recalculatePositions();
        this.changedState = true;
        menuList.put(JOURNAL, this);
    }        
    
    /**
     * Metoda ktora vrati Journal menu. Podobne ako v rodicovskom AbstractInMenu ziskavame toto menu
     * z menuList. V tomto pripade ale priamo vratime menu typu Journal.
     * @return Journal menu
     */
    public static Journal getJournalMenu() {
        return (Journal)menuList.get(JOURNAL);        
    }
    
    @Override
    public final void recalculatePositions() {
        this.xPos = wGap;
        this.yPos = hGap;
    }

    @Override
    protected final void setGraphics() {
        toDraw = new BufferedImage(getWidth(), getHeight(), BufferedImage.TRANSLUCENT);
        Graphics g = toDraw.getGraphics();

        g.setColor(Colors.getColor(Colors.invBackColor));
        g.fillRoundRect(0, 0, getWidth() - wGap, getHeight() - hGap, wGap, hGap);

        g.setColor(Colors.getColor(Colors.invOnTopColor));
        g.fillRoundRect(wGap, hGap, getWidth() - wGap, getHeight() - hGap, wGap, hGap);
    }

    @Override
    public void update() {
        if (subMenu != null && subMenu.isVisible()) {
            subMenu.update();
        }
        
        if (changedState) {
           if (updateImage()) {
               changedState = false;
           }
        }
    }
    
    private boolean updateImage() {                
        
        questBox = new BufferedImage(questsWidth, questHeight, BufferedImage.TRANSLUCENT);
        Graphics g = questBox.getGraphics();
        
        if (entity instanceof Player) {                    
            ArrayList<Quest> quests = ((Player)entity).getActiveQuests();
            

            if (!quests.isEmpty()) {
                ArrayList<Quest> questListToShow;
                int localSelect = 0;

                if (quests.size() > questsToShow) {
                    // sItem = kolko predmetov je za oznacenym predmetom.
                    int sItem = quests.size() - selection - 1;

                    questListToShow = new ArrayList<>();

                    if (sItem < questsToShow) {
                        localSelect = questsToShow - sItem - 1;
                        int diff = quests.size() - questsToShow;
                        for (int i = diff < 0 ? 0 : diff; i < quests.size(); i++) {
                            questListToShow.add(quests.get(i));
                        }
                    } else {
                        for (int i = selection; i < selection + questsToShow; i++) {
                            questListToShow.add(quests.get(i));
                        }
                    }
                } else {
                    questListToShow = quests;
                    localSelect = selection;
                }

                for (int i = 0; i < questListToShow.size(); i++) {
                    Quest quest = questListToShow.get(i);                    
                    g.drawString(quest.getName(), wGap, (i + 1) * questHeight - questHeight / 2);                    

                }

                g.setColor(Colors.getColor(Colors.selectedColor));
                selectPosY = localSelect * questHeight + yPos + questHeight;
                selectPosX = xPos + 120;
                g.fillRoundRect(0, localSelect * 40, questsWidth, questHeight, wGap, hGap);
                return true;
            }
        }
        g.drawString("No Items", 0, 40);
        
        return true;
    }
    
    @Override
    public void paintMenu(Graphics g) {
        if (visible) {
            g.drawImage(toDraw, xPos, yPos, null);
            
            g.drawImage(questBox, xPos + 20, yPos + 20, null);

            if (!activated) {
                g.setColor(Colors.getColor(Colors.selectedColor));
                g.fillRoundRect(xPos, yPos, getWidth() - wGap, getHeight() - hGap, wGap, hGap);
            }
        }
        // Testovanie na submenu. Kedze moze prekryvat toto menu tak je v metode ako posledne.
        if (subMenu != null) {
            subMenu.paintMenu(g);
        }
    }

    /**
     * <i>{@inheritDoc }</i>
     * @return {@inheritDoc }
     */
    @Override
    public String getName() {
        return JOURNAL;
    }
    
    @Override
    public int getWidth() {
        if (w <= 0) {
            return jrnWidth;
        }
        return w;
    }

    @Override
    public int getHeight() {
        if (h <= 0) {
            return jrnHeight;
        }
        return h;
    }

    @Override
    public void exit() {
        if (subMenu != null) {
            subMenu.exit();
        }
        
        this.visible = false;
        this.activated = false;
    }
    
    @Override
    public void inputHandling() {
        if (input.clickedKeys.contains(InputHandle.escape.getKeyCode()) || input.clickedKeys.contains(input.quest.getKeyCode())) {       
            exit();
            changedState = true;
            return;
        }
    }
    
    /**
     * Metoda ktora ma spracovavat eventy/udalosti z mysi. 
     * @param e MouseEvent z mysi
     * @see MouseEvent
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
}
