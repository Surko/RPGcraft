/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.JButton;
import rpgcraft.panels.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.Cursor;
import rpgcraft.panels.components.ListModel;
import rpgcraft.panels.components.ingame.InGameButton;
import rpgcraft.panels.components.ingame.InGameText;
import rpgcraft.resource.UiResource;

/**
 *
 * @author Surko
 */
public class SwingImageList extends SwingImagePanel {
     
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    ListModel model;
    boolean changedList;
    Container[] containers;
    int w,h;
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    
    /**
     * Konstruktor s rovno zadanym modelom.
     * @param container Kontajner ktory tvori prvok v liste
     * @param menu Menu z ktoreho List pochadza.
     * @param model Model podla ktoreho vykreslujeme prvky v liste
     */
    public SwingImageList(Container container, AbstractMenu menu, ListModel model) {
        super(container, menu);
        this.model = model;    
        changedList = true;
    }
    
    /**
     * Konstruktor ktory ma zadane data. Stlpce si ziskava automaticky podla 
     * metody getColumns.
     * @param container Kontajner ktory tvori prvok v liste
     * @param menu Menu z ktoreho List pochadza.
     * @param data Data na zobrazenie prvkov v liste
     */
    public SwingImageList(Container container, AbstractMenu menu, Object[][] data) {
        super(container, menu);
        ArrayList<String> columns = new ArrayList<>();
        getColumns(container.getResource(), columns);        
        setModel(data, columns.toArray(new String[0]));
        changedList = true;
    }
    
    /**
     * Konstruktor so zadanymi stlpcami. Vytvori instanciu SwingImageList s tym
     * ze dopredu predavame stlpce ktore budeme pouzivat.
     * @param container Kontajner ktory tvori prvok v liste
     * @param menu Menu z ktoreho List pochadza.
     * @param data
     * @param columns 
     */
    public SwingImageList(Container container, AbstractMenu menu, Object[][] data, String[] columns) {
        super(container, menu);        
        setModel(data, columns);
        changedList = true;
    }
    
