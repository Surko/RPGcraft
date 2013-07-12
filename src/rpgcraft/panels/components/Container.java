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
import rpgcraft.GamePane;
import rpgcraft.panels.components.swing.SwingComponent;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.MainUtils;

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
    
        private Dimension minDimension,prefDimension;
        private int x,y;
        private boolean autow,autoh;
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
         * @param resource UiResource ktory tvori vzor pre komponentu ulozenu v kontajneri
         * @param w Sirka komponenty
         * @param h Vyska komponenty
         * @param mw Minimalna sirka tejto komponenty
         * @param mh Minimalna vyska tejto komponenty
         * @param parent Rodicovsky kontajner
         */                
        public Container(UiResource resource, int w, int h, int mw, int mh, Container parent) {
            this.resource = resource;
            this.x = resource == null ? 0 : resource.getX();
            this.y = resource == null ? 0 : resource.getY();
            this.positionslessCont = null;            
            minDimension = new Dimension(mw, mh);
            prefDimension = new Dimension(w, h);            
            this.parent = parent;
            this.changed = true;
            this.top = false;
            this.visible = resource == null ? true : resource.isVisible();
            this.autow = (w == -1);
            this.autoh = (h == -1);
        }        
        
        /**
         * Konstruktor ktory vytvori novy kontajner ktory bude okopirovany od toho
         * ktory zadavame ako parameter <b>srcCont</b>.
         * @param srcCont Zdrojovy kontajner z ktoreho kopirujeme
         */
        public Container(Container srcCont) {
            this.resource = srcCont.resource;
            this.x = srcCont.x;
            this.y = srcCont.y;    
            this.minDimension = new Dimension(srcCont.minDimension);
            this.prefDimension = new Dimension(srcCont.prefDimension);  
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
        /**
         * Metoda ktora zvysi pozicie kontajneru.
         * @param x Zvysenie o x-ovu poziciu
         * @param y Zvysenie o y-ovu poziciu
         */
        public void increase(int x, int y) {
            
            if (this.x + x > parent.prefDimension.width) {
                this.x = 0;
            } else {
                this.x+=x;
            } 
            if (this.y + y > parent.prefDimension.height) {
                this.y = 0;
            } else {
                this.y+=y;
            }
            
            if (c != null) {
                c.setBounds(this.x, this.y, prefDimension.width, prefDimension.height);
                
            }
        }
        
        /**
         * Metoda ktora vytvori kontajnerovy obrazok.
         */
        public void makeBufferedImage() {
            this.resImage = new BufferedImage(prefDimension.width, prefDimension.height, BufferedImage.TYPE_4BYTE_ABGR);
        }
        
        /**
         * Metoda ktora prida novy kontajner zadany parametrom <b>cont</b> do tohoto
         * kontajneru. S pridanim kontajneru pridavame aj komponentu ktoru obsahuje kontajner.
         * @param cont Novy kontajner na pridanie 
         */
        public void addChildComponent(Container cont) {
            if (c instanceof GamePane) {
                if (childContainers.contains(cont)) {
                    c.addComponent(cont.getComponent(),cont.getResource().getConstraints());
                }            
            } else {
                switch (resource.getLayoutType()) {
                    case INGAME : c.addComponent(cont.getComponent());
                        break;
                    default : c.addComponent(cont.getComponent(),cont.getResource().getConstraints());  
                        break;                    
                }
            }
    
                
        }
        
        /**
         * Metoda ktora prida vsetky detske komponenty z kontajnerov ktore su ulozene
         * v liste childContainers. 
         */
        public void addChildComponents() {
            if (c instanceof GamePane) {
                //System.out.println(Thread.currentThread());
                for (Container cont : childContainers) {
                    c.addComponent(cont.getComponent(),cont.getResource().getConstraints());
                }
            } else {
                switch (resource.getLayoutType()) {
                    case INGAME : {
                        for (Container cont : childContainers) {
                            c.addComponent(cont.getComponent());
                        }
                    } break;
                    default : {
                        for (Container cont : childContainers) {
                            c.addComponent(cont.getComponent(),cont.getResource().getConstraints());
                        }
                    } break;                    
                }
            }
        }
        
        /**
         * Metoda ktora prida iba kontajner do listu childContainers (nie komponenty)
         * @param cont Kontajner ktory pridavame do detskych kontajnerov
         */
        public void addChild(Container cont) {
            if (childContainers == null) {
                childContainers = new ArrayList<>();                
            }
            if (childContainers.contains(cont)) {
                return;
            }
            childContainers.add(cont);                         
        }
                
        /**
         * Metoda ktora prida kontajner do detskych kontajnerov. Po pridani kontajneru
         * pridame aj komponentu v tomto pridanom kontajneri do rodicovskeho kontajneru.
         * @param cont 
         */
        public void addContainer(Container cont) {
            if (childContainers == null) {
                childContainers = new ArrayList<>();                
            }
            if (childContainers.contains(cont)) {
                return;
            }
            childContainers.add(cont);  
            
            if (c instanceof GamePane) {
                //System.out.println(Thread.currentThread());
                c.addComponent(cont.getComponent(),cont.getResource().getConstraints());
            } else {
                switch (resource.getLayoutType()) {
                    case INGAME : c.addComponent(cont.getComponent());
                        break;
                    default : c.addComponent(cont.getComponent(),cont.getResource().getConstraints());  
                        break;                    
                }
            }
            
        }
        
        /**
         * Metoda ktora vymaze kontajner z listu kontajnerov + vymaze aj komponenty
         * ktore boli v tomto kontajneri.
         * @param cont Kontajner ktory vymazavame.
         */
        public void removeContainer(Container cont) {
            if (childContainers != null) {
                this.c.removeComponent(cont.c);
                childContainers.remove(cont);
                setChanged();
            }
        }
        
        /**
         * Metoda ktora spravne odstrani vsetky detske kontajnery v tomto kontajnery.
         * Spravnym odstranenim sa mysli, ze odstranujeme aj komponenty ktore boli
         * v tychto kontajneroch (na vymazanie staci metoda removeAll)
         */
        public void clear() {
            if (childContainers == null) return;
            this.childContainers.clear();
            this.c.removeAll();
            
            // Pridanie Frameru - mozne odstranit a nahradit nejakym stlacenim klavesnice
            if (c instanceof GamePane) {
                c.addComponent(MainUtils.FPSCOUNTER);
            }
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
        
        public Dimension getPrefDimension() {
            return prefDimension;
        }
        
        public Dimension getMinDimension() {
            return minDimension;                    
        }
        
        public int getWidth() {
            return prefDimension.width;            
        }
        
        public int getHeight() {
            return prefDimension.height;
        }
        
        public int getMinWidth() {
            return minDimension.width;            
        }
        
        public int getMinHeight() {
            return prefDimension.height;
        }
        
        public int getParentWidth() {            
            return parent == null ? mainContainer.getWidth() : parent.prefDimension.width;            
        }
        
        public int getParentHeight() {
            return parent == null ? mainContainer.getHeight() : parent.prefDimension.height;
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
        
        public boolean isAutoWidth() {
            return autow;            
        }
        
        public boolean isAutoHeight() {
            return autoh;
        }
        
        // </editor-fold>
        
        // <editor-fold defaultstate="collapsed" desc=" Settery ">
        /**
         * Metoda ktora nastavi minimalne a normalne vysky a sirky kontajneru. Pri 
         * zmene dlzok nastavime kontajner na zmeneny metodou setChanged ktora postupne nastavi
         * aj rodicovsky kontajner na zmeneny (keby boli kontajnery od seba zavisle)
         * @param w Normaln sirka kontajneru
         * @param h Normaln vyska kontajneru
         * @param mw Minimalna sirka kontajneru
         * @param mh Minimalna vyska kontajneru
         */
        public void set(int w, int h, int mw, int mh) {            
            if (this.prefDimension.width != w || this.minDimension.width != mw || 
                    this.prefDimension.height != h || this.minDimension.height != mh) {
                setChanged(true);
            } else {                
                return;
            } 
            
            if (w == -1 || mw == -1) {
                autow = true;                
            }
            if (h == -1 || mh == -1) {
                autoh = true;                
            }
            
            if (this.prefDimension.width < mw) {
                this.prefDimension.width = mw;
                this.minDimension.width = mw;
            } else {
                this.prefDimension.width = w;
                this.minDimension.width = mw;
            }
            
            if (this.prefDimension.height < mh) {
                this.prefDimension.height = mh;
                this.minDimension.height = mh;
            } else {
                this.prefDimension.height = h;
                this.minDimension.height = mh;
            }
            
        }
        
        /**
         * Metoda ktora nastavi iba normalnu sirku a vysku. Pri 
         * zmene dlzok nastavime kontajner na zmeneny metodou setChanged ktora postupne nastavi
         * aj rodicovsky kontajner na zmeneny (keby boli kontajnery od seba zavisle)
         * @param w Normalna sirka kontajneru
         * @param h Normalna vyska kontajneru
         */
        public void set(int w, int h) {
            if (this.prefDimension.width != w || this.prefDimension.height != h) {
                this.changed = true;
            } else {                
                return;
            } 
            if (w < this.minDimension.width) {
                this.prefDimension.width = this.minDimension.width;
            } else {
                this.prefDimension.width = w;
            }
            
            if (h < this.minDimension.height) {
                this.prefDimension.height = this.minDimension.height;

            } else {
                this.prefDimension.height = h;
            }
        }
        
        /**
         * Metoda ktora vymaze vynuluje list s kontajnermi ktore este nemaju urcene
         * pozicie.
         */
        public void clearPositionsless() {
            positionslessCont = null;
        }
        
        /**
         * Metoda ktora prida ku bezpozicnym kontajnerom novy kontajner zadany
         * parametrom <b>cont</b>
         * @param cont Kontajner ktory pridavame do list bezpozicnych kontajnerov.
         */
        public void addPositionslessCont(Container cont) {
            if (positionslessCont == null) {
                positionslessCont = new ArrayList<>();                
            }
            positionslessCont.add(cont);
        }
        
        /**
         * Metoda ktora nastavi komponentu v tomto kontajnery. K nastavenie komponenty
         * patri aj urcenie layoutu a nastavenie min a normalnych vysok.
         * @param c Komponenta ktoru nastavujeme kontajneru.
         */
        public void setComponent(Component c) {
            this.c = c;                       
            
            if ((resource != null) &&(c instanceof SwingComponent)) {
                SwingComponent swn = (SwingComponent) c;
                swn.setVisible(visible);
                setLayout(swn);   
                                
                swn.setMinimumSize(minDimension);
                swn.setPreferredSize(prefDimension);                
                swn.setSize(prefDimension);
            }
        }
        
        /**
         * Metoda ktora nastavuje pozicie kontajneru v rodicovskom kontajneri
         * @param positions Pozicie kontajneru
         */
        public void setPositions(int[] positions) {
            this.x = positions[0];
            this.y = positions[1];
        }
        
        /**
         * Metoda ktora nastavi ze sa kontajner zmenil
         */
        public void setChanged() {
            this.changed = true;            
            if (parent != null && !(parent.c instanceof GamePane)) {
                parent.setChanged(true);
            }
        }
               
        /**
         * Metoda ktora nastavi ci sa kontajner zmenil podla parametru <b>changed</b>.
         * Ked ma kontajner rodica tak zavola tuto metodu aj nanho (vytvori sa cesta
         * zmenenych kontajnerov)
         * @param changed True/false ci sa zmenil kontajner.
         */
        public void setChanged(boolean changed) {
            this.changed = changed;
            if (parent != null && !(parent.c instanceof GamePane)) {
                parent.setChanged(changed);
            }
        }
        
        /**
         * Metoda ktora nastavi obrazok tohoto kontajneru
         * @param resImage 
         */
        public void setImage(BufferedImage resImage) {
            this.resImage = resImage;
        }
        
        /**
         * Metoda ktora nastavi detske kontajnery tohoto kontajneru. Kontajnery predane parametrom
         * <b>containers</b>
         * @param containers Nove detske kontajnery pre tento kontajner. 
         */
        public void setChildContainers(ArrayList<Container> containers) {
            this.childContainers = containers;            
        }
        
        /**
         * Metoda ktora nastavi rodicovsky kontajner tohoto kontajneru
         * @param parent Novy rodicovsky kontajner 
         */
        public void setParentContainer(Container parent) {
            this.parent = parent;
        }
        
        // </editor-fold>
        
        // </editor-fold>

        // <editor-fold defaultstate="collapsed" desc=" Privatne metody ">
        
        /**
        * Metoda ktora nastavi Layout pre komponentu zadanu parametrom <b>component</b>.
        * Layout na pridanie ziskame podla toho co sa nachadza 
        * v UiResource priradenemu k tomuto kontajneru, 
        */
        private void setLayout(SwingComponent component) {
        switch (resource.getLayoutType()) {
            case GRIDBAGSWING : {
                component.setLayout(new GridBagLayout());                
            } break;
            case GRIDSWING : {
                try {                
                component.setLayout(new GridLayout(resource.getX(), resource.getY()));
                } catch (Exception e) {
                    Logger.getLogger(getClass().getName()).log(Level.WARNING, StringResource.getResource("_iparam"), "width/height in GridLayout");
                }                
            } break;
            case BORDERSWING : {
                component.setLayout(new BorderLayout(resource.getHGap(), resource.getVGap()));                
            } break;
            case FLOWSWING : {
                component.setLayout(new FlowLayout(resource.getAlign(), resource.getHGap(), resource.getVGap()));
            } break;
            case INGAME : {      
                component.setLayout(null);
                component.setBackground(Color.BLACK);
            } break;
        }              

    }
    // </editor-fold>
}

