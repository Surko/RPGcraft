/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map;

import rpgcraft.entities.Player;
import rpgcraft.entities.MovingEntity;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
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
import rpgcraft.graphics.inmenu.AbstractInMenu;
import rpgcraft.graphics.particles.Particle;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.chunks.ChunkContent;
import rpgcraft.map.tiles.*;
import rpgcraft.panels.AbstractMenu;
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
    
    /* 
     * mod pre vykreslenia svetla
     * 0 - Jednoduche vykreslenie len okolo hraca pomocou radiant brush
     * 
     */        
    private static final int LIGHT_MODE = 0;
    
    /**
     * mod pre renderovanie chunkov.
     * 0 - jednoduche renderovanie. Vyrenderuje vsetko v buffery a nasledne aj vykresli.
     * 1 - renderovanie okolo hraca. Vyrenderuje stvorec okolo hracovej pozicie s polomerom lightRadius pre hraca.
     * 2 - dalsie mozne renderovanie na doplnenie.
     */
    private static final int RENDER_MODE = 1;
    
    private final Logger LOG = Logger.getLogger(getClass().getName());
    
    public HashMap<Integer,Tile> tiles;
    
    protected String saveName;
    
    protected Queue<Chunk> chunkQueue = new LinkedList<> ();
    private Deque<Particle> partRemove = new ArrayDeque<>();
    private Deque<Entity> entityRemove = new ArrayDeque<>();
    
    private int numberOfChunks;
    Chunk[][] chunksToRender;    
    
    private int gameTime = 6 ;
    
    protected int x;
    protected int y;
    
    private final GamePane game;
    private AbstractInMenu menu;
    private final InputHandle input;
    public static final boolean defineTileself = true;
    public static final int chunkSize = 25;    
    
    private MapGenerator mapGen = new MapGenerator(Chunk.getSize(), Chunk.getDepth());
    
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
    
    private Graphics2D g2d;
    
    private int _lcurrent;
    private int _xcurrent;
    private int _ycurrent;
    
    private String xCoor;
    private String yCoor;
    
    private int xPixels;
    private int yPixels;
    
    private int screenX;
    private int screenY;
    
    private int width;
    private int height;
    
    private int lastX = 0;
    private int lastY = 0;    
    
    private double scaleParamX = 1;
    private double scaleParamY = 1;
    
    private Image lightingMap;
    private DayLighting dayLight;
    
    private int screenLength;
    
    private RadialGradientPaint rg;
    
    private boolean stat = false;  
    private boolean particle = true;
    private boolean scaleable = false;
    private boolean lighting = false;
    
    private static final Font coordinatesFont = new Font("Helvetica", Font.BOLD, 12);
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
        this.lightingMap = new BufferedImage(MainGameFrame.Fwidth, MainGameFrame.Fheight, BufferedImage.TRANSLUCENT); 
        this.chunksToRender = new Chunk[numberOfChunks][numberOfChunks];
    }
    
    public void initializeTiles() {
        // nacita zakladne dlazdice potrebne k fungovaniu hry      
        tiles = DefaultTiles.getInstance().createDefaultTiles();
        tiles.putAll(LoadedTiles.getInstance().createLoadedTiles(tiles));
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
                scale(g);
            }
        
            paintBackground(g);
            //paintFlora(g);
            paintEntitiesParticles(g);        
            paintLighting(g);
            
            if (menu != null) {
                menu.paintMenu(g);
            }
            
            if (stat) {
                paintStrings(g);
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
        if (menu != null) {
            menu.update();
        }
        updateLighting();      
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
        
        if (e == null) {
            xPixels = 0;
            yPixels = 0;

            if (stat) {            
                xCoor = null;
            }
        } else {            
            xPixels = e.getXPix() & 511;
            yPixels = e.getYPix() & 511;

            if (stat) {            
                xCoor =""+e.getXPix();
                yCoor =""+e.getYPix();
            }  
        }
        
        //debuggingText();
        
        screenX = (width - (screenLength)) / 2 + 256;
        screenY = (height - (screenLength)) / 2 + 256;
                             
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
            timeLighting();
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
    
    
    
    private void paintBackground(Graphics g) throws InterruptedException {
        try {
            
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);
            lastX = screenX - xPixels + 12;
            lastY = screenY - yPixels + 12;
            g.translate(lastX, lastY);      
            
            switch (RENDER_MODE) {
                case 0 : simpleChunkPainting(chunksToRender, g);
                    break;
                case 1 : playerChunkPainting(chunksToRender, g);
                    break;
            }
            g.translate(-lastX, -lastY);
        } catch (Exception e) {
            g.translate(-lastX, -lastY);
            Thread.sleep(delayThread);            
        }

    }
    
    private void paintFlora(Graphics g) {
        if (chunkState) {
            
        }
    }
    
    private void paintEntitiesParticles(Graphics g) {  
        g.translate(width /2, height/2);
        for (Entity e : entities) {   
           if (e.getLevel() == _lcurrent) {
               if (e instanceof Player) {                
                    g.drawImage(e.getTypeImage(), 0, 0, null);
                    continue;
               }
               int x = e.getXPix() - player.getXPix();
               int y = e.getYPix() - player.getYPix();
               if (Math.abs(x) < (player.getLightRadius() << 5) && (Math.abs(y) < (player.getLightRadius() << 5)))
                   g.drawImage(e.getTypeImage(), x, y,  null);
           }
        }
        
        try {
            for (Particle part: particles) {
                if (part.isActivated()) {
                g.drawImage(part.getImage(), part.getX(), part.getY(), null);            
                }
            }
        } catch (Exception e) {}
        g.translate(-width/2, -height/2);
    }
    
    private void paintLighting(Graphics g) { 
       if (lighting) {
            g.drawImage(lightingMap, 0, 0, null);
        } 
    }
    
    private void paintStrings(Graphics g) {
        g.setFont(coordinatesFont);
        g.setColor(fpsColor);
        if  (xCoor == null) {
            g.drawString(StringResource.getResource("_mplayer"), 0, height-50);
        } else {
            g.drawString("x : " + xCoor + " y : " + yCoor , width-125, height-50);
            g.drawString("ViewZ : " + _lcurrent + " PlayerZ : " + player.getLevel(), width - 125, height - 25);
        }
    }    
    
    private void scale(Graphics g) {
        g2d = (Graphics2D)g;
        g2d.scale(scaleParamX, scaleParamY);
    }
    
    private void playerChunkPainting(Chunk[][] _chunks, Graphics g) {
        int[][] _layer;
        int _chunkX, _chunkY;
        Tile toDraw;
        
        int Pminx = player.getTileX() - player.getLightRadius();
        int Pminy = player.getTileY() - player.getLightRadius();
        int Pmaxx = player.getTileX() + player.getLightRadius();
        int Pmaxy = player.getTileY() + player.getLightRadius();                
        
        
        for (int i = 0 ; i< numberOfChunks; i++) {
            for (int j = 0; j< numberOfChunks; j++) {
                if (_chunks[j][i]==null) {
                    continue;
                }                                
                
                _chunkX = i << 9;
                _chunkY = j << 9;
                
                int startX = Pminx - (i-1)*16;
                if (startX < 0) {
                    startX = 0;
                }
                int startY = Pminy - (j-1)*16;
                if (startY < 0) {
                    startY = 0;
                }
                int endX = Pmaxx - (i-1)*16;
                if (endX > Chunk.getSize()) {
                    endX = Chunk.getSize();
                }
                
                int endY = Pmaxy - (j-1)*16;                        
                if (endY > Chunk.getSize()) {
                    endY = Chunk.getSize();
                }
                
                if ((endX < 0)||(endY < 0)||(startX > Chunk.getSize())||(startY > Chunk.getSize())) {
                    continue;
                }
                
                _layer = _chunks[j][i].getLayer(_lcurrent);                 
                for (int _i = startX; _i< endX; _i++) {
                    for (int _j = startY; _j< endY; _j++) {  
                        //System.out.print(_layer[_i][_j]);
                        toDraw = tiles.get(_layer[_i][_j]);
                        if (toDraw == null) {
                            continue;
                        }

                        g.drawImage(toDraw.getImage(), (_i << 5) + _chunkX, (_j << 5) + _chunkY, null);
                    }
                }   
            }
        }
    }
    
    private void simpleChunkPainting(Chunk[][] _chunks, Graphics g) {  
        int[][] _layer;
        int _chunkX, _chunkY;
        Tile toDraw;
        
        for (int i = 0 ; i< numberOfChunks; i++) {
            for (int j = 0; j< numberOfChunks; j++) {
                if (_chunks[i][j]==null) {
                    continue;
                }
                _chunkX = i << 9;
                _chunkY = j << 9;
                                
                _layer = _chunks[i][j].getLayer(_lcurrent);
                for (int _i = 0; _i< Chunk.getSize(); _i++) {
                    for (int _j = 0; _j< Chunk.getSize(); _j++) {  
                        toDraw = tiles.get(_layer[_i][_j]);
                        if (toDraw == null) {
                            continue;
                        }

                        g.drawImage(toDraw.getImage(), (_i << 5) + _chunkX, (_j << 5) + _chunkY, null);
                    }
                }
            }
        }
    }
    
                                            
    public void setWidthHeight(Integer width, Integer height) {
        this.scaleParamX = width.doubleValue()/MainGameFrame.Fwidth;
        this.scaleParamY = height.doubleValue()/MainGameFrame.Fheight;        
        
        if (!scaleable) {            
            this.width = width;
            this.height = height;
            lightingMap = new BufferedImage(width, height, BufferedImage.TRANSLUCENT); 
            timeLighting();
        } else {
            this.width = MainGameFrame.Fwidth;
            this.height = MainGameFrame.Fheight;
            lightingMap = new BufferedImage(MainGameFrame.Fwidth, MainGameFrame.Fheight, BufferedImage.TRANSLUCENT); 
            timeLighting();
        }
        //
    }    
    
    public void setMenu(AbstractInMenu menu) {
        /*
        if (jammedMenu > 0) {
            jammedMenu--;
        } else {
        this.menu = menu;
        jammedMenu = 10;                
        }*/
        this.menu = menu;
    }
    
    public boolean hasMenu() {
        return menu != null ? true : false;
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
        return null;
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
        return null;
    }

    //
    
    public boolean addEntity(Entity e) {
        if (e instanceof Player) {
            this.player = (Player) e;                 
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
    
    /*
     * Testovacia funkcia pre generovanie map
     */    
    private int[][][] test() {
        int[][][] sk = new int[Chunk.getDepth()][Chunk.getSize()][Chunk.getSize()];
        
        for (int k=0;k<Chunk.getDepth();k++) {
            for (int i=0;i<Chunk.getSize();i++) {
                for (int j=0;j<Chunk.getSize();j++) {
                    if (j >8){
                        sk[k][i][j] = 2;
                    } else {
                        sk[k][i][j] = 1;
                    }
                }
            }  
        }
        
        return sk;
    }
    
    
    public void inputHandling() {
        if (menu != null) {
            menu.inputHandling();
        } else {
            if (player != null) {
                player.inputHandling();
            }
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
    
    private void timeLighting() {                         
                        
        dayLight.init(gameTime);                
        
        /*
        while (player == null) {
            try {
                Thread.sleep(10L);
            } catch (Exception e) {
                
            }
        }
        */
        
        if (lighting) {
        
            switch (LIGHT_MODE) {
                case 0 : radialLighting();
                    break;                            
            }   
            
        }
        
        }
       
    
    private void radialLighting() {
        g2d = (Graphics2D)lightingMap.getGraphics();
        
        int radius = player.getLightRadius() + dayLight.getRadiusBuff();
        rg = new RadialGradientPaint(width /2, height /2, radius, dayLight.getFractions(), dayLight.getColors() );               
    
        g2d.setPaint(rg);
                           
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
        g2d.fillRect(0, 0, width, height);
        
    }
    /**
     * 
     * @param x
     * @param y 
     */
    public void loadMap(int x, int y) {
        if (shiftChunk(chunkXYExist(x, y))) {
            return;
        }
        try {
            inputStream = new ObjectInputStream(new FileInputStream(
                    PathManager.getInstance().getWorldSavePath(saveName + PathManager.MAPS) + File.separator
                    + "region["+x+","+y+"].m")); 
            try {
                SaveChunk save = (SaveChunk) inputStream.readObject();

                saveOld(save.getChunk());
                ArrayList<Entity> entLoad = save.getEntities();
                if (entLoad != null) {
                    for (Entity e : entLoad) {                    
                        e.setSaved(false);
                        e.setMap(this);                    
                        e.setChunk(save.getChunk());
                        addEntity(e);
                    }
                }                
            } catch (Exception e) {
                LOG.log(Level.SEVERE, StringResource.getResource("_bsaveformat"));
                new MultiTypeWrn(null, Color.BLACK, StringResource.getResource("_bsaveformat"),null).renderSpecific(
                        StringResource.getResource("_label_badsave"));
            }

        } catch(Exception e) { 
            saveOld(new Chunk(new ChunkContent(test()), x, y));  
            
        }
    }
    
    // Ukladacie a nacitacie operacie s mapou (Chunk save/load)
    
    private void saveOld(Chunk chunk) {
        if (chunkQueue.size() > chunkSize) {
            saveMap(chunkQueue.poll());
            chunkQueue.add(chunk);
        } else {
            chunkQueue.add(chunk);             
        }
    }
    
    public void saveMap(int x, int y) {      
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(
                            PathManager.getInstance().getWorldSavePath(saveName + PathManager.MAPS)
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
                    PathManager.getInstance().getWorldSavePath(saveName + PathManager.MAPS) + File.separator
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
        System.out.println("Screen x-origin" + screenX +
                "\n Screen y-origin" + screenY +
                "\n Pixels inside one Chunk :"
                + "\n x:" + xPixels +
                "\n y:" + yPixels +
                "\n x translated :"+ lastX +
                "\n y translated :" + lastY);
    }

    
    private void debuggingTiles(Collection<Tile> tiles) {
        
        for (Tile tile: tiles) {
            try {
                ImageIO.write((BufferedImage)tile.getImage(), "jpg", new File("./"+tile.getName()+".jpg"));
            } catch (IOException ex) {
                LOG.log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
