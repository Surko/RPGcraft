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
import java.util.Random;
import rpgcraft.entities.Entity;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.plugins.Listener;
import rpgcraft.plugins.AbstractInMenu;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.resource.ConversationGroupResource;
import rpgcraft.resource.ConversationResource;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.Pair;
import rpgcraft.utils.TextUtils;

/**
 * ConversationPanel je trieda ktora sa stara o vytvorenie a zobrazenie konverzacneho panelu.
 * Trieda dedi od AbstractInMenu cim sme donuteni implementovat zakladne 
 * abstraktne metody z AbstractInMenu.
 */
public class ConversationPanel extends AbstractInMenu<ConversationPanel> {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final String TEST = "Testing";
    private static final String CONVID = "_convid";  
    private static final int wGap = 5, hGap = 5;
    private static final int itemHeight = 30;
    private static final int convWidth = 600, convHeight = 400;              
    private static final String CONVER = StringResource.getResource(CONVID); 
    private static final char LOCALPREFIX = '@';
    
    protected int width = convWidth, height = convHeight;
    protected Font font,boldTitleFont,italicFont, boldNormalFont;
    private int[] txtSize, boldSize, italicSize, boldNormalSize;
    private Entity convEntity;    
                
    private Pair pair;
    private int selection, iGroupsToShow = convHeight / itemHeight, localSelect;
    private ArrayList<ConversationGroupResource> groupsToShow;
    private ArrayList<ConversationGroupResource> allGroups;
    private ArrayList<ConversationGroupResource> tempGroups;
    private int convStartY = 0, starty;
    private BufferedImage convImage;
    private BufferedImage listImage;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="Collapsed" desc=" Konstruktory ">
    /**
     * Default konstruktor pre vytvorenie instancie konverzacneho panela. Dolezity
     * pri volani newInstance.
     */
    public ConversationPanel() {
        super(null, null);
    }
    
    /**
     * Konstruktor pre vytvorenie konverzacneho panelu. Konverzacny panel je tvoreny medzi dvoma
     * entitami ktore su zadane parametrani <b>entity, convEntity</b>. Z tychto entit vytvorime par
     * a zavolame metodu setGraphics na nastavenie toDraw obrazku. Nakonci pridame konverzacny panel
     * do menuListu.
     * @param entity Entita ktora vyvolala konverzaciu
     * @param convEntity Entita s ktorou sa rozpravame
     * @param input Vstup podla ktoreho spracovavame
     * @param menu Menu v ktorom sme toto menu vytvorili.
     */
    public ConversationPanel(Entity entity, Entity convEntity, InputHandle input, AbstractMenu menu) {
        super(entity, input);
        this.convEntity = convEntity;        
        this.menu = menu;
        this.pair = new Pair(entity, convEntity);
        setGraphics();
        this.changedState = true;        
        menuList.put(CONVID, this);                
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Inicializatory ">
    /**
     * <i>{@inheritDoc} </i>
     * Metoda ktora inicializuje konverzacny panel z originalneho menu zadaneho parametrom
     * <b>origMenu</b>. Ostatne dva parametre sluzia ako preinicializator kedze konverzacia
     * moze prebiehat vzdy medzi dvoma roznymi entitami.
     * @param origMenu Originalne menu z ktoreho cerpame vlastnosti
     * @param e1 Entita pre ktoru vytvarame konverzacny panel
     * @param e2 Entita s ktorou sa entita e1 rozprava
     * @return Novo vytvoreny konverzacny panel.
     */
    @Override
    public ConversationPanel initialize(ConversationPanel origMenu, Entity e1, Entity e2) {
        this.entity = e1;
        this.convEntity = e2;  
        if (convEntity.getGroupConversations() != null) {
            this.allGroups = new ArrayList(convEntity.getGroupConversations());
        }
        this.input = origMenu.getInput();
        this.menu = origMenu.getMenu();
        this.pair = new Pair(e1, e2);
        this.convImage = origMenu.convImage;
        Graphics g = convImage.getGraphics();
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, convImage.getWidth(), convImage.getHeight());
        this.changedState = true;
        this.localSelect = 0;
        this.toDraw = origMenu.toDraw;                
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

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * <i>{@inheritDoc }<i>
     * <p>
     * Pozicie su nastavene na wGap a hGap => lava horna pozicia
     * </p>
     * 
     */
    @Override
    public void recalculatePositions() {
        this.xPos = wGap;
        this.yPos = hGap;
    }

    /**
     * Metoda ktora skontroluje ci moze byt konverzacia zobrazena. Kontrola prebieha zavolanim listenerov 
     * a po dokonceni operacie vybrani navratovej hodnoty ci je true.
     * @param listeners Listenery z konverzacie ktore volame.
     * @return True/false ci su splnene podmienky
     */
    private boolean checkConditions(ArrayList<Listener> listeners) {        
        ActionEvent e = new ActionEvent(menu, 0, -1, null, pair);
        boolean result = true;        
        for (Listener list : listeners) {
            list.actionPerformed(e);
            if (e.getReturnValue().equals(Boolean.TRUE)) {                
                e.setReturnValue(false);
            } else {
                return false;
            }
        }
        return result;
    }
    
    /**
     * Metoda ktora vymaze docasne pridane konverzacie z listu
     */
    private void removeTempConversation() {
        if (tempGroups != null) {
            for (ConversationGroupResource res : tempGroups) {
                allGroups.remove(res);
            }
        }
    }
    
    /**
     * Metoda ktora vykresli konverzaciu zadanu parametrom <b>res</b>. Z konverzacie sa vyberie text na zobrazenie.
     * Takisto vyberieme konverzacie na odpoved (getAnswerConversations) ktore su pridane k celkovym konverzaciam
     * ale aj k docasnym. Nakonci vykona akcie ktore ma konverzacie v sebe.
     * @param res Konverzaciu ktoru vykreslujeme a vykonavame.
     */
    private void paintConversation(ConversationResource res) {        
        Graphics g = convImage.getGraphics();   
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, convImage.getWidth(), convImage.getHeight());

        int sPaint = hGap + italicSize[1];
        g.setColor(Color.WHITE);
        g.setFont(italicFont);
        String text = res.getText();
        if (text.charAt(0) == LOCALPREFIX) {
            text = StringResource.getResource(text.substring(1));
        }
        ArrayList<Pair<String,Integer>> parsedTexts = TextUtils.parseToSize(convImage.getWidth(), text, italicFont);                
        for (Pair<String,Integer> pair : parsedTexts) {                    
            g.drawString(pair.getFirst(), wGap, sPaint);
            sPaint += italicSize[1];
        }

        if (allGroups.size() <= iGroupsToShow) {
            changedState = true;
        }

        removeTempConversation();
        if (tempGroups == null) {
            tempGroups = new ArrayList<>();
        }        
        if (res.getAnswerConversations() != null) {
            for (String s : res.getAnswerConversations()) { 
                ConversationGroupResource groupRes = ConversationGroupResource.getResource(s);
                if (groupRes != null) {
                    if (!allGroups.contains(groupRes)) {
                        tempGroups.add(groupRes);
                        allGroups.add(groupRes);
                    }
                }
            }
        }        
    }
    