    /**
     * Konstruktor bez blizsie urcenych dat na zobrazenie. Data su mozne pridat 
     * metodami setModel.
     * @param container Kontajner ktory tvori prvok v liste
     * @param menu Menu z ktoreho List pochadza.
     */
    public SwingImageList(Container container, AbstractMenu menu) {
        super(container, menu);   
        changedList = true;
        // Neutriedena Mapa zadana pomocou linkedHashMap.
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Privatne metody ">
    
    private void getColumns(UiResource res, ArrayList<String> columns) {
        int[] dimensions = new int[2];
        if (res.getElements() != null) {
            for (UiResource _res : res.getElements()) {                
                if (columns != null) {
                    columns.add(_res.getId());
                }                
                getColumns(_res, columns);
            }            
            
        }
    }
    
    /**
     * Metoda ktora vrati velkost (dolezita je hlavne vyska) najvacsieho z detskych komponentov (prvky listu) od 
     * Containeru zadanym ako parameter cont. Normalne by mohlo fungovat ze je len 
     * jeden prvok v liste (moze byt panel a ten by mal dalsie panely, atd), ale
     * v tomto pripade uvazujeme aj viacero elementov v containery. Kazdy kontajner tvori jeden prvok v liste
     * a vyhladanim maxima z detskych containerov budu vsetky mensie korektne vykreslene.
     * @param cont Kontajner z ktoreho ziskavame maxima.
     * @return Maximum z prvkov listu.
     */
    private int[] getElementsDimensions(Container cont) {
        int[] dimensions = new int[2];
        if (cont.getChildContainer() != null) {
            for (Container _cont : cont.getChildContainer()) {                                               
                int[] _dimensions = getElementsDimensions(_cont);
                dimensions[0] =  dimensions[0] > _dimensions[0] ? dimensions[0] : _dimensions[0];
                dimensions[1] =  dimensions[1] > _dimensions[1] ? dimensions[1] : _dimensions[1];
            }
            return dimensions;
        } else {                         
            dimensions[0] = cont.getWidth() > cont.getMinWidth() ? cont.getWidth() : cont.getMinWidth();
            dimensions[1] = cont.getHeight() > cont.getMinHeight() ? cont.getHeight() : cont.getMinHeight();
            return dimensions;
        }
    }  
    
    /**
     * Metoda ktora vrati pole s nakopirovany kontajnermi 
     * @param rows
     * @return 
     */
    private Container[] getCopiedContainers(int rows) {
        Container[] containers = new Container[rows];
        int size = componentContainer.getChildContainer().size();
        for (int i = 0; i < rows; i++) {
            containers[i] = new Container(componentContainer.getChildContainer().get(i % size));
        }        
        return containers;
    }
    
    /**
     * Metoda vytvori pocet prvkov listu zadane podla parametru rows. 
     * Vykona sa rows-krat skopirovanie kontajneru, ktore su prvkami v liste a nasledne metodou
     * fillContainer vyplni tieto okopirovane kontajnery.
     * @param rows Pocet prvkov listu na vykreslenie
     */
    private void makeTemplateList(int rows) {
        
        Cursor c = model.getCursor();
        
        // Vytvorime rows-krat pocet kontajnerov
        this.containers = getCopiedContainers(rows);
        
        c.makeIterator();        
        for (Container cont : containers) {
            if (c.hasNext()) {
                c.next();         
                fillContainer(cont, c, 0);
            }
            
        }        
        
    }
        
    /**
     * Metoda fillContainer vyplni kontajner z aktualnej pozicie kursoru. Vyplnanie 
     * prebieha tak ze si udrzujeme index aktualneho stlpca a podla metod 
     * getXXX v kurzore ziskame informacie do kontajneru. V tomto pripade vieme
     * postupnost stlpcov tak nevyuzivam premennu columns.
     * @param cont Kontajner od ktoreho vyplnujeme
     * @param c Kurzor z ktoreho ziskavam informacie
     * @param columns Stlpce podla ktorych rozhodujeme 
     * @param index 
     */
    private void fillContainer(Container cont, Cursor c, Integer index) {
                Component comp = cont.getComponent();
                if (comp instanceof SwingImageButton) {
                    ((SwingImageButton)comp).setText(c.getString(index++));                    
                }
                if (comp instanceof InGameButton) {
                    ((InGameButton)comp).setText(c.getString(index++));
                }
                if (comp instanceof SwingText) {
                    ((SwingText)comp).setText(c.getString(index++));                    
                }
                if (comp instanceof InGameText) {
                    ((InGameText)comp).setText(c.getString(index++));
                }    
                
                if (cont.getChildContainer() != null) {
                    for (Container _cont : cont.getChildContainer()) {
                        fillContainer(_cont, c, index);
                    }
                }
    }
    
    /**
     * Metoda fillContainer vyplni kontajner z aktualnej pozicie kursoru. Vyplnanie 
     * prebieha podla metod 
     * getXXX kde z kurzoru ziskame informacie do kontajneru. V tomto pripade
     * bude tato metoda volana s tym ze stavba kontajneru (id potomkov a ich potomkov) je odlisna od toho
     * ako su rozostavene v zadanych stlpcoch. <br>
     * <br> 
     * <b>Priklad :</b> <br>
     * Nech data su predane takto : <br>
     * data[x,0] = Image1; <br>
     * data[x,1] = Text; <br>
     * data[x,2] = Image2; <br>
     * <br>
     * Nech kontajner ma v sebe elementy v poradi vyplnanie : <br>
     * Text(id="1id"),Image2(id="2id"),Image1(id="3id") <br>
     * <br>
     * V takomto pripade spravne rozlozenie elementov dosiahneme tym ze pole columns bude obsahovat : <br>
     * columns[0] = "3id";
     * columns[1] = "1id";
     * columns[2] = "2id";
     * 
     * @param cont Kontajner od ktoreho vyplnujeme
     * @param c Kurzor z ktoreho ziskavam informacie
     * @param columns Stlpce podla ktorych rozhodujeme 
     * @param index 
     */
    private void fillContainer(Container cont, Cursor c, String[] columns) {
                Component comp = cont.getComponent();
                int index = c.getColumnIndex(cont.getResource().getId());
                if (comp instanceof SwingImageButton) {
                    ((SwingImageButton)comp).setText(c.getString(index));                    
                }
                if (comp instanceof InGameButton) {
                    ((InGameButton)comp).setText(c.getString(index));
                }
                if (comp instanceof SwingText) {
                    ((SwingText)comp).setText(c.getString(index));                    
                }
                if (comp instanceof InGameText) {
                    ((InGameText)comp).setText(c.getString(index));
                }    
                
                if (cont.getChildContainer() != null) {
                    for (Container _cont : cont.getChildContainer()) {
                        fillContainer(_cont, c, columns);
                    }
                }
    }  
    
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public metody ">
    
    // <editor-fold defaultstate="collapsed" desc=" Modelove nastavenia ">
    public void setModel(Object[][] objects, String[] columns) {
        model = new ListModel(objects, columns);
        
    }
            
    public void setModel(Object[][] objects) {
        model = new ListModel(objects);
    }
    
    public void setModel(ListModel model) {
        this.model = model;        
    }
    
    public void setColumns(String[] columns) {
        model.setColumns(columns);
    }
    
    public String[] getColumns() {
        return model.getCursor().getColumns();
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Kresliace metody ">
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (changedList) {
            
                                            
            changedList = false;
        }
        
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update metody ">
    @Override
    public void update() {
        
        int[] dim = getElementsDimensions(componentContainer);
        int rows = getHeight() / dim[1];
        int r = getHeight();
        if (rows > 0) {
            makeTemplateList(rows);
            changedList = true;
            
            GridBagConstraints gc = new GridBagConstraints();            
            containers[1].setComponent(new SwingText(containers[1], null));
            for (Container cont : containers) {
                this.add(cont.getSwingComponent(),gc);
                gc.gridy += 1;
                
            }
            
        }
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Eventy ">
    @Override
    public void fireEvent(ActionEvent event) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void addActionListener(ActionListener listener) {
    }

    @Override
    public void removeActionListener(ActionListener listener) {
    }
    // </editor-fold>
    
    // </editor-fold>
}
