/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import rpgcraft.GamePane;
import rpgcraft.effects.Effect;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.panels.GameMenu;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.MainUtils;

/**
 * Trieda ktora vytvara obalovaciu triedu pre SaveMap. Tiez si hu mozme predstavovat 
 * ako ukladaciu poziciu ako celok. Trieda v sebe obsahuje instanciu SaveMap ktoru 
 * moze ukladat aj nacitat zo suborov v zlozke region. Dalej trieda umoznuje ukladat/nacitat obrazok 
 * mapy, nazov tohoto savu, datum a aktualny cas hry kedy sme skoncili hru. Pri vytvarani alebo nacitani hlavnej hry je
 * vzdy vytvarana instancia tejto triedy a dalej sluzi az len pri ukladani.
 * @see SaveMap
 * @author kirrie
 */
public class Save {

    // <editor-fold defaultstate="collapsed" desc=" Pomocne enumy a triedy ">
    /**
     * Mena a indexy ulozene v enume pri ziskanie dat zo savov.
     */
    public enum RecordIndexes {
        
        ID(0),
        NAME(0),
        DATE(1),
        IMAGE(2);
        
        private int index;
        
        private RecordIndexes(int i) {
            this.index = i;
        }
        
        public int getIndex() {
            return index;
        }                        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre Save
     */
    private static final Logger LOG = Logger.getLogger(Save.class.getName());
    
    /**
     * Menu v ktorom je nacitany vytvoreny Save
     */
    protected GameMenu menu;
    /**
     * Panel v ktorom je nacitany vytvoreny Save
     */
    protected GamePane game;
    /**
     * Aktivny vstup v Save
     */
    protected InputHandle input;
    /**
     * Meno pozicie
     */
    protected String saveName;
    
    /**
     * InputStream z ktoreho nacitavame Save
     */
    private ObjectInputStream inputStream;
    /**
     * OutputStream do ktoreho ukladame Save
     */
    private ObjectOutputStream outputStream;
    
