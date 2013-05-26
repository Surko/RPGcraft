/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map;

import rpgcraft.map.generators.MapGenerator;
import rpgcraft.entities.Player;
import rpgcraft.entities.MovingEntity;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import rpgcraft.GamePane;
import rpgcraft.MainGameFrame;
import rpgcraft.entities.*;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.*;
import rpgcraft.graphics.ui.particles.Particle;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.chunks.ChunkContent;
import rpgcraft.map.tiles.*;
import rpgcraft.plugins.RenderPlugin;
import rpgcraft.resource.StringResource;
/**
 *
 * @author Kirrie
 */

public class SaveMap  {          
    
    /* 
     * Pre zistenie kam ma zaradit mapu budem pouzivat bitove operacie posunu doprava o 4,
     * 0x0 - 15x15 budu v rovnakom priestore na disku pod menom region0x0
     */                              
    private final Logger LOG = Logger.getLogger(getClass().getName());       
    
    protected String saveName;
    
    protected Deque<Chunk> chunkQueue = new LinkedList<> ();
    private Deque<Particle> partRemove = new ArrayDeque<>();
    private Deque<Entity> entityRemove = new ArrayDeque<>();
    
    private int numberOfChunks;
    private Chunk[][] chunksToRender;    
    
    private RenderPlugin render;
    
    private int gameTime = 6 ;
    
    protected int x;
    protected int y;
    
    private final GamePane game;    
    private final InputHandle input;
    public static final boolean defineTileself = true;
    public static final int chunkSize = 25;            
    
    public MovingEntity player;   
    public MovingEntity origPlayer;
    
    private Color fpsColor = Colors.getColor(Colors.fpsColor);
        
    private boolean lightState = true;
    private boolean chunkState = true;
    private boolean playerState = true;
    
    private ObjectInputStream inputStream;
    private ObjectOutputStream outputStream;
    
    protected volatile ArrayList<Entity> entities = new ArrayList<>();
    protected volatile ArrayList<Particle> particles = new ArrayList<>();
    
    protected volatile HashMap<Integer,Entity> tileEntities = new HashMap<>();
    
    private Graphics2D g2d;
    
    private int _lcurrent;
    private int _xcurrent;
    private int _ycurrent;
                               
    private int width;
    private int height;
    public int xCoordinate,yCoordinate;
        
    private DayLighting dayLight;
    
    private int screenLength;    
    
    private boolean stat = false;  
    private boolean particle = true;
    private boolean scaleable = false;
    private boolean lighting = false;
        
    private static final int delayThread = 2;
    private static int jammedMenu = 0;
    
    public SaveMap(Save save) {
        this.game = save.game; 
        this.input = save.input;
        this.saveName = save.saveName;
        this.width = game.getWidth();
        this.height = game.getHeight();
        this.dayLight = new DayLighting();
        this.numberOfChunks = 3;
        this.screenLength = numberOfChunks << 9;        
        this.chunksToRender = new Chunk[numberOfChunks][numberOfChunks];
        render = RenderPlugin.loadRender("sk.jar");
        render.setMap(this);
    }
    
    public void initializeTiles() {
        // nacita zakladne dlazdice potrebne k fungovaniu hry      
        Tile.initializeTiles();
        //debuggingTiles(tiles.values());
    }    
            
    /**
     * Metoda ktora posunie Chunk vo fronte Chunkov (ak sa tam nachadza) na posledne miesto takym sposobom
     * ze vymaze aktualne miesto a prida ho nakoniec. Ked je parameter chunk rovny null tak vrati false.
     * @param chunk Chunk na presunutie
     * @result True/False ci sa podaril presun na spodok fronty.
     */
    private boolean shiftChunk(Chunk chunk) {
        if (chunk == null) return false;
        chunkQueue.remove(chunk);
        chunkQueue.add(chunk);
        return true;
    }
    
    public void paint(Graphics g) {  
        try {
            if (scaleable) {
                render.scale(g);
            }
        
            render.paintBackground(g, chunksToRender);
            //paintFlora(g);
            render.paintEntitiesParticles(g, entities, particles);   
            
            if (lighting) {
                render.paintLighting(g);                        
            }
            
            if (stat) {
                render.paintStrings(g);
            }
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }        
    }
    
    public void update() {
        updateAround(player);
        updateEntities();
        if (particle) {
            updateParticles();
        }       
        if (lighting) {
            updateLighting();      
        }
    }        
    
