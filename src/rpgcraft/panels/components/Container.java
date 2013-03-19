/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import rpgcraft.panels.components.swing.SwingComponent;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;

 /**
 * <p>Trieda Kontainer zdruzuje informacie pre kazdy resource nachadzajuci sa v tomto menu.
 * Da sa povedat ze je to podobne ako Java Component a od neho odvodene JPanel, JButton, odlisne iba v tom
 * ze v kazdom containery je iba jedina komponenta, ktora si zije mimo vsetkych ostatnych a ovplyvnuje
 * sa iba podla rodicovskej. </p> 
 * <p>Obsahuje zakladne pozicie resource (x,y), dlzku a vysku resource (w,h), 
 * dlzku a vysku otcovskeho resource (pw, ph) a minimalnu dlzku a vysku tejto komponenty.
 * Tieto informacie uchovavam preto, lebo jednotlive komponenty pri zvacsovani resp. zmensovani musim takisto
 * zvacsovat resp. zmensovat a spravne umiestnit. V pripade, ze ma komponenta natvrdo zadane parametre, tak mi opada starost
 * o prepocitavanie velkosti. Pri komponente ktora ma v xml hodnoty <b>fill-parent</b> sa tomu
 * nevyhnem. <br>
 * Takisto sa v nom nachadza BufferedImage v ktorom je vykresleny len aktualny resource. <br>
 * Len aktualny znamena ze kazdy resource ma pre seba vlastny kontajner s obrazkom, co moze vyzerat
 * ako zbytocne mrhanie ramkov. </p>
 * <p>Dovodom pre takyto postup je ten, ze si treba uvedomit, ze ked sa nieco vykresluje,
 * tak je treba mysliet nato, ze jeden resource ma svoj priestor v svojom kontainery a v ziadnom inom
 * (tymto zabranujem aby jeden resource krizoval druhy a v strukture xml by ten najviditelnejsi musel byt uplne naspodku, tymto 
 * nehovorim ze aj tymto sa to neda dosiahnut, napriklad ze
 * sa bude rovno vykreslovat do grafickeho kontextu) =>
 * kazdy element je vzdy ohraniceny svojim kontajnerom co dava moznost vytvorit zaujimave a pekne GUI. <br>
 * Dalsim dovodom je kedze prvky su dynamicke a jednotlive resource/elementy sa mozu hybat,
 * pricom nemusia menit svoje velkosti tak mi tymto sposobom opada zbytocne alokovanie 
 * alebo vykreslovanie niecoho co sa v konecnom dosledku ani nezmenilo.</p>
 * <br>
 * Pouziva sa ako hodnota v HashMape pre pristup k nemu skrz resource.
 */   
public class Container {
    
        // <editor-fold defaultstate="collapsed" desc=" Premenne ">    
        public static Container mainContainer;
    
        private int x,y,w,h,mw,mh;
        private BufferedImage resImage;
        private UiResource resource;
        private Component c;
        private Container parent;
        private boolean top;
        private ArrayList<Container> childContainers;
        private boolean changed;
        private boolean visible;
        private ArrayList<Container> positionslessCont;
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
        /**
         * Konstruktor ktory vytvori novy kontajner uchovavajuci si informacie <br>
         * - o pozicii komponenty : parametre x,y <br>
         * - o sirke a vyske komponenty : parametre w,h <br>
         * - o sirke a vyske rodicovskej komponenty : parametre pw, ph <br>
         * - o minimalnej sirke a vyske komponenty : parametre mw, mh
         * @param x x pozicia v komponente
         * @param y y pozicia v komponente
         * @param w Sirka komponenty
         * @param h Vyska komponenty
         * @param pw Sirka otcovskej komponenty
         * @param ph Vyska otcovskej komponenty
         * @param mw Minimalna sirka tejto komponenty
         * @param mh Minimalna vyska tejto komponenty
         */        
        
        public Container(UiResource resource, int w, int h, int mw, int mh, Container parent) {
            this.resource = resource;
            this.x = resource == null ? 0 : resource.getX();
            this.y = resource == null ? 0 : resource.getY();
            this.positionslessCont = null;
            this.mw = mw;
            this.mh = mh;
            this.w = w;
            this.h = h;
            this.parent = parent;
            this.changed = true;
            this.top = false;
            this.visible = resource == null ? true : resource.isVisible();
        }
        
