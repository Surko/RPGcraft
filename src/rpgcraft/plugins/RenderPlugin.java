/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.plugins;

import java.awt.Graphics;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.entities.Entity;
import rpgcraft.graphics.DayLighting;
import rpgcraft.graphics.render.DefaultRender;
import rpgcraft.graphics.ui.particles.Particle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.SaveMap;
import rpgcraft.map.chunks.Chunk;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.MainUtils;
import rpgcraft.utils.TextUtils;

/**
 * Abstraktna trieda ktora tvori zaklad pre kazdy renderovaci plugin ktory vytvorime.
 * Metoda obsahuje abstraktne metody na implementaciu dediacimi triedami ktore zdruzuju
 * zakladne moznosti pri vykreslovani mapy ktora je v triede zadana ako premenna.
 * Na nacitanie render pluginu nam staci zavolat metodu loadRender s menom suboru
 * (ktory mozeme dostat z vypisu vsetkych pluginov).
 * @author kirrie
 */
public abstract class RenderPlugin {

    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    
    public enum RenderIndexes {
        ID(0),
        NAME(0),
        DATE(1);
        
        private int value;
        
        private RenderIndexes(int value) {
            this.value = value;
        }
        
        public int getIndex() {
            return value;
        }
    }
    
    /**
     * Logger pre tuto triedu
     */
    private static final Logger LOG = Logger.getLogger(RenderPlugin.class.getName());    
    
    /**
     * Aktualne aktivny render.
     */
    private static RenderPlugin activeRender;
    
    /**
     * Mapa ktoru vykreslujeme.
     */
    protected SaveMap map;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Abstraktne metody ">
    /**
     * Metoda vrati meno render pluginu. Zavisle na implementacii. <br>
     * @return Meno pluginu.
     */
    public abstract String getName();
    /**
     * Metoda nastavi sirku a vysku v akych vykreslujeme mapu. Zavisle na implementacii. <br>
     * @param width Sirka mapy
     * @param height Vyska mapy.
     */
    public abstract void setWidthHeight(int width, int height);
    /**
     * Metoda ktora nastavuje zaciatocnu x-ovu poziciu obrazovky kde vykreslujeme (vhodne pri translaciach). <br>
     * Zavisle na implementacii.
     * @param screenX X-ova pozicia zaciatku
     */
    public abstract void setScreenX(int screenX);
    /**
     * Metoda ktora nastavuje zaciatocnu y-ovu poziciu obrazovky kde vykreslujeme (vhodne pri translaciach). <br>
     * Zavisle na implementacii.
     * @param screenY Y-ova pozicia zaciatku
     */
    public abstract void setScreenY(int screenY);
    /**
     * Metoda vrati zaciatocnu x-ovu poziciu obrazovky. <br>
     * Zavisle na implementacii.
     * @return X-ova pozicia obrazovky
     */
    public abstract int getScreenX();
    /**
     * Metoda vrati zaciatocnu y-ovu poziciu obrazovky. <br>
     * Zavisle na implementacii.
     * @return Y-ova pozicia obrazovky
     */
    public abstract int getScreenY();
    /**
     * Metoda ktora vrati poslednu x-ovu hodnotu translacie ktora bola vykonana. <br>
     * Zavisle na implementacii.
     * @return X-ova hodnota translacie
     */
    public abstract int getLastX();
    /**
     * Metoda ktora vrati poslednu y-ovu hodnotu translacie ktora bola vykonana. <br>
     * Zavisle na implementacii.
     * @return Y-ova hodnota translacie
     */
    public abstract int getLastY();
    /**
     * Metoda ktora nastavuje skalovacie parametre podla parametrov. <br>
     * Zavisle na implementacii.
     * @param scaleX Skalovanie v ose x.
     * @param scaleY Skalovanie v ose y.
     */
    public abstract void setScaleParams(double scaleX, double scaleY);
    /**
     * Metoda ktora vytvori osvetlovaciu mapu podla parametru dayLight.
     * @param dayLighting Objekt s roznymi hodnotami pre specificku hodinu v dni. <br>
     * Zavisle na implementacii.
     * @see DayLight
     */    
    public abstract void makeLightingMap(DayLighting dayLighting);
    /**
     * Metoda vykresli pozadie mapy do grafickeho kontextu zadaneho parametrom <b>g</b>
     * hodnoty ulozene v parametre <b>chunksToRender</b> <br>
     * Zavisle na implementacii.
     * @param g Graficky kontext
     * @param chunksToRender Chunky na vykreslenie
     * @throws InterruptedException Vynimka pri preruseni inym Threadom.
     * @see Chunk
     */
    public abstract void paintBackground(Graphics g, Chunk[][] chunksToRender) throws InterruptedException;
    /**
     * Metoda ktora vykresli osvetlenie mapy. Vacsinou vykreslenie premennej lightingMap do grafickeho kontextu
     * zadaneho parametrom <b>g</b> <br>
     * Zavisle na implementacii.
     * @param g Graficky kontext.
     */
    public abstract void paintLighting(Graphics g);
    /**
     * Metoda ktora vykresli floru do mapy. Tato moznost je pre buduce mozne implementacie. <br>
     * Zavisle na implementacii.
     * @param g Graficky kontext do ktoreho vykreslujeme
     */
    public abstract void paintFlora(Graphics g);
    /**
     * Metoda ktora vykresli entity a castice zadane parametrmi <b>entities, particles</b> do grafickeho kontextu
     * zadaneho parametrom g. <br>
     * Zavisle na implementacii.
     * @param g Graficky kontext
     * @param entities Entity ktore vykreslujeme
     * @param particles Castice ktore vykreslujeme
     */
    public abstract void paintEntitiesParticles(Graphics g, ArrayList<Entity> entities, ArrayList<Particle> particles);    
    /**
     * Metoda ktora vykresluje texty do grafickeho kontextu zadaneho parametrom <b>g</b>. <br>
     * Zavisle na implementacii.
     * @param g  Grafickyt kontext.
     */
    public abstract void paintStrings(Graphics g);
    /**
     * Metoda ktora skaluje graficky kontext. <br>
     * Zavisle na implementacii.
     * @param g  Graficky kontext.
     */
    public abstract void scale(Graphics g);
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    /**
     * Metoda setMap nastavi mapu ktoru budeme vykreslovat tymto render pluginom.
     * @param map Mapa na vykreslovanie
     */
    public void setMap(SaveMap map) {
        this.map = map;
    }
    