    /**
     * Metoda ktora skontroluje konverzacie v grupe na localselect pozicii.
     * Z grupy si vyberieme clenov (konverzacie) a pre kazdu konverzaciu 
     * zavolame ci splna podmienky na zobrazenie. Ked splnuje podmienky
     * tak hu pridame k moznym konverzaciam. Nakonci nahodne vyberieme jednu z nich
     * a zavolame metodu paintConversation.
     */
    private void paintSomeConversation() {
        ConversationGroupResource conversation = groupsToShow.get(localSelect);  
        ArrayList<ConversationResource> possible = new ArrayList<>();
        for (ConversationResource res : conversation.getMembers()) {
            if (res.getConditions() == null || checkConditions(res.getConditions())) {
                possible.add(res);                                                                
            }                        
        }           
        if (!possible.isEmpty()) {
            Random random = new Random();
            int index = random.nextInt(possible.size());
            ConversationResource resToShow = possible.get(index);
            paintConversation(resToShow);
            if (resToShow.getActions() != null) {
                for (Action action : resToShow.getActions()) {
                    action.setActionEvent(new ActionEvent(menu, 0, -1, null, pair));
                    DataUtils.execute(action);
                }
            }            
        } else {
            Graphics g = convImage.getGraphics();   
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, convImage.getWidth(), convImage.getHeight());

            int sPaint = hGap + italicSize[1];
            g.setColor(Color.WHITE);
            g.setFont(italicFont);
            String text = StringResource.getResource("notalk");
            g.drawString(text, wGap, sPaint);            
        }        
    }
    
    /**
     * Metoda ktora vykona aktualizaciu konverzacneho panelu, co znamena prepisanie moznych konverzacii
     * medzi dvoma entitami
     */
    private void updateListImage() {
        listImage = new BufferedImage((getWidth() - 3 * wGap)/2, getHeight() - 2*hGap,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = listImage.getGraphics();    
        g.setFont(boldNormalFont);
        starty = 0;
        if (allGroups == null) {
            if (convEntity.getGroupConversations() != null) {
                allGroups = new ArrayList<>(convEntity.getGroupConversations());
            }
        }
        
        if (allGroups != null && !allGroups.isEmpty()) {
            if (selection >= allGroups.size()) {
                selection = allGroups.size() - 1;
            }            
            localSelect = 0;
                
            if (allGroups.size() > iGroupsToShow) {
                // sItem = kolko predmetov je za oznacenym predmetom.
                int sItem = allGroups.size() - selection - 1;
                
                groupsToShow = new ArrayList<>();

                if (sItem < iGroupsToShow) {
                    localSelect = iGroupsToShow - sItem - 1;
                    int diff = allGroups.size() - iGroupsToShow;
                    for (int i = diff < 0 ? 0 : diff; i < allGroups.size(); i++) {
                        groupsToShow.add(allGroups.get(i));
                    }
                } else {
                    for (int i = selection; i < selection + iGroupsToShow; i++) {
                        groupsToShow.add(allGroups.get(i));
                    }
                }
            } else {
                groupsToShow = allGroups;
                localSelect = selection;
            }

            String text;
            for (int i = 0; i < groupsToShow.size(); i++) {
                ConversationGroupResource item = groupsToShow.get(i); 
                text = item.getLabel();                
                if (text.charAt(0) == LOCALPREFIX) {
                    text = StringResource.getResource(text.substring(1));
                }
                g.drawString(text, 0, (i + 1) * itemHeight - itemHeight / 2);                
            }            
                
            g.setColor(Colors.getColor(Colors.selectedColor));
            g.fillRoundRect(0, localSelect * itemHeight, listImage.getWidth(), itemHeight, wGap, hGap);
            
        } else {
            g.drawString(StringResource.getResource("notalking"), 0, boldNormalSize[1] + hGap);
        }        
    }
    
    /**
     * Metoda ktora aktualizuje konverzacny panel. Dochadza k nemu iba pri zmene stavu
     * (changedState). Pri zmene volame aktualizaciu obrazku updateListImage ktora nastavi
     * mozne konverzacie s entitou.
     */
    @Override
    public void update() {
        if (changedState) {
            updateListImage();
            changedState = false;
        }                
    }
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * <i>{@inheritDoc } </i>
     * <p>
     * V tomto menu nastavujeme vysky a fonty pre texty.
     * </p>
     */
    @Override
    protected void setGraphics() {
        super.setGraphics();        
        this.convImage = new BufferedImage((convWidth - 3 * wGap) / 2, convHeight - 2 * hGap,
                BufferedImage.TYPE_INT_ARGB);
        Graphics g = convImage.getGraphics();
        g.setColor(Color.BLACK);
        g.clearRect(0, 0, convImage.getWidth(), convImage.getHeight());
        setFonts();               
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
     * Metoda vrati sirku menu pre info menu
     * @return Sirka info menu
     */
    @Override
    public int getWidth() {
        if (width <= 0) {
            return convWidth;
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
            return convHeight;
        }
        return height;
    }

    /**
     * Metoda ktora vrati meno tohoto menu.
     * @return Meno konverzacneho panelu
     */
    @Override
    public String getName() {
        return CONVID;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    
    /**
     * Metoda ktora vykresli konverzacny panel. Ked je menu viditelne tak najprv vykreslime
     * toDraw image a donho vykreslime listImage s convImage.
     * @param g Graficky kontext do ktoreho kreslime.
     */
    @Override
    public void paintMenu(Graphics g) {
        if (visible) {
            g.drawImage(toDraw, xPos, yPos, null);
            g.drawImage(listImage, xPos + wGap, yPos + hGap, null);
            g.drawImage(convImage, xPos + convImage.getWidth() + wGap, yPos + hGap, null);
        }
    }

    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Handling ">
    /**
     * Metoda ktora spracovava vstup od uzivatela. Pri stlacenie ESC ukoncujeme menu.
     * Stlacenie tlacidla UP alebo DOWN nasledne zvysi alebo znici oznacenu konverzaciu
     * Stlacenim ENTER vyvolame konverzaciu tohoto typu, odstranime docasne pridane konverzacie
     * v paneli (kedze sa rozpravame uz o niecom inom tak docasne konverzacie uz nemaju zmysel
     * plus by mohli poskodit korektnost uloh alebo deju hry), a vykreslenie vybranej konverzacie.
     */
    @Override
    public void inputHandling() {
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ESCAPE.getKeyCode())) {
            exit();
            menu.setInMenu(null);
        }
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.UP.getKeyCode())) {
            if (selection > 0) {
                selection--;
                changedState = true;
            }
        }

        if (input.clickedKeys.contains(InputHandle.DefinedKey.DOWN.getKeyCode())) {
            if (selection < allGroups.size()) {
                selection++;
                changedState = true;
            }
        }
        
        if (input.clickedKeys.contains(InputHandle.DefinedKey.ENTER.getKeyCode())) {             
            if (selection >= 0 && allGroups.size() != 0) {                                
                paintSomeConversation();
            }
        }
        
    }

    /**
     * <i>{@inheritDoc}</i>
     * @param e {@inheritDoc }
     */
    @Override
    public void mouseHandling(MouseEvent e) {
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vrati Konverzacny panel z listu menuList.
     * @return Konverzacny panel
     */
    public static ConversationPanel getConversationPanel() {
        return (ConversationPanel)menuList.get(CONVID);
    }
    
    // </editor-fold>
    
}