        public Container(Container srcCont) {
            this.resource = srcCont.resource;
            this.x = srcCont.x;
            this.y = srcCont.y;            
            this.mw = srcCont.mw;
            this.mh = srcCont.mh;
            this.w = srcCont.w;
            this.h = srcCont.h;
            this.top = srcCont.top;
            this.parent = srcCont.parent;            
            if (srcCont.childContainers != null) {
                this.childContainers = new ArrayList(srcCont.childContainers.size());
                for (Container cont : srcCont.childContainers) {
                    this.childContainers.add(new Container(cont));
                }
            }
            this.changed = srcCont.changed;
            this.resImage = srcCont.resImage;
            this.visible = srcCont.visible;
            setComponent(srcCont.c.copy(this, srcCont.c.getOriginMenu()));
            
            
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Public metody ">
        
        // <editor-fold defaultstate="collapsed" desc=" Funkcie nad kontajnerom ">
        public void increase(int x, int y) {
            
            if (this.x + x > parent.w) {
                this.x = 0;
            } else {
                this.x+=x;
            } 
            if (this.y + y > parent.h) {
                this.y = 0;
            } else {
                this.y+=y;
            }
            
            if (c != null) {
                c.setBounds(this.x, this.y, w, h);
                
            }
        }
        
        public void makeBufferedImage() {
            this.resImage = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
        }
        
        public void addContainer(Container cont) {
            if (childContainers == null) {
                childContainers = new ArrayList<>();                
            }
            childContainers.add(cont);            
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Gettery ">
        public UiResource getResource() {
            return resource;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;        
        }
        
        public int getWidth() {
            return w;            
        }
        
        public int getHeight() {
            return h;
        }
        
        public int getMinWidth() {
            return mw;            
        }
        
        public int getMinHeight() {
            return mh;
        }
        
        public int getParentWidth() {
            return parent.w;            
        }
        
        public int getParentHeight() {
            return parent.h;
        }
        
        public Component getComponent() {
            return c;
        }
        
        public ArrayList<Container> getPositionslessCont() {
            return positionslessCont;
        }                
        
        public java.awt.Component getSwingComponent() {
            if (c instanceof Component) {
                return (java.awt.Component)c;
            }
            return null;
        }
        
        public boolean isChanged() {
            return changed;
        }
        
        public boolean isVisible() {
            return visible;
        }
        
        public boolean isTopContainer() {
            return top;
        }
                     
        public boolean isTopLevelComponent(Container cont) {
            return cont.c.isShowing();
        }
        
        public BufferedImage getImage() {
            return resImage;
        }
        
        public Container getParentContainer() {
            return parent;
        }
        
        public ArrayList<Container> getChildContainer() {
            return childContainers;
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Settery ">
        public void set(int w, int h, int mw, int mh) {
            this.w = mw > w ? this.w : w;
            this.h = mh > h ? this.h : h;            
            this.changed = true;
        }
        
        public void set(int w, int h) {
            this.w = w;
            this.h = h;
        }
        
        public void clearPositionsless() {
            positionslessCont = null;
        }
        
        public void addPositionslessCont(Container cont) {
            if (positionslessCont == null) {
                positionslessCont = new ArrayList<>();                
            }
            positionslessCont.add(cont);
        }
        
        public void setComponent(Component c) {
            this.c = c;                       
            
            if ((resource != null) &&(c instanceof SwingComponent)) {
                JPanel swn = (JPanel)c;
                swn.setVisible(visible);
                setLayout((JPanel)c);   
                                
                swn.setMinimumSize(new Dimension(mw, mh));
                swn.setPreferredSize(new Dimension(w, h));                
                swn.setSize(new Dimension(w, h));
            }
        }
        
        public void setPositions(int[] positions) {
            this.x = positions[0];
            this.y = positions[1];
        }
        
        public void hasChanged() {
            this.changed = true;
        }
        
        public void setTop(boolean top) {
            this.top = top;
        }
        
        public void setChanged(boolean changed) {
            this.changed = changed;
        }
        
        public void setImage(BufferedImage resImage) {
            this.resImage = resImage;
        }
        
        public void setChildContainers(ArrayList<Container> containers) {
            this.childContainers = containers;
        }
        
        public void setParentContainer(Container parent) {
            this.parent = parent;
        }
        
        // </editor-fold>
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Privatne metody ">
        
        /**
        * Metoda ktora nastavi Layout hlavneho panelu hry podla toho, co sa nachadza 
        * v UiResource ktory zodpoveda tomuto menu.
        */
        private void setLayout(JPanel panel) {
        switch (resource.getLayoutType()) {
            case GRIDBAGSWING : {
                panel.setLayout(new GridBagLayout());                
            } break;
            case GRIDSWING : {
                try {                
                panel.setLayout(new GridLayout(resource.getX(), resource.getY()));
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, StringResource.getResource("_iparam"), "width/height in GridLayout");
                }                
            } break;
            case BORDERSWING : {
                panel.setLayout(new BorderLayout(resource.getHGap(), resource.getVGap()));                
            } break;
            case FLOWSWING : {
                panel.setLayout(new FlowLayout(resource.getAlign(), resource.getHGap(), resource.getVGap()));
            } break;
            case INGAME : {      
                panel.setLayout(null);
                panel.setBackground(Color.BLACK);
            } break;
        }              

    }
    // </editor-fold>

    
        
}

