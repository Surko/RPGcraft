/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Deque;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import rpgcraft.GamePane;
import rpgcraft.MainGameFrame;
import rpgcraft.effects.Effect;
import rpgcraft.entities.Entity;
import rpgcraft.entities.MovingEntity;
import rpgcraft.entities.Player;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.Colors;
import rpgcraft.graphics.DayLighting;
import rpgcraft.graphics.ui.particles.Particle;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.map.chunks.ChunkContent;
import rpgcraft.map.generators.MapGenerator;
import rpgcraft.map.tiles.Tile;
import rpgcraft.panels.GameMenu;
import rpgcraft.plugins.RenderPlugin;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.GameUtils.Spawn;
import rpgcraft.utils.MainUtils;


/**
 * <p>
 * Trieda SaveMap je zakladna trieda pre zobrazovanie mapy a vsetkeho co je nanej.
 * Pre zobrazovanie vyuzivame renderovaci plugin ulozeny v premennej <b>render</b>.
 * Takisto v tejto triede aktualizujeme ako entity tak aj castice a chunky ktore budeme
 * zobrazovat v renderi. Trieda implementuje Runnable keby trebalo doplnit viac vlaknovy.
 * </p>
 * <p>
 * Dalsou vlastnostou tejto triedy je ta ze zdruzuje vsetky metody na ukladanie a nacitavanie
 * chunkov ako aj staranie sa kedy a ako sa maju tieto chunky ukladat.
 * </p>
 * <p>
 * Zvykom je tuto mapu posielat kazdej entite aby mohla entita pristupovat 
 * k metodam nacitania ako aj ku getterom na ziskanie ostatnych entit, atd...
 * </p>
 * @author Kirrie
 */
public class SaveMap implements Runnable {                  

    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /* 
     * Pre zistenie kam ma zaradit mapu budem pouzivat bitove operacie posunu doprava o 4,
     * 0x0 - 15x15 budu v rovnakom priestore na disku pod menom region0x0
     */             
    
    /**
     * Logger pre Mapu
     */
    private final Logger LOG = Logger.getLogger(getClass().getName());       
    
    /**
     * Nazov ulozeneej pozicie
     */
    protected String saveName;
    
    /**
     * Fronta s nacitanymi chunkami
     */
    protected Deque<Chunk> chunkQueue = new LinkedList<> ();
    /**
     * Fronta s casticami na vymazanie
     */
    private Deque<Particle> partRemove = new ArrayDeque<>();
    /**
     * Fronta s entitami na vymazanie
     */
    private Deque<Entity> entityRemove = new ArrayDeque<>();
    
    /**
     * Pocet chunkov po x-ovej alebo y-ovej suradnici
     */
    private int numberOfChunks;
    /**
     * 2D pole s chunkami na vykreslenie. Ich pocet je numberOfChunk*numberOfChunks
     */
    private Chunk[][] chunksToRender;    
    
    /**
     * Renderovaci plugin ktorym vykreslujeme svet
     */
    private RenderPlugin render;
    
    /**
     * Ako casto sa vytvaraju entity. Cislo znamena priemerne (v sekundach) ako casto sa vytvori nova entita
     */
    protected int spawnRate = 50;   
    /**
     * Maximalny pocet spawnov
     */
    protected int maxSpawns = 5;
    
    /**
     * Aktualny cas v hre
     */
    protected short dayTime;    
    /**
     * DayLighting instancia pre zobrazenie svetla
     */
    protected DayLighting dayLight;
    /**
     * Posledna aktualizovana sekunda
     */
    protected long lastSecond;
    /**
     * Aktualna hodina v hre (6:00)
     */
    protected int gameTime = 6;
    
    /**
     * X-ova pozicia ktoru mozno vyuzit na nieco
     */
    protected int x;
    /**
     * Y-ova pozicia ktoru mozno vyuzit na nieco
     */
    protected int y;
    
    /**
     * Panel v ktorom je mapa
     */
    private final GamePane game;  
    /**
     * menu v ktorom je mapa
     */
    private final GameMenu menu;
    /**
     * Vstup od uzivatela
     */
    private final InputHandle input;
    /**
     * Ci sa dlazdiciam urcuju id sami od seba
     */
    public static final boolean defineTileself = true;
    /**
     * Pocet chunkov ulozenych vo fronte s chunkami
     */
    public static final int chunkSize = 25;            
    