    /**
     * Staticka trieda ktora nastavi priamo aktivny plugin na ten zadany v parametre <b>plugin</b>.
     * @param plugin 
     */
    public static void setRender(RenderPlugin plugin) {
        activeRender = plugin;
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    
    /**
     * Metoda ktora vrati list s menami render pluginov. Nato aby sme zistili mena
     * potrebujeme subory ktore ziskame metodou getRenderFiles. Nasledne vyberieme o suboroch informacie ktore
     * potrebujeme a pomocou parametra <b>params</b> vyplnime list.
     * @param params Parametre podla ktorych vyplname list
     * @return List s udajmi o render pluginoch.
     */
    public static ArrayList<ArrayList<Object>> getRenderParam(ArrayList<String> params) {
        ArrayList<ArrayList<Object>> resultInf = new ArrayList<>();
        for (File file : getRenderFiles()) {
                ArrayList<Object> record = new ArrayList<>();
                Object[] saveData = new Object[RenderIndexes.values().length];

                saveData[RenderIndexes.NAME.getIndex()] = file.getName();
                saveData[RenderIndexes.DATE.getIndex()] = DataUtils.getSpecificDate(file.lastModified());                

                record.add(saveData[RenderIndexes.ID.getIndex()]);

                for (String p : params) {
                    switch (RenderIndexes.valueOf(p)) {
                        case NAME : {
                            record.add(saveData[RenderIndexes.NAME.getIndex()]);
                        } break;
                        case DATE : {
                            record.add(saveData[RenderIndexes.DATE.getIndex()]);
                        } break;                        
                        default : LOG.log(Level.WARNING,p + " is not implemented parameter");
                    }
                }
                resultInf.add(record);            
        }        
        return resultInf;          
    }
    
    /**
     * Metoda ktora vrati vypis suborov nachadzajuce sa v zlozke
     * s render pluginmi. Na subory pouzije nami zadany jarFilter aby sme vybrali
     * iba jar subory.
     * @return Vypis suborov zodpovedajucim filtru zo zlozky s render pluginmi.
     */
    public static File[] getRenderFiles() {
        return PathManager.getInstance().getRenderPath().listFiles(MainUtils.jarFilter);
    }
    
    /**
     * Metoda ktora nacita renderovaci plugin s menom suboru zadanym ako parameter <b>fileName</b>
     * Volame classLoader ktory nacita triedu Render v packagi rpgcraft.graphics.render (z tohoto vyplyva
     * ze kazdy plugin musi mat rovnaku stavbu) a ulozi hu priamo do aktivneho renderPluginu.
     * @param fileName Meno suboru s render pluginom ktory nacitavame.
     * @return Aktivny render plugin
     */
    public static RenderPlugin loadRender(String fileName) {
        File renderFile = new File(PathManager.getInstance().getRenderPath(), fileName);
        
        try {
            ClassLoader authorizedLoader = URLClassLoader.newInstance(new URL[] { renderFile.toURI().toURL() });
            activeRender = (RenderPlugin) authorizedLoader.loadClass("rpgcraft.graphics.render.Render").newInstance();        
        } catch (MalformedURLException | ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            LOG.log(Level.INFO, StringResource.getResource("_renderload", new String[] { fileName, TextUtils.stack2string(e)}));
            activeRender = DefaultRender.getInstance();
        }
        
        return activeRender;
    }
    // </editor-fold>
    
}