    /**
     * Stav Savu v ktorom sa odohrava hra
     */
    private SaveMap state;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor pre vytvorenie instanciu typu Save. Jednoducho prekopiruje
     * referencie ktore dostavame v parametroch.
     * @param saveName Meno savu
     * @param game Hraci panel kde bol vytvoreny save
     * @param menu Hracie menu kde bol vytvoreny save
     * @param input Input podla ktoreho spracovavame vstup
     */
    public Save(String saveName, GamePane game, GameMenu menu, InputHandle input) {
        this.saveName = saveName;
        this.game = game;
        this.menu = menu;
        this.input = input;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Ukladania/Nacitania/Vytvarania Savu ">
    /**
     * Metoda ktora ulozi a ukonci hru => nastavi menu na mainMenu. Parameter thumbImage
     * je obrazok ktory sa ulozi v adresari s ulozenou poziciou. Metoda postupne uklada
     * udaje o pozicii a mapu v ktorej sme sa hrali.
     * @param thumbImage Obrazok ktory ukladame
     */
    public void saveAndQuit(BufferedImage thumbImage) {
        try {
            state.end();
            outputStream = new ObjectOutputStream(new FileOutputStream(
                            PathManager.getInstance().getWorldSavePath(saveName, true) + File.separator + "world.info"));   
            outputStream.writeUTF(saveName);
            outputStream.writeUTF(DataUtils.getCurrentDate());
            outputStream.writeLong(MainUtils.objectId);            
            outputStream.writeLong(MainUtils.SECONDTIMER);
            outputStream.writeLong(state.lastSecond);            
            outputStream.writeShort(state.dayTime);
            outputStream.writeInt(state.gameTime);
            outputStream.writeObject(state.getAllEffects());
            
            ImageIO.write(thumbImage, "JPG", PathManager.getInstance().getWorldSavePath(saveName + File.separator + "world.jpg", true));
            
            if (state.player == null) {
                outputStream.writeInt(0);
                outputStream.writeInt(0);
            } else {
                outputStream.writeInt(state.player.getRegX());
                outputStream.writeInt(state.player.getRegY());
            }
            outputStream.close();
            for (Chunk chunk : state.chunkQueue) {
                state.saveMap(chunk);
            }
            state.entitiesList.clear();            
            LOG.log(Level.INFO, StringResource.getResource("_label_confirmsave"), saveName);
            
            
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Metoda ktora nacita a nastartuje hlavnu hru. Parameter saveName sluzi ako pomenovatel
     * pre save ktory budeme hrat. Nasledne uz len nacitavame do premennych a do state
     * udaje pre spravny beh.
     * @param saveName Meno savu ktory nacitavame
     * @return True/false ci sa podarilo nacitat.
     */
    public boolean loadAndStart(String saveName) {
        try {
            this.state = new SaveMap(this);
            LOG.log(Level.INFO, StringResource.getResource("_label_confirmload"), saveName);
            inputStream = new ObjectInputStream(new FileInputStream(
                               PathManager.getInstance().getWorldSavePath(saveName, false) + File.separator + "world.info"));        
            this.saveName = inputStream.readUTF();
            
            // DATUM - mozne doplnit na nejake testovanie, posledne prihlasenie, atd...
            inputStream.readUTF();
            
            // Nacitanie pocitadla objektov
            MainUtils.objectId = inputStream.readLong();
            // Nacitanie casu v hre
            MainUtils.SECONDTIMER = inputStream.readLong();
            state.lastSecond = inputStream.readLong();            
            state.dayTime = inputStream.readShort();
            state.setGameTime(inputStream.readInt());
            // Nacitanie efektov ktore sa spracovavaju v mape
            state.onSelfEffects = (ArrayList<Effect>) inputStream.readObject();
            
            // Nacitavanie thumbImage vynechavam co neznamena zeby sa nedalo na nieco vyuzit
            
            // Pozicia stredneho chunku okolo ktoreho sa loaduje.
            int chunkX = inputStream.readInt();
            int chunkY = inputStream.readInt();                        
            
            inputStream.close();
            // Nacitanie mapy okolo pozicii ktore boli ulozene v tomto save
            state.loadMapAround(chunkX, chunkY);
            // Reinicializacia efektov ktore sme nacitali do mapy
            for (Effect effect : state.onSelfEffects) {
                effect.reinitEntities(state);
            }
            
        } catch (Exception ex) {
            if (ex instanceof FileNotFoundException) {
                LOG.log(Level.SEVERE, StringResource.getResource("_label_cannotsaveload"), ex.toString());
                new MultiTypeWrn(ex, Color.red, StringResource.getResource("_msave", new String[] {saveName}),
                        null).renderSpecific(StringResource.getResource("_label_saverror"));                  
            } else {
                LOG.log(Level.SEVERE, null, ex);
            }
            return false;
        }
        
        return true;
    }
    
    /**
     * Metoda ktora vytvori novu mapu => novu instanciu SaveMap.
     */
    public void createNewSave() {
        this.state = new SaveMap(this);
    }
    
    /**
     * Metoda ktora spracovava vstup. Odposielame ho dalej volanim metody inputHandling
     * v SaveMap.
     */
    public void inputHandling() {
        state.inputHandling();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati mapu v ktorej hrame.
     * @return Aktualnu SaveMap.
     */
    public SaveMap getSaveMap() {
        return state;
    }           
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vrati true/false ci existuje save s menom zadanom v parametri <b>name</b>
     * @param name Meno savu ktory hladame
     * @return True/false ci existuje save
     */
    public static boolean saveExist(String name) {        
        return PathManager.getInstance().getWorldSavePath(name + File.separator + "world.info", false).exists();                
    }
    
    /**
     * Metoda ktora vrati list listov s objektmi ktore predstavuju informacie o jednotlivych savoch.
     * Kazdy List predstavuje udaje o jednej ulozenej pozicii a obsahuje len tie polozky
     * ktore sme zadali parametrom <b>param</b>. Mozne parametre ktore sa daju pouzit 
     * su dane v enume RecordIndexes. Vsetky savy ziskavame zo zlozky nastavenej v
     * PathManageri a postupne z nich vyberame potrebne udaje.
     * @param param Parametre ktore chceme v listoch.
     * @return List s listami v ktorych sa nachadzaju udaje o savoch.
     */
    public static ArrayList<ArrayList<Object>> getGameSavesParam(ArrayList<String> param) {
        ObjectInputStream _inputStream;
        ArrayList<ArrayList<Object>> resultRecords = new ArrayList<>();
        for (File f : PathManager.getInstance().getWorldPath().listFiles()) {
            try {
                ArrayList<Object> record = new ArrayList<>();
                Object[] saveData = new Object[RecordIndexes.values().length];
                _inputStream = new ObjectInputStream(new FileInputStream( 
                                  f + File.separator +  "world.info"));
                
                saveData[RecordIndexes.NAME.getIndex()] = _inputStream.readUTF();
                saveData[RecordIndexes.DATE.getIndex()] = _inputStream.readUTF();
                saveData[RecordIndexes.IMAGE.getIndex()] = ImageIO.read(
                            new File(f, File.separator + "world.jpg"));
                
                _inputStream.close();                                
                
                for (String p : param) {
                    switch (RecordIndexes.valueOf(p)) {
                        case NAME : {
                            record.add(saveData[RecordIndexes.NAME.getIndex()]);
                        } break;
                        case DATE : {
                            record.add(saveData[RecordIndexes.DATE.getIndex()]);
                        } break;
                        case IMAGE : {
                            record.add(saveData[RecordIndexes.IMAGE.getIndex()]);    
                        } break;
                        default : LOG.log(Level.WARNING,p + " is not recognized parameter");
                    }
                }
                                                
                resultRecords.add(record);
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, StringResource.getResource("_msaveinfo"), ex);
                        
            }
            
        }
        
        return resultRecords;
        
    }
    // </editor-fold>    
}