    /**
     * Aktualny hrac
     */
    public Player player;   
    /**
     * Originalny hrac
     */
    public MovingEntity origPlayer;
    
    /**
     * Farba FPS
     */
    private Color fpsColor = Colors.getColor(Colors.fpsColor);
       
    // Stavy hry
    private boolean lightState = true;
    private boolean chunkState = true;
    private boolean playerState = true;
    private boolean paused = false;
    private boolean running = true;
    
    /**
     * InputStream na nacitanie vlastnosti hry ako chunky a entity,...
     */
    private ObjectInputStream inputStream;
    /**
     * OutputStream pre zapisanie vlastnosti ako chunky a entity,...
     */
    private ObjectOutputStream outputStream;
    
    /**
     * Aktivne efekty v mape
     */
    protected ArrayList<Effect> onSelfEffects;
    /**
     * List s blizkymi entitami od hraca
     */
    protected volatile ArrayList<Entity> nearEntities = new ArrayList<>();
    /**
     * List s entitami v chunkoch
     */
    protected volatile ArrayList<Entity> entitiesList = new ArrayList<>();
    // Entity na poziciach protected volatile HashMap<EntityPosition, ArrayList<Entity>> entities;
    /**
     * List s aktivnymi casticami
     */
    protected volatile ArrayList<Particle> particles = new ArrayList<>();
    
    //protected volatile HashMap<Integer,Entity> tileEntities = new HashMap<>();
    
    /**
     * Graphics2D objekt
     */
    private Graphics2D g2d;
    
    /**
     * Aktualne podlazdie
     */
    private int _lcurrent;
    /**
     * Aktualna x-ova pozicia
     */
    private int _xcurrent;
    /**
     * Aktualna y-ova pozicia
     */
    private int _ycurrent;
                 
    /**
     * Sirka a vyska okna
     */
    private int width,height;
    /**
     * X-ova a Y-ove koordinaty obrazovky dlzky screenLength
     */
    public int xCoordinate,yCoordinate;
    
    /**
     * Dlzka okna
     */
    private int screenLength;    
    
    /**
     * Premenne urcujuce co sa moze aktualizovat a vykreslovat
     */
    private boolean stat = false,particle = true,scaleable = false,lighting = false;
        
