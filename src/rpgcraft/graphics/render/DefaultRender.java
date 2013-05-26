/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.render;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RadialGradientPaint;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import rpgcraft.MainGameFrame;
import rpgcraft.entities.Entity;
import rpgcraft.entities.MovingEntity;
import rpgcraft.entities.Player;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.DayLighting;
import rpgcraft.graphics.spriteoperation.Sprite;
import rpgcraft.graphics.ui.particles.Particle;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.tiles.DefaultTiles;
import rpgcraft.map.tiles.Tile;
import rpgcraft.plugins.RenderPlugin;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.MainUtils;

/** 
 * Defaultna trieda pre vykonanie renderu pri vykreslovani mapy. Zahrna v sebe zakladne vykreslovanie pozadia, entity,
 * svetelnosti, castic ktore podedila. Trieda dedi od RenderPlugin co je zaklad pre kazdy renderovaci plugin. Na ziskanie 
 * instancie volame staticku metodu getInstance, ktora vytvori objekt a ulozi ho do
 * hashmapy nachadzajucej sa v triede RenderPlugin (pre vykonanie musi davat metoda getName meno tohoto renderu). 
 * @author kirrie
 */
public final class DefaultRender extends RenderPlugin {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final String NAME = "default";    
    private static final Font coordinatesFont = new Font("Helvetica", Font.BOLD, 12);
    private static DefaultRender instance;        
    
    /**
     * mod pre renderovanie chunkov.
     * 0 - jednoduche renderovanie. Vyrenderuje vsetko v buffery a nasledne aj vykresli.
     * 1 - renderovanie okolo hraca. Vyrenderuje stvorec okolo hracovej pozicie s polomerom lightRadius pre hraca.
     * 2 - dalsie mozne renderovanie na doplnenie.
     */
    private static final int RENDER_MODE = 1;
    /* 
     * mod pre vykreslenia svetla
     * 0 - Jednoduche vykreslenie len okolo hraca pomocou radiant brush
     * 
     */        
    private static final int LIGHT_MODE = 0; 
    
    /**
     * Graficka pomocna premenna.
     * Uklada sa vnom vacsinou skalovatelna instancia normalneho Graphics     
     */
    private Graphics2D g2d; 
    /**
     * textColor - Farba textu ktory vykreslujeme
     */
    private Color textColor = Colors.getColor(Colors.fpsColor);
    
    /**
     * Entita okolo ktorej kreslime, ine pluginy hu vobec nemusia mat
     */
    private MovingEntity entity;
    
    /**
     * Translacne body. Kazdym prechodom paint metod sa tieto hodnoty prenastavia a znova vynuluju
     */
    private int lastX = 0;
    private int lastY = 0;  
    
    /**
     * Skalovacie parametre pre zvacsenie okna
     */
    private double scaleParamX = 1;
    private double scaleParamY = 1;
        
    /**
     * x-ova a y-ova pozicia okna kam sa kresli 
     */
    private int screenX;
    private int screenY;
    
    /**
     * Sirka a vyska priestoru do ktoreho kreslime
     */
    private int width,height;
    /**
     * Svetelna mapa.
     */
    private Image lightingMap;    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    private DefaultRender() {
        
    }
    
