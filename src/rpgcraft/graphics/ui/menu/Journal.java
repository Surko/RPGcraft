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
import rpgcraft.entities.Entity;
import rpgcraft.entities.Player;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.quests.Quest;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.TextUtils;

/**
 * Journal je trieda ktora sa stara o vytvorenie a zobrazenie menu pre vsetky quest
 * ktore su aktivne v hracovi.
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu. 
 */
public class Journal extends AbstractInMenu {  
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Id zurnalu
     */
    public static final String JOURNALID = "_journal";
    /**
     * Meno tohoto menu
     */
    private static final String JOURNAL = StringResource.getResource(JOURNALID);
       
    /**
     * Sirka, vyska a odsadenie zurnalu.
     */
    private static final int jrnWidth = 200, jrnHeight = 400, wGap = 5, hGap = 5;
    /**
     * Sirka a vyska okna priamo s ulohami
     */
    private static final int questsWidth = jrnWidth - 40, questsHeight = jrnHeight - 40;
    /**
     * Vyska jednej ulohy
     */
    private static final int questHeight = 40;
    /**
     * Pocet uloh na zobrazenie ziskane z celkovej vysky a jednej vysky
     */
    private static final int questsToShow = questsHeight / questHeight;
    /**
     * Obrazok s ulohami
     */
    private Image questBox;
    /**
     * Oznaceny quest v menu
     */
    private int selection = 0;
    /**
     * x, y pozicie, kde sa bude vykreslovat QuestInfo.
     */
    private int selectPosX, selectPosY;
    /**
     * List s ulohami
     */
    private ArrayList<Quest> quests;
    /**
     * Instancia zurnalu
     */
    private static Journal instance;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Zakladny konstruktor ktory vytvori instanciu/objekt typu Journal. Dolezite
     * pri volani metody newInstance pri kopirovani takehoto menu.
     */
    public Journal() {
        super(null, null);
    }
    
    /**
     * Metoda ktora vytvori instanciu/objekt typu Journal. Journal je menu ktore zdruzuje
     * aktivne ulohy preto je dolezite aby sme mali entitu z ktorej tieto ulohy ziskame. Na
     * toto sluzi parameter <b>entity</b>. Ovladanie je riesenie cez parameter <b>input</b>.
     * Menu v ktorom bolo toto inMenu vytvorene je zadane poslednym parametrom <b>menu</b>.
     * Konstruktor vola metodu setGraphics ktora nastavi obrazok toDraw. Nakonci
     * pridame toto menu do listu menuList.
     * @param entity Entita pre ktoru vytvarame Journal
     * @param input Ovladanie vstupu
     * @param menu Menu v ktorom sme vytvorili Journal.
     */
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati Journal menu. Podobne ako v rodicovskom AbstractInMenu ziskavame toto menu
     * z menuList. V tomto pripade ale priamo vratime menu typu Journal.
     * @return Journal menu
     */
    public static Journal getJournalMenu() {
        return (Journal)menuList.get(JOURNALID);        
    }
    
    /**
     * <i>{@inheritDoc }</i>
     * @return {@inheritDoc }
     */
    @Override
    public String getName() {
        return JOURNALID;
    }
    
    /**
     * Metoda vrati oznaceny quest v menu
     * @return Quest ktory mame oznaceny
     */
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
    
    /**
     * <i>{@inheritDoc}</i>
     * @return Dlzku menu
     */
    @Override
    public int getWidth() {
        if (w <= 0) {
            return jrnWidth;
        }
        return w;
    }

    /**
     * <i>{@inheritDoc}</i>
     * @return Vyska menu
     */
    @Override
    public int getHeight() {
        if (h <= 0) {
            return jrnHeight;
        }
        return h;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Metoda ktora vykona aktualizaciu menu. Ked existuje nejake subMenu tak vykoname
     * aktualizaciu najprv nanom. V tomto menu dochadza k aktualizacii iba pri zmene stavu, ktore
     * donuti aktualizovat aj obrazok s vypisanymi questami (napr. ked prebiehame listom uloh a dostaneme
     * sa nakoniec tak nacitavame nove).
     */
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
    
    /**
     * Metoda ktora aktualizuje obrazok s vypisanymi questami. Na to aby sme mohli vypisat questy
     * potrebujeme ich ziskat. Questy sa nachadzaju iba v Player entite takze kontrolujeme
     * ci je nasa entita instancia Playera. Nasledne vyberieme aktivne questy a vykreslime
     * tolko kolko mame zadane v premennej questsToShow. Vykreslujeme odtial odkial mame
     * nastavenu premennu selection. Z Questu vykreslujeme iba nazov (getLabel).
     * Nakonci este skontrolujeme oznaceny quest a ulozime jeho suradnice do 
     * premennych selectedPosX a selectedPosY ktore potom posuvame do vytvorenia QuestInfa ako suradnice
     * tohoto menu.
     * @return True/false ci sa podarila aktualizacia obrazku.
     */
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
    
    /**
     * Metoda ktora rekalkuluje pozicie pre Journal.
     */
    @Override
    public final void recalculatePositions() {
        this.xPos = wGap;
        this.yPos = hGap;
    }   
        
    /**
     * Metoda ktora spravne ukonci toto menu => ked existuje subMenu (ako napr. QuestInfo)
     * tak  ukoncujeme toto subMenu. Pre ukoncenie je dolezite volat metodu setInMenu z AbstractMenu 
     * v ktorom bolo toto menu vytvorene.
     */
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi/vytvori obrazok toDraw. Kedze vsetky menu maju rovnaky tento obrazok
     * tak volame super.setGraphics. Na doplnenie vykreslujeme navrch tohoto menu 
     * nazov o ake menu sa jedna.
     */
    @Override
    protected final void setGraphics() {
        super.setGraphics();
        Graphics g = toDraw.getGraphics();
        g.setColor(Color.BLACK);
        int[] txtSize = TextUtils.getTextSize(TextUtils.DEFAULT_FONT, JOURNAL);
        g.drawString(JOURNAL, (getWidth() - txtSize[0])/2, txtSize[1] + hGap);
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    /**
     * Metoda ktora vykresluje Journal do grafickeho kontextu zadaneho v parametri <b>g</b>.
     * Vykreslujeme iba ked je menu viditeln (visible). Ked je tak vykreslime obrazok toDraw 
     * a k nemu obrazok questBox. Ked existuje v tomto menu subMenu tak zavolame 
     * nanho vykreslovanie. Vykreslovanie subMenu volame ako posledne aby bolo viditelnejsie.
     * @param g Graficky kontext kam vykreslujeme menu.
     */
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
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Handler ">
    /**
     * Metoda ktora reaguje na vstupy od uzivatela. Pri existencii subMenu 
     * volame inputHandling aj na toto subMenu. Pri stlaceni ESC alebo QUEST klavesy
     * spravne ukoncujeme toto menu. ENTER klavesa nam potvrdi oznaceny quest a ukaze
     * ho v menu/submenu QuestInfo.
     */
    @Override
    public void inputHandling() {
        if (subMenu != null) {
            
            if (subMenu.isActivated()) {
                subMenu.inputHandling();
                return;
            } 
        }
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())
                || input.clickedKeys.contains(InputHandle.DefinedKey.QUEST.getKeyCode())) {       
            exit();
            changedState = true;
            return;
        }
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ENTER.getKeyCode())) {       
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
     * <i>{@inheritDoc }</i>
     * @param e {@inheritDoc }
     * @see MouseEvent
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    // </editor-fold>
    
}