    private static final int delayThread = 2;
    private static int jammedMenu = 0;
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor pre vytvorenie mapy v ktorej zobrazujeme a aktualizujeme vnutorne elementy ako su
     * entity, castice, svetlo atd... . V konstruktore inicializujeme aky input pouzivame
     * aku velkost ma mapa a vytvarame listy v ktorych budu entity a ostatne objekty.
     * Taktiez nastavujeme pocet chunkov kolko mozme nacitat do 2-dim pola.
     * Nakonci nastavujeme co za render plugin budeme pouzivat. Ked nenajdeme nami zadany 
     * tak pouzivame zakladny DefaultPlugin.
     * @param save Save s ktorym je prepojena mapa
     */
    public SaveMap(Save save) {
        //this.entities = new HashMap<>();
        this.menu = save.menu;
        this.game = save.game; 
        this.running = true;        
        this.input = save.input;
        this.saveName = save.saveName;
        this.width = game.getWidth();
        this.height = game.getHeight();
        this.dayLight = new DayLighting(gameTime);
        this.nearEntities = new ArrayList<>();
        this.onSelfEffects = new ArrayList<>();
        this.numberOfChunks = 3;
        this.lighting = true;
        this.screenLength = numberOfChunks << 9;        
        this.chunksToRender = new Chunk[numberOfChunks][numberOfChunks];
        render = RenderPlugin.loadRender("sk.jar");
        render.setMap(this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Inicializacie ">
    /**
     * Metoda ktora zinicializuje dlazdice pomocou metody initializeTiles v triede Tile.
     */
    public void initializeTiles() {
        // nacita zakladne dlazdice potrebne k fungovaniu hry      
        Tile.initializeTiles();
        //debuggingTiles(tiles.values());
    }    
                
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Zakladne vykreslovanie ">
    /**
     * Metoda paint ktora sa stara o vykreslenie mapy do grafickeho kontextu ktory je zadany
     * ako parameter <b>g</b>. Vykreslovanie spociva s pozadia, entit a castic, svetla
     * textov. Vsetky vykreslovania ma na starosti render ktory sme nastavili (alebo ktory mozme
     * zmenit pomocou akcii).
     * @param g Graficky kontext do ktoreho kreslime.
     */
    public void paint(Graphics g) {  
        try {
            if (scaleable) {
                render.scale(g);
            }
        
            render.paintBackground(g, chunksToRender);
            //paintFlora(g);
            render.paintEntitiesParticles(g, entitiesList, particles);   
            
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
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Update metody ">
    /**
     * Metoda ktora vykona update/aktualizaciu vsetkych dat aby spravne fungovala hra.
     * Aktualizovanie mapy, entit, casu, efektov ,svetla a casti to vsetko sa vykonava z tejto metody.
     */
    public void update() {
        updateAround(player); 
        updateEntities();
        if (isSecond()) {  
            updateTime();
            updateSpawning();
            updateEffects();         
            updateLighting();
            if (player != null) {
                player.updateQuests();
            }
            if (particle) {
                updateParticles();
            }             
        }                      
    }        
    
    /**
     * Metoda ktora vykona aktualizaciu entit na plane. Hlavnou ulohou
     * je spawnut/ vytvorit novu entitu na plane po nejakej nahodnej dobe urcenej
     * premennou spawnRate, ktoru mozme lubovolne menit. 
     */
    private void updateSpawning() {
        if (MainUtils.random.nextInt(spawnRate) == spawnRate - 1) {            
             DataUtils.execute(new Thread(new Spawn(this)));             
        }
    }
    
    /**
     * Metoda ktora vykona aktualizacie okolo entity zadanej parametrom <b>e</b>.
     * Pri zmene chunk stavu (chunkState) sme donuteny nacitat do chunkov na vyrenderovanie
     * nove data pomocou pozicii z entity. Taktiez nastavujeme nasmu renderovaciemu pluginu odkial sa ma vykreslovat 
     * pomocou metod setScreenX a setScreenY    
     * @param e Entita okolo ktorej aktualizujeme mapu
     */
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
        
        
                             
    }
    
    /**
     * Metoda ktora aktualizuje cas vzdy po kazdej sekunde. 60 sekund znamena v hre
     * jednu hodinu a po 24 hodinach znova vynuluvavame. Po kazdej zmene hodiny menime 
     * stav prekreslovania svetla.
     * 
     */
    private void updateTime() {
        dayTime++;
        if (dayTime >= 60) {
            gameTime++;
            if (gameTime >= 24) {
                gameTime = 0;
            }
            lightState = true;
            dayTime = 0;
        }
    }
    
    /**
     * Metoda ktora aktualizuje efekty ktore su aktivne v mape v liste onSelfEffects.
     * Pri nepodareni aktualizie efektu vymazavame efekt z tohoto listu.
     */
    private void updateEffects() {
        /* Riesenie cez iterator ktory vymaze z aktivnych efektov taky ktory
         * uz skoncil alebo tam nejakym sposobom nepatri. Vyberame iba effeckty typu
         * ONSELF ktore sa vykonavaju kazdu sekundu na entite. Dalsie efekty sa vykonavaju v prislucnych
         * blokoch/metodach (ONUSE - use metoda, ONEQUIP - equip metoda, ... atd)
         * Bez iteratora, vymazavanim rovno z Listu by vyhodilo ConcurrentModificationException
         */                      
        if (!onSelfEffects.isEmpty()) {
            for (Iterator<Effect> iter = onSelfEffects.iterator(); iter.hasNext();) {
                    if (!(iter.next()).update()) {
                        iter.remove();                    
                }
            }
        }           
        
    }
    
    /**
     * Metoda ktora aktualizuje entity. Aktualizujeme iba efekty nachadzajuce sa v tesnej blizkosti entity
     * (cca 10 dlazdic). Na mape sa moze nachadzat viacero entit (100-1000) co by pravdaze spomalovalo hru.
     * Taktiez berieme do uvahy ako daleko je entita od hraca a pri velkej vzdialenosti entitu tiez vymazeme.
     * Preto riesim takyto pripad tymto sposobom. Pri nepodareni update entity vymazavame entitu
     * z listu entit.
     */
    private void updateEntities() {
        nearEntities.clear();
        for (Entity e: entitiesList) {   
            int lx = Math.abs(e.getX() - player.getX());
            int ly = Math.abs(e.getY() - player.getY());
            if (player.getLevel() == e.getLevel() && lx < 10
                    && ly < 10) {
                nearEntities.add(e);
            } else {
                if (e.isDespawnable() && (lx > 16 || ly > 16)) {                       
                    entityRemove.add(e);                                           
                }
            }            
        }
        
        for (Entity e : nearEntities) {
            if (!e.update()) {
                entityRemove.add(e);
            }
        }
        
        if (!entityRemove.isEmpty()) {
            entitiesList.remove(entityRemove.pop());
        }
    }
    
    /**
     * Metoda ktora aktualizuje svetlo. Svetlo aktualizujeme iba vtedy ked mame zapnute zobrazovanie
     * svetla (lighting) a ked sa zmenil svetelny stav (po kazdych 60 skeundach). 
     * Svetlo aktualizujeme pomocou objektu typu DayLighting na ktory zavolame
     * metodu init s aktualnym casom. Nasledne vytvorime v renderi svetelnu mapu
     * pomocou tohoto objektu. 
     */
    private void updateLighting() {         
        if (lighting && lightState) {
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
        
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda ktora nastavi sirku a vysku mapy. Nazaciatku nastavujeme v render pluginu ako skalujeme mapu
     * potom podla toho ci chcem skalovat alebo nie nastavujeme vyska a sirky aj do render pluginu +
     * nastavujeme x a y-ovu poziciu okna.
     * @param width Sirka mapy.
     * @param height Vyska mapy.
     */
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
        
        render.setScreenX((width - (screenLength)) / 2 + 256);
        render.setScreenY((height - (screenLength)) / 2 + 256);
        //
    }                
    
    /**
     * Metoda ktora nastavi v akej vyske pozorujeme mapu.
     * @param level Vyska mapy ktoru pozorujeme.
     */
    public void setLevel(int level) {
        if (level < Chunk.getDepth()) {
            this._lcurrent = level;
        }
    }
    
    /**
     * Metoda ktora nastavi ako casto sa vytvaraju nove entity
     * @param rate 
     */
    public void setSpawnRate(int rate) {
        this.spawnRate = rate;
    }
    
    /**
     * Metoda ktora nastartuje aktualizovanie a vykreslovanie.
     */
    public void start() {
        this.running = true;
    }
    
    /**
     * Metoda ktora ukonci hru
     */
    public void end() {
        this.running = false;
    }
    
    /**
     * Metoda ktora nastavi aktualiaziu svetelnej mapy na parameter <b>state</b>
     * @param state True/false ci chceme/nechceme aktualizovat mapu
     */
    public void setLightState(boolean state) {
        this.lightState = state;
    }
    
    /**
     * Metoda ktora nastavi hraciu hodinu podla parametru <b>time</b>.     
     * @param time Cas v hodinach na ktory nastavime hru.
     */
    public void setGameTime(int time) {
        this.gameTime = time;
        dayLight.init(time);
        lightState = true;
    }        
        
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Gettery ">   
    /**
     * Metoda ktora vrati true/false podla toho ci prebehla jedna sekunda.
     * @return True/false ci prebehla sekunda
     */
    public boolean isSecond() {
        if (MainUtils.SECONDTIMER - lastSecond >= 1)  {
            lastSecond = MainUtils.SECONDTIMER;
            return true;
        }
        return false;
    }
    
    /**
     * Metoda ktora vrati hraca z mapy za ktoreho hrame.
     * @return Hraca na mape
     */
    public MovingEntity getPlayer() {
        return player;
    }
    
    /**
     * Metoda ktora nastavi pocet chunkov aj po vyske aj po sirke.
     * @return Pocet chunkov po vyske aj sirke.
     */
    public int getChunksSize() {
        return numberOfChunks;
    }                    
    
    /**
     * Metoda ktora vrati chunky ktore chceme renderovat v renderPlugine.
     * Velkost pola je velkost numberOfChunks.
     * @return Pole chunkov na renderovanie.
     */
    public Chunk[][] getChunksToRender() {
        return chunksToRender;
    }
    
    /**
     * Metoda ktora vrati spracovavac svetelnosti v ramci dna ako objektu typu
     * DayLighting.
     * @return DayLighting z ktoreho zistim cas.
     */
    public DayLighting getDayLighting() {
        return dayLight;
    }
    
    /**
     * Metoda ktora vrati cas v hodinach
     * @return Aktualny cas hry (0-24)
     */
    public short getTime() {
        return dayTime;
    }
    
    /**
     * Metoda ktora vrati stav chunkov a ci sa maju preinitializovat
     * @return True/false ci sa initializuju chunky.
     */
    public boolean getChunkState() {
        return chunkState;
    }
    
    /**
     * Metoda ktora vrati stav ci sa zobrazuje osvetlenie.
     * @return True/false ci je aktivne osvetlenie.
     */
    public boolean hasLighting() {
        return lighting;
    }        
    
    /**
     * Metoda ktora vrati sirku mapy.
     * @return Sirka mapy
     */
    public int getWidth() {
        return width;
    }
    
    /**
     * Metoda ktora vrati vysku mapy
     * @return Vyska mapy
     */
    public int getHeight() {
        return height;
    }    
    
    /**
     * Metoda ktora  vrati len tie najblizsie entity k hracovi.
     * @return List s najblizsimi entitami k hracovi
     */
    public ArrayList<Entity> getNearEntities() {
        return nearEntities;
    }
    
    /**
     * Metoda ktora vrati vsetky entity z mapy
     * @return List so vsetkymi entitami.
     */
    public ArrayList<Entity> getAllEntities() {
        return entitiesList;
    }
    
    /**
     * Metoda ktora vrati cas hry v sekundach.
     * @return Celkovy cas hry (v sekundach)
     */
    public int getGameTime() {
        return gameTime;
    }
    
    
    /*
    public ArrayList<Entity> getEntity(int x, int y) {
        EntityPosition pos = new EntityPosition(x, y);
        return entities.get(pos);
    }
    * */
    
    public Entity getEntity(String name) {
        if (entitiesList == null) {
            return null;
        }
        for (Entity e : entitiesList) {
            //System.out.println(e);
            if (e.getName().equals(name)) {
                return e;
            }
        }
        return null;
    }
    
    public Entity getEntity(long uniId) {
        for (Entity e : entitiesList) {
            if (e.getUniId() == uniId) {
                return e;
            }
        }
        return null;
    }
    
    public ArrayList<Effect> getAllEffects() {
        return onSelfEffects;
    }
    
    public ArrayList<Effect> getEffects(Entity e) {
        ArrayList<Effect> result = new ArrayList<>();
        for (Effect effect : onSelfEffects) {
            if (effect.getDestUniId() == e.getUniId()) {
                result.add(effect);
            }
        }
        return result;
    }
    
    public GameMenu getMenu() {
        return menu;
    }
    
    public int getLevel() {
        return _lcurrent;
    }
     
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Map Update ">
    
    /**
     * Metoda ktora zvysi hodinu v hre o 1.
     */
    public void increaseGameTime() {
        this.gameTime++;
    }                       
    
    /**
     * Metoda ktora zvysi aktualny map level (To co akurat vidime na mape) o 1.
     */
    public void incLevel() {
        this._lcurrent++;
    }
    
    /**
     * Metoda ktora znizi aktualny map level (To co akurat vidime na mape) o 1.
     */
    public void decLevel() {
        this._lcurrent--;
    }        
    
    /**
     * Metoda ktora prida efekt do mapy na aktualizovanie a zobrazenie
     * @param effect Efekt ktory pridavame do mapy
     */
    public void addEffect(Effect effect) {
        this.onSelfEffects.add(effect);
    }
    
    /**
     * Metoda ktora prida entitu zadanu parametrom <b>e</b> do mapy.
     * @param e Entita ktoru pridavame.
     * @return True/false ci sa podarilo pridat entitu
     */
    public boolean addEntity(Entity e) {
        if (e instanceof Player) {
            this.player = (Player) e;
            this.render.setMap(this);            
            this.player.setHandling(input);
            entitiesList.add(e);             
            _lcurrent = e.getLevel();
            return true;            
        }
        //addEntityToPosition(e);
        return entitiesList.add(e);
    }
        
    /**
     * Metoda ktora odoberie entitu zadanu parametrom <b>e</b> z mapy.
     * @param e Entita ktoru odoberame z mapy
     */
    public void removeEntity(Entity e) {
        entityRemove.add(e);
        //entities.get(e.getPosition()).remove(e);
    }     
    
    /**
     * Metoda ktora prida casticu do mapy na aktualizovanie a zobrazenie.
     * @param particle Castica ktoru pridavame do mapy.
     * @return True/false ci sa podarilo pridat casticu.
     */
    public boolean addParticle(Particle particle) {
        return particles.add(particle);
    }
    
    /*
    public void addEntityToPosition(Entity e) {
        ArrayList<Entity> positionEntities = entities.get(e.getPosition());
        if (positionEntities == null) {
            positionEntities = new ArrayList<>();
            entities.put(e.getPosition(), positionEntities);            
        } else {
            if (positionEntities.contains(e)) {
                return;
            }
        }
        positionEntities.add(e);
    }
    */        
    /*
    public void removeFromPos(Entity e) {
        ArrayList<Entity> posEntities = entities.get(e.getPosition());
        if (posEntities != null) {
            posEntities.remove(e);
        }
    } */
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Chunk metody ">
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
    
    /**
     * Metoda ktora vrati true/false podla toho ci sa nachadza vo fronte chunkov
     * parameter <b>chunk</b> typu Chunk.
     * @param chunk Chunk ktory hladame vo fronte chunkov.
     * @return True/false ci sa chunk vo fronte nachadza.
     */
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
    
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Handler ">
    /**
     * Metoda ktora spracovava vstup od uzivatela z tejto mapy. Ked nie je player
     * nulovy tak volame metodu inputHandling z Playera. Dalej mozme vypinat/zapinat
     * zobrazovanie svetla, info o mape, castic, skalovania, zvysenie/znizenie aktualneho levelu
     */
    public void inputHandling() {        
        
        if (player != null) {
            player.inputHandling();
        }
        
        // Zobrazovanie svetla
        if (input.clickedKeys.contains(InputHandle.DefinedKey.LIGHTING.getKeyCode())) {
            lighting = !lighting;
        }
        
        // Zobrazovanie info
        if (input.clickedKeys.contains(InputHandle.DefinedKey.STAT.getKeyCode())) {
            stat = !stat;            
        }        
        
        // Zobrazovanie castic
        if (input.clickedKeys.contains(InputHandle.DefinedKey.PARTICLES.getKeyCode())) {
            particle = !particle;            
        }
        
        // Branie skalovania do uvahy skalovania
        if (input.clickedKeys.contains(InputHandle.DefinedKey.SCALING.getKeyCode())) {
            scaleable = !scaleable;
            setWidthHeight(game.getWidth(), game.getHeight());            
        }
        
        // Zvysenie terajsieho stupna
        if (input.clickedKeys.contains(InputHandle.DefinedKey.LEVELUP.getKeyCode())) {
            _lcurrent++;
        }
        
        // Znizenie terajsieho stupna
        if (input.clickedKeys.contains(InputHandle.DefinedKey.LEVELDOWN.getKeyCode())) {
            _lcurrent--;
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Saving Loading ">
    
    /**
     * Metoda ktora nacita chunk na poziciach dane parametrami <b>x,y</b>. Chunk obycajne po nacitani
     * davame nakoniec fronty no pri takomto nacitani ho pridame nazaciatok, co znamena, ze
     * pri ukladani chunkov bude tento ulozeny ako prvy
     * @param x X-ova pozicia Chunku
     * @param y Y-ova pozicia Chunku
     * @return Nacitany chunk na poziciach x,y
     */
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
            Chunk newChunk = new Chunk(x, y);
            addChunkOnBegin(newChunk);
            newChunk.setContent(generateMap(x, y));
            return newChunk;             
        }
        
        return chunk;
    }
    
    /**
     * Metoda ktora nacita chunk na poziciach dane parametrami <b>x,y</b>. Chunk ukladame
     * nakoniec fronty => bude ulozeny ako posledny.
     * @param x X-ova pozicia Chunku
     * @param y Y-ova pozicia Chunku
     * @return Nacitany chunk na poziciach x,y
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
            Chunk newChunk = new Chunk(x, y);
            addChunk(newChunk);
            newChunk.setContent(generateMap(x, y));
            return newChunk;               
        }
        
        return chunk;
    }
    
    /**
     * Metoda ktora nacita chunky okolo entity zadanej parametrom <b>e</b>.
     * Pre kazdy chunk volame metodu loadMap. Po nacitani poziadame o preinicializovanie
     * chunkov na renderovanie pomocou premennej chunkState.
     * @param e Entita okolo ktorej nacitavame
     */
    public void loadMapAround(MovingEntity e) {
        for (int i = e.getRegY() - 1; i < e.getRegY()+2; i++) {
            for (int j = e.getRegX() -1 ; j < e.getRegX()+2; j++) {
                loadMap(j, i);
            }
        }
        chunkState = true;
    }
    
    /**
     * Metoda ktora nacita chunky okolo pozicii zadanych parametrami <b>x,y</b>.
     * Pre kazdy chunk volame metodu loadMap. Po nacitani poziadame o preinicializovanie
     * chunkov na renderovanie pomocou premennej chunkState. 
     * @param x X-ova pozicia chunku
     * @param y Y-ova pozicia chunku
     */
    public void loadMapAround(int x, int y) {
        for (int i = y - 1; i < y+2; i++) {
            for (int j = x - 1 ; j < x+2; j++) {
                loadMap(j, i);
            }
        }
        chunkState = true;
    }        
    
    /**
     * Metoda ktora ulozi chunk z pozicie dane parametrami <b>x,y</b> rovno na disk.
     * @param x X-ova pozicia chunku ktory ukladame
     * @param y Y-ova pozicia chunku ktory ukladame
     */
    public void saveMap(int x, int y) {      
        try {
            outputStream = new ObjectOutputStream(new FileOutputStream(
                            PathManager.getInstance().getWorldSavePath(saveName + PathManager.MAPS, true)
                    + "region["+x+","+y+"].m")); 
            for (Chunk chunk : chunkQueue) {
                if ((chunk.getX() == x)&&(chunk.getY() == y)) {                                        
                    // Objekt s entitami, ktory ulozime k Chunku.
                    ArrayList<Entity> entSave = new ArrayList<>();
                    for (Entity e : entitiesList) {
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
    
    /**
     * Metoda ktora ulozi chunk zadany parametrom <b>chunkToSave</b>
     * @param chunkToSave Chunk na ulozenie
     */
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
            for (Entity e : entitiesList) {
                if ((e.getRegX() == chunkX )&&(e.getRegY() == chunkY)) {
                    entSave.add(e);
                    e.setSaved(true);                    
                }
            }
            
            SaveChunk save = new SaveChunk(entSave, chunkToSave);
            outputStream.writeObject(save);
            outputStream.close();
            
        } catch (Exception ex) {            
            LOG.log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Metoda ktora vygeneruje mapu a vrati ChunkContent ktory obsahuje data ktore budu ulozene v
     * Chunku. Na vygenerovanie mapy pouzivame Objekt typu MapGenerator ktory 
     * pomocou definovanych map pluginov vygeneruje mapy.
     * @param x X-ova pozicia generovaneho chunku
     * @param y Y-ova pozicia generovaneho chunku
     * @return Obsah/data s ulozenymi polami ktore budu tvorit Chunk.
     */
    public ChunkContent generateMap(int x, int y) {
        MapGenerator map = new MapGenerator(Chunk.getSize(), Chunk.getDepth(), x, y);
        return map.generate(this);
    }
    
    
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Debug ">        
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
        
    // </editor-fold>

    @Override
    public void run() {
        while (running && !paused) {
            try {
                updateEntities();
                //System.out.println(Thread.currentThread());
                Thread.sleep(10);
            } catch (InterruptedException ex) {
                Logger.getLogger(SaveMap.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

}