    private DefaultRender(SaveMap map) {        
        this.map = map;
        this.entity = map.getPlayer();        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">    
    public static DefaultRender getInstance() {
        if (instance == null) {
            instance = new DefaultRender();
            setRender(instance);
        }        
        return instance;
    }
    // </editor-fold>
           
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    
    @Override
    public void paintBackground(Graphics g, Chunk[][] chunksToRender) throws InterruptedException {
        try {            
            g.setColor(Color.BLACK);
            g.fillRect(0, 0, width, height);              
            lastX = screenX - (entity.getXPix()&511);
            lastY = screenY - (entity.getYPix()&511);                    
            
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
            //Thread.sleep(delayThread);            
        }

    }
    
    @Override
    public void paintFlora(Graphics g) {
        if (map.getChunkState()) {
            
        }
    }
    
    @Override
    public void paintEntitiesParticles(Graphics g, ArrayList<Entity> entities, ArrayList<Particle> particles) {  
        g.translate(width /2, height/2);
        for (Entity e : entities) {   
           if (e.getLevel() == map.getLevel()) {
               if (e == entity) {                
                    g.drawImage(e.getTypeImage(), 0, 0, null);
                    continue;
               }               
               int x = e.getXPix() - entity.getXPix();
               int y = e.getYPix() - entity.getYPix();
               if (Math.abs(x) < (entity.getLightRadius() << 5) && (Math.abs(y) < (entity.getLightRadius() << 5)))
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
    
    /**
     * Metoda paintLighting ako meno napoveda vykresli do mapy osvetlenie.
     * Parameter typu graphics sluzi na prenos grafickeho kontextu do tejto metody.
     * Do tohoto kontextu vykreslujeme svetelnu mapu ktoru nastavujeme v metodach 
     * makeLightingMap.
     * @param g Graficky kontext do ktoreho vykreslujeme texty.     
     */
    @Override
    public void paintLighting(Graphics g) {        
        g.drawImage(lightingMap, 0, 0, null);         
    }
    
    /**
     * Metoda paintStrings ako meno napoveda vykresli vsetky texty ktore budu v mape.
     * Parameter typu graphics sluzi na prenos grafickeho kontextu do tejto metody.
     * V metode nastavime typ fontu, farbu textu a nasledne vykreslime text o suradniciach hraca.
     * @param g Graficky kontext do ktoreho vykreslujeme texty.
     */
    @Override
    public void paintStrings(Graphics g) {
        g.setFont(coordinatesFont);
        g.setColor(textColor);
        if  (entity == null) {
            g.drawString(StringResource.getResource("_mplayer"), 0, height-50);
        } else {
            g.drawString("x : " + entity.getXPix() + " y : " + entity.getYPix() , width-125, height-50);
            g.drawString("ViewZ : " + map.getLevel() + " PlayerZ : " + entity.getLevel(), width - 125, height - 25);
        }
    }    
    
    /**
     * Metoda ktora skaluje graficky kontext zadany parametrom <b>g</b>.
     * Skalovatelny kontext ukladame do Graphics2D ktory dokaze pracovat s affinymi transformaciami
     * a meteodami na skalovanie kontextu. Nevadi nam ani to ze ostatne metody pracuju so samostatnym Graphics pretoze
     * Graphics2D v sebe uchovava originalny odkaz na tento Graphics a pri skalovani
     * vlastne zvacsujeme/zmensujeme ten originalny.
     * @param g Graficky kontext ktory skalujeme.
     */
    @Override
    public void scale(Graphics g) {
        g2d = (Graphics2D)g;
        g2d.scale(scaleParamX, scaleParamY);
    }
    
    private void playerChunkPainting(Chunk[][] _chunks, Graphics g) {
        int[][] upperLayer, upperMeta, lowerLayer, lowerMeta;
        int _chunkX, _chunkY;
        Tile toDraw;
        int numberOfChunks = map.getChunksSize();
        int level = map.getLevel();
        
        int Pminx = entity.getTileX() - entity.getLightRadius();
        int Pminy = entity.getTileY() - entity.getLightRadius();
        int Pmaxx = entity.getTileX() + entity.getLightRadius();
        int Pmaxy = entity.getTileY() + entity.getLightRadius();                        
        
        for (int y = 0 ; y< numberOfChunks; y++) {
            for (int x = 0; x< numberOfChunks; x++) {
                if (_chunks[y][x]==null) {
                    continue;
                }                                
                
                _chunkX = x << 9;
                _chunkY = y << 9;
                
                int startX = Pminx - (x-1)*16;
                if (startX < 0) {
                    startX = 0;
                }
                int startY = Pminy - (y-1)*16;
                if (startY < 0) {
                    startY = 0;
                }
                int endX = Pmaxx - (x-1)*16;
                if (endX > Chunk.getSize()) {
                    endX = Chunk.getSize();
                }
                
                int endY = Pmaxy - (y-1)*16;                        
                if (endY > Chunk.getSize()) {
                    endY = Chunk.getSize();
                }
                
                if ((endX < 0)||(endY < 0)||(startX > Chunk.getSize())||(startY > Chunk.getSize())) {
                    continue;
                }
                
                lowerLayer = _chunks[y][x].getLayer(level - 1);                 
                lowerMeta = _chunks[y][x].getMetaDataLayer(level - 1);
                upperLayer = _chunks[y][x].getLayer(level);
                upperMeta = _chunks[y][x].getMetaDataLayer(level);
                
                for (int _y = startY; _y< endY; _y++) {
                    for (int _x = startX; _x< endX; _x++) {  
                        //System.out.print(_layer[_i][_j]);                                                
                        
                        if (upperLayer[_x][_y] != DefaultTiles.BLANK_ID) {
                            toDraw = Tile.tiles.get(upperLayer[_x][_y]); 
                            if (toDraw != null) {
                                g.drawImage(toDraw.getUpperImage(), (_x << 5) + _chunkX, (_y << 5) + _chunkY, null);
                            }
                        } else {                            
                            toDraw = Tile.tiles.get(lowerLayer[_x][_y]);
                            if (toDraw != null) {                      
                                g.drawImage(toDraw.getImage(lowerMeta[_x][_y]), (_x << 5) + _chunkX, (_y << 5) + _chunkY, null);
                            }                                                        
                        }                                                
                    }
                }   
            }
        }
    }        
    
    private void simpleChunkPainting(Chunk[][] _chunks, Graphics g) {  
        int[][] upperLayer,upperMeta,lowerLayer,lowerMeta;
        int _chunkX, _chunkY;
        Tile toDraw;
        int numberOfChunks = map.getChunksSize();
        int level = map.getLevel();
        
        for (int y = 0 ; y< numberOfChunks; y++) {
            for (int x = 0; x< numberOfChunks; x++) {
                if (_chunks[y][x]==null) {
                    continue;
                }
                _chunkX = x << 9;
                _chunkY = y << 9;
                                
                upperLayer = _chunks[y][x].getLayer(level);
                upperMeta = _chunks[y][x].getMetaDataLayer(level);
                lowerLayer = _chunks[y][x].getLayer(level - 1);
                lowerMeta = _chunks[y][x].getMetaDataLayer(level - 1);
                for (int _y = 0; _y< Chunk.getSize(); _y++) {
                    for (int _x = 0; _x< Chunk.getSize(); _x++) {  
                        if (upperMeta[_x][_y] != Sprite.Type.TILEDESTROYED.getValue()) {
                            toDraw = Tile.tiles.get(upperLayer[_x][_y]); 
                            if (toDraw == null) {
                                continue;
                            } else {                        
                                g.drawImage(toDraw.getImage(upperMeta[_x][_y]), (_x << 5) + _chunkX, (_y << 5) + _chunkY, null);
                            }
                        } else {
                            toDraw = Tile.tiles.get(lowerLayer[_x][_y]);
                            if (toDraw == null) {
                                continue;
                            } else {                        
                                g.drawImage(toDraw.getImage(lowerMeta[_x][_y]), (_x << 5) + _chunkX, (_y << 5) + _chunkY, null);
                            }
                        }
                    }
                }
            }
        }
    }
            
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Lighting metody">
    @Override
    public void makeLightingMap(DayLighting dayLight) {                                                                         
        switch (LIGHT_MODE) {
            case 0 : radialLighting(dayLight);
                break;                            
        }                               
    }
    
    private void radialLighting(DayLighting dayLight) {
        g2d = (Graphics2D)lightingMap.getGraphics();
        
        int radius = entity.getLightRadius() + dayLight.getRadiusBuff();
        RadialGradientPaint rg = new RadialGradientPaint(width /2, height /2, radius, dayLight.getFractions(), dayLight.getColors() );               
    
        g2d.setPaint(rg);
                           
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
        g2d.fillRect(0, 0, width, height);        
    }
    
    // </editor-fold>
        
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    @Override
    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;
        if (map.hasLighting()) {
            lightingMap = new BufferedImage(MainGameFrame.Fwidth, MainGameFrame.Fheight, BufferedImage.TRANSLUCENT); 
            makeLightingMap(map.getDayLighting());
        }
    }

    @Override
    public void setMap(SaveMap saveMap) {
        this.map = saveMap;
        this.entity = saveMap.getPlayer();        
    }
    
    @Override
    public void setScaleParams(double scaleX, double scaleY) {
        this.scaleParamX = scaleX;
        this.scaleParamY = scaleY;
    }
        
    @Override
    public void setScreenX(int screenX) {
        this.screenX = screenX + 12;
    }

    @Override
    public void setScreenY(int screenY) {
        this.screenY = screenY + 12;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    @Override
    public String getName() {        
        return NAME;
    }
    
    @Override
    public int getScreenX() {
        return screenX;
    }

    @Override
    public int getScreenY() {
        return screenY;
    }

    @Override
    public int getLastX() {
        return lastX;
    }

    @Override
    public int getLastY() {
        return lastY;
    }
    // </editor-fold>
}