    private void updateAround(Entity e) {
        if (chunkState) {
                  
            if (e == null) {
                _xcurrent = 0;
                _ycurrent = 0;
                _lcurrent = 0; 
            } else {
                _xcurrent = e.getRegX();
                _ycurrent = e.getRegY();                
            }
            
            for (int i = 0; i < numberOfChunks; i++) {
                for (int j = 0; j < numberOfChunks; j++) {
                    chunksToRender[i][j] = chunkXYExist(_xcurrent-(numberOfChunks / 2) + j, _ycurrent - (numberOfChunks / 2) + i);
                }
            }                        
                        
            chunkState = false;
        }                                        
        //debuggingText();                
        
        render.setScreenX((width - (screenLength)) / 2 + 256);
        render.setScreenY((height - (screenLength)) / 2 + 256);
                             
    }
    
    private void updateEntities() {
        for (Entity e: entities) {
            if (!e.update()) {
                entityRemove.add(e);
            }
        }
        
        if (!entityRemove.isEmpty()) {
            entities.remove(entityRemove.pop());
        }
    }
    
    private void updateLighting() {                      
        if (lightState) {
            dayLight.init(gameTime);  
            render.makeLightingMap(dayLight);
            lightState = false;
        }
    }
          
    /**
     * Metoda updateParticles ktora simuluje vsetky castice ponechane 
     * v poli <b>particles</b>. Ked castica "dozije", premenna span v objekte, 
     * tak hu zaradime do fronty s casticami cakajucich na odstranenie.
     */
    private void updateParticles() {   
        
        for (Particle part: particles) {            
                if (!part.update()) {
                    partRemove.add(part);
                }            
        }
            
        if (!partRemove.isEmpty()) {
            particles.remove(partRemove.pop());
        }
    }                
                                                
    public void setWidthHeight(Integer width, Integer height) {
        render.setScaleParams(width.doubleValue()/MainGameFrame.getContentWidth(),
            height.doubleValue()/MainGameFrame.getContentHeight());        
        
        if (!scaleable) {            
            this.width = width;
            this.height = height;  
            render.setWidthHeight(width, height);            
        } else {
            this.width = MainGameFrame.getContentWidth();
            this.height = MainGameFrame.getContentHeight();
            render.setWidthHeight(width, height);  
        }
        //
    }                
    
    public void setLevel(int level) {
        if (level < Chunk.getDepth())
            this._lcurrent = level;
    }
    
    public void incLevel() {
        this._lcurrent++;
    }
    
    public void decLevel() {
        this._lcurrent--;
    }        
    
    public MovingEntity getPlayer() {
        return player;
    }
    
    public int getChunksSize() {
        return numberOfChunks;
    }                    
    
    public Chunk[][] getChunksToRender() {
        return chunksToRender;
    }
    
    public DayLighting getDayLighting() {
        return dayLight;
    }
    
    public boolean getChunkState() {
        return chunkState;
    }
    
    public boolean hasLighting() {
        return lighting;
    }        
    
