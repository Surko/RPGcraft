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
import rpgcraft.entities.Entity;
import rpgcraft.entities.MovingEntity;
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
    private int lightingMapX, lightingMapY;
    
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
    /**
     * Vytvorenie instancie DefaultRenderu. Kedze je to privatny konstruktor
     * tak sa da volat len zvnutra tejto triedy.
     */
    private DefaultRender() {
        
    }
    
    /**
     * Vytvorenie instancie DefaultRenderu. Kedze je to privatny konstruktor
     * tak sa da volat len zvnutra tejto triedy a to s parametrom <b>map</b>, z ktoreho
     * si vyberieme hracovu entitu.
     * @param map 
     */
    private DefaultRender(SaveMap map) {        
        this.map = map;
        this.entity = map.getPlayer();        
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Staticke metody "> 
    /**
     * Staticka metoda ktora vrati instanciu DefaultRenderu ako singleton triedu.     
     * @return Instancia DefaultRender.
     */
    public static DefaultRender getInstance() {
        if (instance == null) {
            instance = new DefaultRender();           
        }        
        return instance;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    /**
     * Metoda ktora vykresluje dlazdice do grafickeho kontextu zadaneho parametrom <b>g</b>.
     * Vykreslujeme iba tie dlazdice ktore sa nachdazaju v parametri <b>chunksToRender</b>.
     * Prekreslovanie prebieha tak ze ked sa hybeme hracom tak s nim nepohybujeme 
     * ale pohybujeme mapou (mapa ma 512 x 3 po kazdom smere => musime spravit taky translate
     * aby dlazdica pod hracom bola na pozicii [0,0]). Nato sluzi translate ktory prevadzame pred samotnym kreslenim dlazdic.
     * Nasledne volame metodu simple alebo playerChunkPainting (podla toho aky mod renderovania
     * mame zapaty). Nakonci urobime spatne translate na zaciatocne pozicie. Pri chybe tiez vraciama translate
     * naspat.
     * @param g Graficky kontext do ktoreho kreslime
     * @param chunksToRender Chunky ktore kreslime.
     * @throws InterruptedException 
     */
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
    
    /**
     * Metoda ktora vykresluje floru. Zatial nevyuzite. Moznost doplnit v buducnosti.
     * @param g Graficky kontext do ktoreho kreslime floru.
     */
    @Override
    public void paintFlora(Graphics g) {
        if (map.getChunkState()) {
            
        }
    }
    
    /**
     * Metoda ktora vykresli entity aj castice do grafickeho kontextu zadaneho parametrom
     * <b>g</b>. Entity na vykreslenie dostavame parametrom <b>entities</b> a casti parametrom
     * <b>particles</b>. Nazaciatku robime translate grafickeho kontextu aby sme mali stred
     * celeho okna na zaciatocnej pozicii [0,0]. Potom prechadzame entitami a vykreslujeme ich
     * podla vzdialenosti od hracovej entity. Ked narazime na hraca tak ho vykreslime na
     * poziciu [0,0]. Ked je vzdialenost entit mensia ako lightRadius hracovej entity
     * tak hu vykreslime inak nie. Castice vykreslujeme nad hracovu entitu. Nakonci
     * naspat spravime translate ako to bolo pred translatom
     * @param g Graficky kontext do ktoreho kreslime
     * @param entities Entity ktore vykreslujeme
     * @param particles Castice ktore vykreslujeme
     */
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
        g.drawImage(lightingMap, lightingMapX, lightingMapY, null);         
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
    
    /**
     * 
     * @param _chunks
     * @param g 
     */
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
    
    /**
     * 
     * @param _chunks
     * @param g 
     */
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
    /**
     * Metoda ktora vytvori svetelnu mapu z parametru <b>dayLight</b>, ktory urcuje
     * ako mame vykreslovat svetlo. Vyberieme si aky svetelny mod mame aktivovany 
     * a zavolame metodu. Kedze mame len jeden mod tak volame metodu radialLighting
     * s parametrom dayLight
     * @param dayLight DayLighting s aktualnym casom a farbami ake sa vyuzivaju pri osvetlovani.
     */
    @Override
    public void makeLightingMap(DayLighting dayLight) {                                                                         
        switch (LIGHT_MODE) {
            case 0 : radialLighting(dayLight);
                break;                            
        }                               
    }
    
    /**
     * Metoda ktora vykona radialne osvetlenie pomocou RadialGradientPaint. 
     * Pri neexistencii svetelnej mapy hu vytvarame. Nasledne vyberieme z playera 
     * dlzku pokial dosiahne svetlo a prenastavime ho podla udajov ktore su definovane
     * v DayLighting objekte <b>dayLight</b>. Nakonci vyplnime iba taky obdlznik s gradientom
     * aky je potreba, kedze svetelnu mapu vytvarame podla lightRadius hracovej entity
     * (iba bezprostredne okolie hraca ktore je vykreslovane).     
     * @param dayLight DayLighting objekt s aktualnymi nastaveniami farieb.
     */
    private void radialLighting(DayLighting dayLight) {
        int lTileRadius = (entity.getLightRadius() + 1) << 5;
         if (lightingMap == null || lightingMap.getWidth(null) != 2 * lTileRadius) {
        lightingMap = new BufferedImage(2 * lTileRadius,
                2 * lTileRadius, BufferedImage.TRANSLUCENT);
        lightingMapX = (width - lightingMap.getWidth(null))/2;
        lightingMapY = (height - lightingMap.getHeight(null))/2;
        }                 
        int radius = (entity.getLightRadius() << 5) + dayLight.getRadiusBuff();                
        g2d = (Graphics2D)lightingMap.getGraphics();
        RadialGradientPaint rg = new RadialGradientPaint(lightingMap.getWidth(null)/ 2 ,
                lightingMap.getHeight(null)/2 , radius, dayLight.getFractions(), dayLight.getColors() );               
    
        g2d.setPaint(rg);
                           
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC, 1.0f));
        g2d.fillRect(0, 0, 2 * lTileRadius, 2 * lTileRadius);        
    }
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi sirku a vysku v akej renderujeme/vykreslujeme.
     * @param width Sirka vykreslovacieho priestoru
     * @param height Vyska vykreslovacieho priestoru
     */
    @Override
    public void setWidthHeight(int width, int height) {
        this.width = width;
        this.height = height;   
        if (lightingMap != null) {
            lightingMapX = (width - lightingMap.getWidth(null))/2;
            lightingMapY = (height - lightingMap.getHeight(null))/2;
        }
    }

    /**
     * Metoda ktora nastavi renderu mapu ktoru vykresluje
     * @param saveMap Mapa ktoru vykreslujeme.
     */
    @Override
    public void setMap(SaveMap saveMap) {
        this.map = saveMap;
        this.entity = saveMap.getPlayer();        
    }
    
    /**
     * Metoda ktora nastavuje skalovatelne parametre scaleX a scalY podla parametrov.
     * @param scaleX Skalovatelny modifikator po x-ovej osi
     * @param scaleY Skalovatelny modifikator po y-ovej osi
     */
    @Override
    public void setScaleParams(double scaleX, double scaleY) {
        this.scaleParamX = scaleX;
        this.scaleParamY = scaleY;
    }
        
    /**
     * Metoda ktora nastavi x-ovu poziciu obrazovky
     * @param screenX X-ova pozicia obrazovky
     */
    @Override
    public void setScreenX(int screenX) {
        this.screenX = screenX + 16;
    }

    /**
     * Metoda ktora nastavi y-ovu poziciu obrazovky
     * @param screenY Y-ova pozicie obrazovky
     */
    @Override
    public void setScreenY(int screenY) {
        this.screenY = screenY + 16;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati meno renderovacieho pluginu.
     * @return Meno pluginu
     */
    @Override
    public String getName() {        
        return NAME;
    }
    
    /**
     * Metoda ktora vrati x-ovu poziciu obrazovky
     * @return X-ova pozicia obrazovky
     */
    @Override
    public int getScreenX() {
        return screenX;
    }

    /**
     * Metoda ktora vrati y-ovu poziciu obrazovky
     * @return Y-ova pozicia obrazovky
     */
    @Override
    public int getScreenY() {
        return screenY;
    }

    /**
     * Metoda ktora vrati posledne nastavenu x-ovu translaciu
     * @return X-ova translacia
     */
    @Override
    public int getLastX() {
        return lastX;
    }

    /**
     * Metoda ktora vrati posledne nastavenu y-ovu translaciu.
     * @return Y-ova translacia
     */
    @Override
    public int getLastY() {
        return lastY;
    }
    // </editor-fold>
}
