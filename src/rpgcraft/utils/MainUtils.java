/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.File;
import java.io.FileFilter;
import rpgcraft.graphics.Colors;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingText;

/**
 * Trieda Framer v sebe zdruzuje zakladne moznosti pre pracu s pocitanim vykreslenych framov.
 * Definuje si privatnu staticku triedu FramePanel, ktora zarucuje vykreslovanie framov.
 * Instancia tohoto objektu je definovana v statickom bloku.
 * @see FramePanel
 * @author Surko
 */
public class MainUtils {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    
    /**
     * Filter suborov iba na jar subory
     */    
    public static final FileFilter jarFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            return file.getName().matches(".*[.]jar");
        }
    };
    
    // FramePanel pre frame pocitadlo
    public static final FramePanel FPSCOUNTER;        
    public static volatile int TICK = 0;
    public static volatile long START_TIME;
    public static volatile boolean DEBUG = true;
    
    //FPS junk
    public static int fShow;
    public static final boolean SHOWFPS = true;
    public static final long fpsProhibitor = 10;
    public static volatile int framing; 
    public static volatile long fpsTimer;  
    public static volatile int debugint;
    
    // </editor-fold>
       
    /**
     * Definovana staticka trieda FramePanel dediaca od SwingText. Vytvara komponentu pre 
     * zobrazovanie framov, ktora podla toho co je v nej napisane zvacsuje zmensuje tuto komponentu.
     * Takisto v nej dochadza k aktualizovani vykreslovanych framov. Trieda sa musi nachadzat v programe
     * len v jednej instancii preto je trieda privatna a staticka a jediny sposob ako sa vytvori 
     * je v statickom bloku definovanom v triede Framer. Nasledne si tuto komponentu ziskava GamePane
     * a pridava si ho k svojim komponentam cim zarucuje, ze sa komponenta zobrazi.
     */
    private static final class FramePanel extends SwingText {
    
        private static final int FPSWIDTH = 47, FPSHEIGHT = 10;
        
        /**
         * Konstruktor pre FramePanel. ROvnaky ako v SwingComponent, jedine je pridane nastavenie
         * pozadia na transparentnu farbu.
         * @param container Kontajner v ktorom je Framer (null)
         * @param menu Menu ktore vytvara Framer (null)
         */
        protected FramePanel(Container container,AbstractMenu menu) {
            super(container, menu);
            setBackground(Colors.getColor(Colors.transparentColor));  
            this.tw = w = FPSWIDTH;
            this.th = h = FPSHEIGHT;
        }

        /**
         * Metoda paintComponent vykresli FPS do komponenty. Po vykresleni aktualizuje pocet framov.
         * Toto aktualizovanie nam da spravny pocet kedze vykreslovanie vykonava jeden Thread postupne.
         * @param g Graficky kontext Frameru.
         */
        @Override
        public void paintComponent(Graphics g) {
            //System.out.println("framer " + debugint);
            g.setColor(textColor);  
            g.setFont(getFont());
            if (title != null) {
                g.drawString(title, 0, th);                
            }
            updateFPS();
        }       

        
        /**
         * Override metoda zo SwingText ktora nastavi text o pocte framov do FPS pocitadla.
         * Velkosti su priamo zadane v konstruktore preto nevolame metody setTextSize atd.
         * @param text Text na zobrazenie
         * @param parsing nepouzity parameter parsing
         */
        @Override
        public void setText(String text, boolean parsing) {
            this.title = text;
            
        }
        
        
        
        /**
         * Metoda ktora vrati preferovanu dimenziu pre framer.
         * Velkost je velkost toho co je v komponente napisane.
         * @return Preferovana velkost Dimenzie.
         */
        @Override
        public Dimension getPreferredSize() {
            return new Dimension(tw,th);
        }
        
        /**
         * Metoda ktora vrati minimalnu velkost tejto komponenty.
         * Takisto to je velkost toho co je v komponente napisane
         * @return Minimalne velkost komponenty 
         */
        @Override
        public Dimension getMinimumSize() {
           return new Dimension(tw,th); 
        }
        
        /**
         * Metoda ktora vrati normalnu velkost tejto komponenty.
         * Takisto to je velkost toho co je v komponente napisane
         * @return Normalna velkost komponenty 
         */
        @Override
        public Dimension getSize() {
           return new Dimension(tw,th); 
        }       
        
        /**
         * Metoda ktora aktualizuje pocet vykreslenych framov za 1 sekundu.
         * Kedze vykreslovanie prebieha v jednom Threade (AWT-Thread) tak volanie tejto metody
         * z paintComponent nam da spravny pocet. Po 1000ms <=> 1s nastavi text (pocet framov) podla
         * metody definovanej v SwingText cim sa zmeni aj velkost komponenty, co sa vdaka
         * definovanym metodam getPreffered/Minimum/Size zobrazi na obrazovke v spravnych velkostiach.
         */
        private void updateFPS() {
        
            if (MainUtils.SHOWFPS) {
                MainUtils.framing++;
            } 
        
            if (MainUtils.SHOWFPS) {
                if (System.currentTimeMillis() - fpsTimer > 1000) {
                    fpsTimer += 1000;                    
                    fShow = MainUtils.framing;
                    framing = 0; 
                    setText(fShow+ " FPS", false);
                }                    
            }                    
        }               
    }
    
    /**
     * Staticka trieda IntervalTree vytvara moznost vytvorit datovu strukturu ktora v sebe uchovava 
     * dalsiu datovu strukturu a to BS (Binarny strom). Intervalovy strom ma zarucovat metodou getValue ze volajuca metoda 
     * dostane naspat minimalnu hodnotu typu Comparable intervalu v ktorom lezi nejaka programatorom zadana hodnota.
     * <p><blockquote><pre>
     *          50
     *        /   \
     *      20    null 
     *     /  \ 
     *  null null
     *
     *</pre></blockquote> 
     * 
     * <pre>
     * Nech volame metodu getValue(10, 0) :
     *  - 10 je mensie ako 50 
     *  - 10 je mensie ako 20 
     *  - je tam null vratime minimalnu hodnotu = 0. <br>
     * Nech volame metodu getValue(30, 0) :
     *  - 30 je mensie ako 50 
     *  - 30 je vacsie ako 20 => volame metodu getValue(30, 20) :
     *      - je tam null vratime minimalnu hodnotu = 20.
     * </pre>
     * 
     * V lahkosti to ma rovnaky ucinok ako keby prehladavame pole po prvkoch a najdeme prvok ktoreho cislo
     * je vacsie ako nami zadane tak vratime predchadzajuci prvok (prvy prvok od najdeneho ktoreho hodnota je mensia ako nami zadana)
     * @param <O> Intervalovy strom moze byt postaveny nad lubovolnym objektom dediacom po objekte Comparable
     */
    public static final class IntervalTree<O extends Comparable> {
        
        /**
         * Vnutorna trieda na vytvorenie datovej struktury binarneho stromu. Rozdiel je len v tom
         * ze hodnoty do uzlov su dane podla parametru O v Intervalovom strome.
         */
        class Node {
            
            /**
             * Konstruktor BS.
             * @param value Hodnota do uzlu.
             */
            Node(O value) {
                this.value = value;
                this.rightChild = null;
                this.leftChild = null;
            }
            
            /**
             * Pridanie potomka k uzlu podla hodnoty <b>value</b>
             * @param value Hodnota novo vytvoreneho uzlu.
             */
            void addChild(O value) {
                if (value.compareTo(this.value) < 0) {
                    if (leftChild == null) {
                        leftChild = new Node(value);
                    } else {
                        leftChild.addChild(value);
                    }
                    return;
                } 
                if (value.compareTo(this.value) > 0) {
                    if (rightChild == null) {
                        rightChild = new Node(value);
                    } else {
                        rightChild.addChild(value);
                    }                    
                }
            }
            
            /**
             * Pravy a lavy potomok uzlu
             */
            Node rightChild,leftChild;
            /**
             * Hodnota uzlu typu Comparable.
             */
            O value;
        }
        
        /**
         * Root element tohoto stromu
         */
        Node root;
        
        /**
         * Konstruktor Invervaloveho stromu. Hodnota ako parameter inicializuje korenovy element.
         * @param value Hodnota do korenoveho uzlu.
         */
        public IntervalTree(O value) {
            root = new Node(value);
        }
        
        /**
         * Metoda ktora prida potomka s hodnotou <br>value</br> ku korenu. Zaobstaranie tejto cinnosti ma na starosti
         * metoda vo vnutornej triede Node.
         * @param value Hodnota noveho potomka.
         * @see Node#addChild(java.lang.Comparable) 
         */
        public void addChild(O value) {
            root.addChild(value);
        }
        
        /**
         * Metoda ktora najde interval do ktoreho spada hodnota zadana parametrom value.
         * Nasledne vrati minimum tohoto intervalu. Interval je ohraniceny dvoma uzlami.
         * Ked je uzol rovny null tak vrati hodnotu zadanu parametrom min.
         * @param start Startovaci uzol
         * @param value Hodnota podla ktorej hladame.
         * @param min Minimum keby je uzol rovny null
         * @return Minimalna hodnota intervalu do ktoreho spada hodnota value
         */
        public O getLowerInterval(Node start, O value, O min) {
            if (start == null) {
                return min;
            }
            
            if (value.compareTo(start.value) < 0) {
                return getLowerInterval(start.leftChild, value, min);
            } else {
                return getLowerInterval(start.rightChild, value, start.value);
            }
                       
        }
        
        
    }
    
    public static void timerStart() {
        START_TIME = System.nanoTime();
    }
    
    public static void timerEnd() {
        System.out.println(System.nanoTime() - START_TIME);
    }
    
    static {      
        FPSCOUNTER = new FramePanel(null, null);
        FPSCOUNTER.setText("000 FPS", false);
        FPSCOUNTER.setFont(null);
        FPSCOUNTER.setColor(Colors.getColor(Colors.fpsColor));        
        FPSCOUNTER.setBounds(0, 0, FPSCOUNTER.getTextW(), FPSCOUNTER.getTextH());    
    }
    
    
}
