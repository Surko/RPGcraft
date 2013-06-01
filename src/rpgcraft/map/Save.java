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
 *
 * @author kirrie
 */
public class Save {
        
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
    
    private static final Logger LOG = Logger.getLogger(Save.class.getName());
    
    protected GameMenu menu;
    protected GamePane game;
    protected InputHandle input;
    protected String saveName;
    
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    
    private SaveMap state;
    
    public Save(String saveName, GamePane game, GameMenu menu, InputHandle input) {
        this.saveName = saveName;
        this.game = game;
        this.menu = menu;
        this.input = input;
    }
    
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
            state.loadMapAround(chunkX, chunkY);             
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
    
    public void createNewSave() {
        this.state = new SaveMap(this);
    }
    
    public void inputHandling() {
        state.inputHandling();
    }
    
    public SaveMap getSaveMap() {
        return state;
    }           
    
    public static boolean saveExist(String name) {                   
        return PathManager.getInstance().getWorldSavePath(name, false).exists();                
    }
    
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
    
}
