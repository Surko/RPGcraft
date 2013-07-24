/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.ui.menu;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.quests.Quest;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.Pair;
import rpgcraft.utils.TextUtils;

/**
 * QuestInfo je trieda ktora sa stara o vytvorenie a zobrazenie submenu quest infa.
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu.
 */
public class QuestInfo extends AbstractInMenu {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Predpona pri parsovani ulohy
     */
    private static final char LOCALPREFIX = '@';
    /**
     * Odsadenie ulohoveho okna
     */
    private static final int wGap = 5, hGap = 5;   
    
    /**
     * Sirka a vyska menu
     */
    private int width = 300,height = 300;
    
    /**
     * Uloha ktoru zobrazujeme
     */
    private Quest quest;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Privatny konstruktor pre vytvorenie infa.
     */
    public QuestInfo() {
        super(null, null);
    }
    
    /**
     * Konstruktor pre vytvorenie QuestInfa. Volame metodu setGraphics pre vytvoreni
     * obrazku toDraw.
     * @param source Zdrojove menu z ktoreho bolo vyvolane toto menu
     * @param quest Quest ktory zobrazujeme v infe
     * @param x X-ova pozicia infa
     * @param y Y-ova pozicia infa
     */
    public QuestInfo(AbstractInMenu source,Quest quest, int x, int y) {
        super(source.getEntity(), source.getInput());
        this.sourceMenu = source;
        this.quest = quest;
        this.xPos = x;
        this.yPos = y;
        setGraphics();        
        this.changedState = true;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update metody ">  
    /**
     * Metoda ktora rekalkuluje pozicie infa v paneli. Kedze je v lavom hornom rohu vedla
     * journal menu tak nemenime pozicie
     */
    @Override
    public void recalculatePositions() {
        
    }
    
    /**
     * Metoda ktora aktualizuje info. Kedze QuestInfo je nacitany jeden quest
     * a neda sa v tomto menu nic robit tak je metoda prazdna.
     */    
    @Override
    public void update() {
        
    }
    
    /**
     * Metoda ktora spravne ukonci menu. 
     */
    @Override
    public void exit() {
        if (subMenu != null) {
            subMenu.exit();
        }
        this.visible = false;
        this.activated = false;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * <i>{@inheritDoc }</i>
     * <p>
     * Metoda ktora nastavi/vykresli obrazok toDraw ktory je pre kazde menu rovnake (preto
     * volame metodu super.setGraphics()). Nakonci zavolame metodu setQuestInfo
     * ktora vykresli info o queste do grafickeho kontextu (obycajne vytvarame druhy obrazok)
     * </p>
     */
    @Override
    protected final void setGraphics() {
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
        ArrayList<Pair<String,Integer>> parsedTexts;
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
        if (questText != null) {
            if (questText.charAt(0) == LOCALPREFIX) {
                questText = StringResource.getResource(questText.substring(1));
            }
            font = TextUtils.DEFAULT_FONT.deriveFont(Font.ITALIC, 13f);
            txtSize = TextUtils.getTextSize(font, questText);
            // Rozparsovanie textu aby sa zmestil do infa.        
            parsedTexts = TextUtils.parseToSize(getWidth() - 2 *wGap, questText, font);
            starty += hGap + txtSize[1];        
            g.setFont(font);
            for (Pair<String,Integer> pair : parsedTexts) {
                g.drawString(pair.getFirst(), wGap , starty);
                starty += txtSize[1];
            }
        }
        
        // Vykreslenie textu z aktualneho stavu questu
        starty += 2*hGap;
        questText = quest.getStateText();
        if (questText != null) {
            if (questText.charAt(0) == LOCALPREFIX) {
                questText = StringResource.getResource(questText.substring(1));
            }
            font = TextUtils.DEFAULT_FONT.deriveFont(Font.BOLD, 13f);
            txtSize = TextUtils.getTextSize(font, questText);
            parsedTexts = TextUtils.parseToSize(getWidth() - 2 *wGap, questText, font);
            starty += hGap + txtSize[1];        
            g.setFont(font);
            for (Pair<String,Integer> pair : parsedTexts) {
                g.drawString(pair.getFirst(), wGap , starty);
                starty += txtSize[1];
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    /**
     * Metoda ktora vykresli toto quest info do grafickeho kontextu zadaneho
     * v parametri <b>g</b>. Pri viditelnom menu vykreslime obrazok toDraw
     * a podla aktivovanosti prekryvame oznacenou farbou
     * @param g Graficky kontext do ktoreho vykreslujeme quest info.
     */
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
     * <i>{@inheritDoc }</i>
     * @return {@inheritDoc }
     */
    @Override
    public int getWidth() {
        return width;
    }

    /**
     * <i>{@inheritDoc }</i>
     * @return {@inheritDoc }
     */
    @Override
    public int getHeight() {
        return height;
    
    }

    /**
     * <i>{@inheritDoc }</i>
     * @return {@inheritDoc }
     */
    @Override
    public String getName() {
        return null;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handler ">
    /**
     * Metoda ktora spracovava vstup z klavesnice. V tomto pripade iba reagujeme na
     * stlacenie esc ktore ukonci menu.
     */
    @Override
    public void inputHandling() {
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) { 
            if (sourceMenu != null) {
                exit();
                sourceMenu.activate();                                
                return;
            }            
        }
    }

    /**
     * Metoda ktora spracovava vstup z mysi. V tomto pripade neimplementovane.
     * @param e 
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    // </editor-fold>
    
}
