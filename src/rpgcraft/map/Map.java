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
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import org.w3c.dom.Element;
import rpgcraft.GamePane;
import rpgcraft.MainGameFrame;
import rpgcraft.entities.*;
import rpgcraft.errors.CorruptedFile;
import rpgcraft.graphics.*;
import rpgcraft.graphics.inmenu.AbstractInMenu;
import rpgcraft.graphics.particles.Particle;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.Map;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.chunks.ChunkContent;
import rpgcraft.map.tiles.*;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.resource.TileResource;
/**
 *
 * @author Kirrie
 */

public class Map  {
    
    
    /* 
     * Pre zistenie kam ma zaradit mapu budem pouzivat bitove operacie posunu doprava o 4,
     * 0x0 - 15x15 budu v rovnakom priestore na disku pod menom region0x0
     */
    
    
    public HashMap<Integer,Tile> tiles = new HashMap<>();
    
    Queue<Chunk> chunkQueue = new LinkedList<> ();
    private Deque<Particle> partRemove = new ArrayDeque<>();
    private Deque<Entity> entityRemove = new ArrayDeque<>();
    
    private int numberOfChunks;
    Chunk[][] chunksToRender;    
    
    public int gameTime = 6 ;
    
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
    
    private volatile ArrayList<Entity> entities = new ArrayList<>();
    private volatile ArrayList<Particle> particles = new ArrayList<>();
    
    private Graphics2D g2d;
    
    private int _lcurrent;
    private int _xcurrent;
    private int _ycurrent;
    
    private int xCoor;
    private int yCoor;
    
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
    
    private static final Font coordinatesFont = new Font("Helvetica", Font.BOLD, 12);
    private static final int delayThread = 2;
    private static int jammedMenu = 0;
    
    public Map(GamePane game, InputHandle input) {
        this.game = game; 
        this.input = input;     
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
        debuggingTiles(tiles.values());
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
                Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
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
            _xcurrent = e.getRegX();
            _ycurrent = e.getRegY();
            _lcurrent = e.getLevel();
            
            
            for (int i = 0; i < numberOfChunks; i++) {
                for (int j = 0; j < numberOfChunks; j++) {
                    chunksToRender[i][j] = chunkXYExist(_xcurrent-(numberOfChunks / 2) + j, _ycurrent - (numberOfChunks / 2) + i);
                }
            }
                        
            chunkState = false;
        }
        
        //debuggingText();
        
        screenX = (width - (screenLength)) / 2 + 256;
        screenY = (height - (screenLength)) / 2 + 256;
        
        xPixels = e.getXPix() & 511;
        yPixels = e.getYPix() & 511;
        
        if (stat) {            
            xCoor = e.getXPix();
            yCoor = e.getYPix();
        }               
    }
    
    private void updateEntities() {
        for (Entity e: entities) {
            if (!e.update()) {
                entityRemove.add(e);
            }
        }
        
        if (!entityRemove.isEmpty()) entities.remove(entityRemove.pop());
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
            
        if (!partRemove.isEmpty()) particles.remove(partRemove.pop());
    }
    
    
    
    private void paintBackground(Graphics g) throws InterruptedException {
        try {
            
            lastX = screenX - xPixels + 12;
            lastY = screenY - yPixels + 12;
            g.translate(lastX, lastY);                         
            chunksToPaint(chunksToRender, g);
            g.translate(-lastX, -lastY);
        } catch (Exception e) {
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
           if (e instanceof Player) {
                g.drawImage(e.getTypeImage(), 0, 0, null);
                continue;
            }       
           g.drawImage(e.getTypeImage(), e.getXPix()- player.getXPix(), e.getYPix() - player.getYPix(),  null);
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
       g.drawImage(lightingMap, 0, 0, null); 
    }
    
    private void paintStrings(Graphics g) {
        g.setFont(coordinatesFont);
        g.setColor(fpsColor);
        g.drawString("x : " + xCoor + "\n y : " + yCoor , width-100, height-50);
    }    
    
    private void scale(Graphics g) {
        g2d = (Graphics2D)g;
        g2d.scale(scaleParamX, scaleParamY);
    }
    
    public void chunksToPaint(Chunk[][] _chunks, Graphics g) {  
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
            return entities.add(e);            
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
                    if ((i == 0) && (j == 0) ){
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
            player.inputHandling();
        }
        
        if (input.stat.click) {
            stat = !stat;
            input.stat.click = false;
        }
        if (input.escape.click) {
            entities.clear();
            game.setMenu(AbstractMenu.getMenuByName("mainmenu"));
            input.escape.click = false;
        }
        
        if (input.particles.click) {
            particle = !particle;
            input.particles.click = false;
        }
        
        if (input.scaling.click) {
            scaleable = !scaleable;
            setWidthHeight(game.getWidth(), game.getHeight());
            input.scaling.click = false;
        }
    }

    public void setLightState(boolean state) {
        this.lightState = state;
    }
    
    
    
    public ArrayList<Entity> getEntities() {
        return entities;
    }
    
    private void timeLighting() {         
        
        g2d = (Graphics2D)lightingMap.getGraphics();
                        
        dayLight.init(gameTime);                
        
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
        if (shiftChunk(chunkXYExist(x, y))) return;
        try {
            inputStream = new ObjectInputStream(new FileInputStream(PathManager.getInstance().getWorldPath() + 
                ("region["+ x +"."+ y +"].m")));
            saveOld((Chunk)inputStream.readObject());
            
            try {
                // Z mapy nacita List s ulozenymi entitami a prida ich k terajsim entitam.
                ArrayList<Entity> entLoad = (ArrayList<Entity>)inputStream.readObject();
                for (Entity e : entLoad) {
                    e.setSaved(false);
                    entities.add(e);
                }
            } catch (Exception e) {
                
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
                        PathManager.getInstance().getWorldPath() +
                        "region["+ x + "." + y + "].m"));
            for (Chunk chunk : chunkQueue) {
                if ((chunk.getX() == x)&&(chunk.getY() == y)) {
                    outputStream.writeObject(chunk);
                    //outputStream.writeObject(chunk);
                    
                    // Objekt s entitami, ktory ulozime k Chunku.
                    ArrayList<Entity> entSave = new ArrayList<>();
                    for (Entity e : entities) {
                        if ((e.getRegX() == x )&&(e.getRegY() == y)) {
                            entSave.add(e);                        
                            e.setSaved(true);
                        }
                    }
                    outputStream.writeObject(entSave);
                    outputStream.close();
                }
            }
        } catch(Exception ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void saveMap(Chunk chunkToSave) {
        try {
            int chunkX = chunkToSave.getX();
            int chunkY = chunkToSave.getY();
            outputStream = new ObjectOutputStream(new FileOutputStream(
                    PathManager.getInstance().getWorldPath() +
                    "region["+ chunkX + "." + chunkY + "].m"));
            outputStream.writeObject(chunkToSave);
            //outputStream.writeObject(chunkToSave);
            
            // Objekt s entitami, ktory ulozime k Chunku.
            ArrayList<Entity> entSave = new ArrayList<>();
            for (Entity e : entities) {
                if ((e.getRegX() == chunkX )&&(e.getRegY() == chunkY)) {
                    entSave.add(e);
                    e.setSaved(true);                    
                }
            }
            outputStream.writeObject(entSave);
            outputStream.close();
            
        } catch (Exception ex) {
            Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
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
                Logger.getLogger(Map.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
}
