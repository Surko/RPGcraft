/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Color;
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
import rpgcraft.resource.StringResource;
import rpgcraft.utils.TextUtils;

/**
 *
 * @author kirrie
 */
public class Journal extends AbstractInMenu {    
    public static final String JOURNALID = "_journal";
    private static final String JOURNAL = StringResource.getResource(JOURNALID);
        
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
    private ArrayList<Quest> quests;
    private static Journal instance;
    
    public Journal() {
        super(null, null);
    }
    
    public Journal(Entity entity, InputHandle input, AbstractMenu menu) {
        super(entity, input);
        this.menu = menu;
        this.activated = true;
        this.visible = true;
        setGraphics();
        recalculatePositions();
        this.changedState = true;
        menuList.put(JOURNALID, this);
    } 
    
    /**
     * Metoda ktora initializuje toto menu s novymi udajmi. 
     * Novymi udajmi je entita zadana parametrom pre ktoru vytvarame menu.
     * Parameter origMenu sluzi ako vzor pre toto menu z ktoreho ziskavame pozadie tohoto menu.
     * @return Initializovany journal.
     */
    @Override
    public Journal initialize(AbstractInMenu origMenu, Entity e1, Entity e2) {
        this.entity = e1;
        this.input = origMenu.getInput();
        this.menu = origMenu.getMenu();
        this.changedState = true;
        this.toDraw = origMenu.getDrawImage();
        return this;
    }
    
    /**
     * Metoda ktora vrati Journal menu. Podobne ako v rodicovskom AbstractInMenu ziskavame toto menu
     * z menuList. V tomto pripade ale priamo vratime menu typu Journal.
     * @return Journal menu
     */
    public static Journal getJournalMenu() {
        return (Journal)menuList.get(JOURNALID);        
    }
    
    @Override
    public final void recalculatePositions() {
        this.xPos = wGap;
        this.yPos = hGap;
    }   
        
    @Override
    protected final void setGraphics() {
        super.setGraphics();
        Graphics g = toDraw.getGraphics();
        g.setColor(Color.BLACK);
        int[] txtSize = TextUtils.getTextSize(TextUtils.DEFAULT_FONT, JOURNAL);
        g.drawString(JOURNAL, (getWidth() - txtSize[0])/2, txtSize[1] + hGap);
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
            quests = ((Player)entity).getActiveQuests();
            

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
                    g.drawString(quest.getLabel(), wGap, (i + 1) * questHeight - questHeight / 2);                    

                }

                g.setColor(Colors.getColor(Colors.selectedColor));
                selectPosY = localSelect * questHeight + yPos + questHeight;
                selectPosX = xPos + jrnWidth;
                g.fillRoundRect(0, localSelect * questHeight, questsWidth, questHeight, wGap, hGap);
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
        return JOURNALID;
    }
    
    public Quest getSelectedQuest() {
        return quests.get(selection);
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
        subMenu = null;
        activated = false;
        visible = false;
        menu.setInMenu(null);  
        input.freeKeys();
    }
    
    @Override
    public void inputHandling() {
        if (subMenu != null) {
            
            if (subMenu.isActivated()) {
                subMenu.inputHandling();
                return;
            } 
        }
        
        if (input.clickedKeys.contains(InputHandle.escape.getKeyCode()) || input.clickedKeys.contains(input.quest.getKeyCode())) {       
            exit();
            changedState = true;
            return;
        }
        
        if (input.clickedKeys.contains(InputHandle.enter.getKeyCode())) {       
            if (subMenu != null) {
                safeSubMenuExit();
            }
            this.activated = false;
            subMenu = new QuestInfo(this, getSelectedQuest(), selectPosX, selectPosY);
            subMenu.setVisible(true);
            subMenu.activate();
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