    public Entity getEntity(String name) {
        for (Entity e : entities) {
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }
    
    public int getLevel() {
        return _lcurrent;
    }
        
    public void paintFPS(Graphics g,int framer) {        
        g.setColor(fpsColor);
        g.drawString(String.valueOf(framer)+ " FPS", 0, 10);
    }
            
    public boolean chunkExist(Chunk chunk) {
        return chunkQueue.contains(chunk);
    }
    
    // Zistovace na Chunk podla nejakych suradnic
    
    /**
     * Metoda ktora vyhlada chunk ( velkosti 512 x 512 pixelov) z fronty chunkov
     * podla parametrov x a y, ktore predstavuju regionalne pozicie chunku.
     * [0,0] => Chunk [0,0]
     * @param x X-ova pozicia chunku vo svete
     * @param y Y-ova pozicia chunku vo svete
     * @return Chunk na danych poziciach
     */
    public Chunk chunkXYExist(int x, int y) {        
        for (Chunk chunk : chunkQueue) {
            if (((chunk.getX()) == x) && ((chunk.getY()) == y)) {
                return chunk;
            }
        }
        return loadMapOnBegin(x, y);        
    }
    
    /**
     * Metoda ktora vyhlada chunk (velkosti 512 x 512 pixelov) z fronty chunkov
     * podla parametrov x a y, ktore predstavuju pixelove pozicie chunku.
     * [0,0]-[512,512] => Chunk [0,0] 
     * @param x X-ova pixelova pozicia chunku vo svete
     * @param y Y-ova pixelova pozicia chunku vo svete
     * @return Chunk na danych poziciach
     */
    public Chunk chunkPixExist(int x, int y) {
        for (Chunk chunk : chunkQueue) {
            if (((chunk.getX()) == (x >> 9) ) && (chunk.getY() == (y >> 9))) {
                return chunk;
            }
        }
        return loadMapOnBegin(x >> 9, y >> 9);  
    }

    public boolean addEntity(Entity e) {
        if (e instanceof Player) {
            this.player = (Player) e;
            this.render.setMap(this);
            System.out.println("HOTOVO");
            this.player.setHandling(input);
            entities.add(e);
            _lcurrent = e.getLevel();
            return true;            
        }
        
        return entities.add(e);
    }
    
    public boolean addParticle(Particle particle) {
        return particles.add(particle);
    }
    
    public void loadMapAround(MovingEntity e) {
        for (int i = e.getRegY() - 1; i < e.getRegY()+2; i++) {
            for (int j = e.getRegX() -1 ; j < e.getRegX()+2; j++) {
                loadMap(j, i);
            }
        }
        chunkState = true;
    }
    
    public void loadMapAround(int x, int y) {
        for (int i = y - 1; i < y+2; i++) {
            for (int j = x - 1 ; j < x+2; j++) {
                loadMap(j, i);
            }
        }
        chunkState = true;
    }        
    
    
    public void inputHandling() {        
        
        if (player != null) {
            player.inputHandling();
        }
        
        if (input.clickedKeys.contains(input.stat.getKeyCode())) {
            stat = !stat;            
        }        
        
        if (input.clickedKeys.contains(input.particles.getKeyCode())) {
            particle = !particle;            
        }
        
        if (input.clickedKeys.contains(input.scaling.getKeyCode())) {
            scaleable = !scaleable;
            setWidthHeight(game.getWidth(), game.getHeight());            
        }
        
        if (input.clickedKeys.contains(input.levelUp.getKeyCode())) {
            _lcurrent++;
        }
        if (input.clickedKeys.contains(input.levelDown.getKeyCode())) {
            _lcurrent--;
        }
    }

    public void setLightState(boolean state) {
        this.lightState = state;
    }
    
    public int getWidth() {
        return width;
    }
    
    public int getHeight() {
        return height;
    }
    
    public ArrayList<Entity> getEntities() {
        return entities;
    }
    
    public int getGameTime() {
        return gameTime;
    }
    
    public void setGameTime(int time) {
        this.gameTime = time;
    }
    
    public void increaseGameTime() {
        this.gameTime++;
    }                       
    
    public ChunkContent generateMap() {
        MapGenerator map = new MapGenerator(Chunk.getSize(), Chunk.getDepth());
        return map.generate();
    }
    
    public Chunk loadMapOnBegin(int x, int y) {
        Chunk chunk = null;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(
                    PathManager.getInstance().getWorldSavePath(saveName + PathManager.MAPS, false) + File.separator
                    + "region["+x+","+y+"].m")); 
            try {
                SaveChunk save = (SaveChunk) inputStream.readObject();

                chunk = addChunkOnBegin(save.getChunk());
                ArrayList<Entity> entLoad = save.getEntities();
                if (entLoad != null) {
                    for (Entity e : entLoad) {                    
                        e.setSaved(false);
                        e.setMap(this);                    
                        e.setChunk(save.getChunk());
                        addEntity(e);
                    }
                }
                return chunk;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, StringResource.getResource("_bsaveformat"));
                new MultiTypeWrn(null, Color.BLACK, StringResource.getResource("_bsaveformat"),null).renderSpecific(
                        StringResource.getResource("_label_badsave"));
            }

        } catch(Exception e) { 
            return addChunkOnBegin(new Chunk(generateMap(), x, y));              
        }
        
        return chunk;
    }
    
    /**
     * 
     * @param x
     * @param y 
     */
    public Chunk loadMap(int x, int y) {
        Chunk chunk = chunkXYExist(x, y);
        if (shiftChunk(chunk)) {
            return chunk;
        }
        try {
            inputStream = new ObjectInputStream(new FileInputStream(
                    PathManager.getInstance().getWorldSavePath(saveName + PathManager.MAPS, false) + File.separator
                    + "region["+x+","+y+"].m")); 
            try {
                SaveChunk save = (SaveChunk) inputStream.readObject();

                chunk = addChunk(save.getChunk());
                ArrayList<Entity> entLoad = save.getEntities();
                if (entLoad != null) {
                    for (Entity e : entLoad) {                    
                        e.setSaved(false);
                        e.setMap(this);                    
                        e.setChunk(save.getChunk());
                        addEntity(e);
                    }
                }
                return chunk;
            } catch (Exception e) {
                LOG.log(Level.SEVERE, StringResource.getResource("_bsaveformat"));
                new MultiTypeWrn(null, Color.BLACK, StringResource.getResource("_bsaveformat"),null).renderSpecific(
                        StringResource.getResource("_label_badsave"));
            }

        } catch(Exception e) { 
            return addChunk(new Chunk(generateMap(), x, y));              
        }
        
        return chunk;
    }
    
    // Ukladacie a nacitacie operacie s mapou (Chunk save/load)
    
    /**
     * Metoda prida chunk predany parametrom <b>chunk</b> do nacitanych chunkov.
     * Ked je fronta nacitanych chunkov vacsia ako maximalne povolena, tak ulozi
     * prvy a prida novy na koniec fronty.
     * @param chunk Chunk do nacitanych chunkov.
     * @return Chunk ktory pridavame
     */
    private Chunk addChunk(Chunk chunk) {
        if (chunkQueue.size() > chunkSize) {
            saveMap(chunkQueue.poll());
            chunkQueue.add(chunk);
        } else {
            chunkQueue.add(chunk);             
        }
        return chunk;
    }
    
    /**
     * Metoda prida chunk predany parametrom <b>chunk</b> do nacitanych chunkov
     * priamo nazaciatok fronty.
     * Ked je fronta nacitanych chunkov vacsia ako maximalne povolena, tak ulozi
     * prvy a prida novy namiesto neho.
     * @param chunk Chunk do nacitanych chunkov.
     * @return Chunk ktory pridavame
     */
    private Chunk addChunkOnBegin(Chunk chunk) {
        if (chunkQueue.size() > chunkSize) {
            saveMap(chunkQueue.poll());
            chunkQueue.addFirst(chunk);            
        } else {
            chunkQueue.addFirst(chunk);             
        }
        return chunk;
    }
    
    public void saveMap(int x, int y) {      
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(
                            PathManager.getInstance().getWorldSavePath(saveName + PathManager.MAPS, true)
                    + "region["+x+","+y+"].m")); 
            for (Chunk chunk : chunkQueue) {
                if ((chunk.getX() == x)&&(chunk.getY() == y)) {                                        
                    // Objekt s entitami, ktory ulozime k Chunku.
                    ArrayList<Entity> entSave = new ArrayList<>();
                    for (Entity e : entities) {
                        if ((e.getRegX() == x )&&(e.getRegY() == y)) {
                            entSave.add(e);                        
                            e.setSaved(true);
                        }
                    }
                    SaveChunk save = new SaveChunk(entSave, chunk);
                    outputStream.writeObject(save);
                    outputStream.close();
                }
            }
        } catch(Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }           
    
    public void saveMap(Chunk chunkToSave) {
        try {
            int chunkX = chunkToSave.getX();
            int chunkY = chunkToSave.getY();
            
            outputStream = new ObjectOutputStream(new FileOutputStream(
                    PathManager.getInstance().getWorldSavePath(saveName + PathManager.MAPS, true) + File.separator
                    + "region["+chunkX+","+chunkY+"].m"));
            //outputStream.writeObject(chunkToSave);
            
            // Objekt s entitami, ktory ulozime k Chunku.
            ArrayList<Entity> entSave = new ArrayList<>();
            for (Entity e : entities) {
                if ((e.getRegX() == chunkX )&&(e.getRegY() == chunkY)) {
                    entSave.add(e);
                    e.setSaved(true);                    
                }
            }
            
            SaveChunk save = new SaveChunk(entSave, chunkToSave);
            outputStream.writeObject(save);
            outputStream.close();
            
        } catch (Exception ex) {
            System.out.println(chunkToSave.getX() + " " + chunkToSave.getY());
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Debugovacie metody k lahsiemu rozpoznaniu chyb
     */
    private void debuggingText() {
        System.out.println("Screen x-origin" + render.getScreenX() +
                "\n Screen y-origin" + render.getScreenY() +
                "\n Pixels inside one Chunk :"
                + "\n x:" + player.getRegX() +
                "\n y:" + player.getRegX() +
                "\n x translated :"+ render.getLastX() +
                "\n y translated :" + render.getLastY());
    }

    
    private void debuggingTiles(Collection<Tile> tiles) {
        
        for (Tile tile: tiles) {
            try {
                ImageIO.write((BufferedImage)tile.getImage(0), "jpg", new File("./"+tile.getName()+".jpg"));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
