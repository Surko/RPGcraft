/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels.components.swing;

import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.Point;
import java.awt.event.ActionListener;

import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.handlers.InputHandle;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.Cursor;
import rpgcraft.panels.components.ListModel;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;
import rpgcraft.resource.UiResource.ClickType;
import rpgcraft.resource.UiResource.LayoutType;
import rpgcraft.resource.types.ListType;
import rpgcraft.utils.DataUtils;
import rpgcraft.utils.MathUtils;

/**
 *
 * @author Surko
 */
public class SwingImageList extends SwingImagePanel {
     
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    ListModel model;
    boolean changedList;
    ArrayList<Container> containers;    
    int w,h;
    int selected = -1, jump = 0;
    int rows,cols,table;
    ListType lType;
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
        lType = (ListType)container.getResource().getType();
        this.model = model;    
        changedList = true;
        addMouseListener(this);
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
        lType = (ListType)container.getResource().getType();
        ArrayList<String> columns = new ArrayList<>();
        getColumns(container.getResource(), columns);       
        columns.add(0, "_id");
        setModel(data, columns.toArray(new String[0]));
        changedList = true;
        addMouseListener(this);
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
        lType = (ListType)container.getResource().getType();
        setModel(data, columns);
        changedList = true;
        addMouseListener(this);
    }
    
    /**
     * Konstruktor bez blizsie urcenych dat na zobrazenie. Data su mozne pridat 
     * metodami setModel.
     * @param container Kontajner ktory tvori prvok v liste
     * @param menu Menu z ktoreho List pochadza.
     */
    public SwingImageList(Container container, AbstractMenu menu) {
        super(container, menu);   
        lType = (ListType)container.getResource().getType();
        changedList = true;
        ArrayList<String> columns = new ArrayList<>();
        getColumns(container.getResource(), columns);        
        columns.add(0, "_id");
        
        setModel(DataUtils.getDataArrays(lType.getData(), menu), columns.toArray(new String[0]));
        
        addMouseListener(this);
        // Mouse listeners length
        // System.out.println(this.getMouseListeners().length);
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Privatne metody ">
    
    /**
     * Metoda getColumns vracia do listu columns stlpce zodpovedajuce id-ckam resource
     * ktore vytvaraju jeden podelement. Id-cka su radene do tohoto listu prefixovo. Takze
     * vkladanie dat do elementov musi takisto zodpovedat prefixovemu vkladaniu.
     * @param res Resource z ktoreho ziskame stlpce/id resourcov pod nim.
     * @param columns Stlpce do ktorych sa vkladali id resourcov.
     * @return ArrayList so stlpcami ktore zodpovedaju id-ckam resource.
     */
    private ArrayList<String> getColumns(UiResource res, ArrayList<String> columns) {
        if (res.getType().getElements() != null) {
            for (UiResource _res : res.getType().getElements()) {                
                if (columns != null) {
                    columns.add(_res.getId());
                }                
                getColumns(_res, columns);
            }
            return columns;            
        }
        return null;
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
            if (dimensions[1] == 0) dimensions[1] = 1;
            return dimensions;
        } else {                         
            dimensions[0] = cont.getWidth() > cont.getMinWidth() ? cont.getWidth() : cont.getMinWidth();
            dimensions[1] = cont.getHeight() > cont.getMinHeight() ? cont.getHeight() : cont.getMinHeight();
            if (dimensions[1] == 0) dimensions[1] = 1;
            return dimensions;
        }
    }  
    
    /**
     * Metoda ktora vrati pole s nakopirovany kontajnermi 
     * @param rows
     * @return 
     */
    private ArrayList<Container> getCopiedContainers(int rows) {
        ArrayList<Container> _containers = new ArrayList<>();        
        for (int i = 0; i < rows; i++) {
            _containers.add(new Container(componentContainer.getChildContainer().get(i)));
        }        
        return _containers;
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
                fillContainer(cont, c, 1);
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
                if (comp instanceof SwingText) {
                    ((SwingText)comp).setText(c.getString(index++));  
                }
                if (comp instanceof SwingImage) {
                    
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
                if (comp instanceof SwingText) {
                    ((SwingText)comp).setText(c.getString(index));                    
                }   
                
                if (cont.getChildContainer() != null) {
                    for (Container _cont : cont.getChildContainer()) {
                        fillContainer(_cont, c, columns);
                    }
                }
    }
    
    /**
     * Metoda selectItem ako nazov napoveda oznaci element na pozicii zadanej parametrami
     * x, y. Oznaceny prvok si uklada do parametru znameho v celom liste <b>selected</b>
     * <p>
     * <i> Opravena chyba s chybajucou else vetvou a posuvanim oznacenej komponenty </i>
     * </p>
     * @param x X-ova pozicia pre oznacenie
     * @param y Y-ova pozicia pre oznacenie
     * @return True/False ci oznacilo nejaky prvok
     */
    private boolean selectItem(int x, int y) {
        java.awt.Component c = getComponentAt(x, y);
        if (selected >= 0) {
            containers.get(selected).getComponent().unselect();
        }
        
        int i = 0;
        for (Container cont : containers) {
            if (cont.getSwingComponent() == c) {
                if (cont.getComponent().isNoData()) {                             
                    selected = -1;
                    changedList = true;
                    return false;
                }               
                
                selected = i;
                cont.getComponent().select();
                changedList = true;                
                return true;
            } else {
               i++;
            }
        }
        return false;
    }
    
    /**
     * Metoda selectItem ako nazov napoveda oznaci element v bode zadanom parametrom Point.
     * Oznaceny prvok si uklada do parametru znameho v celom liste <b>selected</b>
     * @param p Bod kde oznacujeme komponentu
     * @return True/False ci oznacilo nejaky prvok
     */
    private boolean selectItem(Point p) {
        return selectItem(p.x, p.y);
    }
            
    /**
     * Metoda oreze pocet riadkov o pocet dat ulozenych v modeli =>
     * minimum z tychto hodnot<br><br>
     * Pr. <br>
     * rows = 10 <br>
     * dataCount = 3 <br>
     * return = 3 <br>
     * ----------------------------- <br>
     * rows = 2 <br>
     * dataCount = 3 <br>
     * return 2 <br>
     *
     * @param rows Pocet riadkov ktore sa orezu
     * @return Orezany pocet riadkov
     */
    private int cropRowsByCount(int rows) {
        if (rows > model.getCursor().getCount()) {
            return model.getCursor().getCount();
        }
        return rows;
    }
    
    /**
     * Metoda ma za ulohu vratit podla typu riadkov a stlpcov 
     * prislusne hodnoty (Pocty riadkov a stlpcov) <br>
     * <b>Autotype</b> - cely mozny priestor <br>
     * <b>Defaultype</b> - jeden riadok / stlpec <br>
     * <b>Integer hodnoty</b> - priame cislo
     * 
     * @param rc Parameter podla ktoreho urcuje kolko riadkov/stlpcov pre prislusny typ
     * @return Pocet riadkov prisluchajuci pre prislusny typ.
     */
    private int getRCValues(int rc) {
        switch (rc) {
            case ListType.AUTOTYPE : {
               return Integer.MAX_VALUE;
            }
            case ListType.DEFAULTTYPE : {
                return 1;
            }
            default : {
                return rc;
            }
        }
    }
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Public metody ">
    
    // <editor-fold defaultstate="collapsed" desc=" Settery ">
    
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
    // </editor-fold>
    
     public void setColumns(String[] columns) {
        model.setColumns(columns);
    }
    
    public void incSelection() {
                
        if (table == 0 || selected + jump + 1 == containers.size()) return;
        
        if (selected >= 0)
            containers.get(selected).getComponent().unselect();
        
        if (selected - jump == table) {
            jump++;        
            this.removeAll();
            initializeListElements();            
        }
                
        if (containers.get(selected+1).getComponent().select()) {
            selected++;
        }
    }
    
    public void decSelection() {
        if (table == 0 || (selected <= 0 && jump <= 0))
            return;
        
        if (selected >= 0) {
            containers.get(selected).getComponent().unselect();
        }
        
        if (jump > 0 && selected % table == 0) {
            jump--;
            selected--;
            this.removeAll();
            initializeListElements();
        } else {
            selected--;
        }
        
        containers.get(selected).getComponent().select();
        
    }
    
    /**
     * Metoda vymaze komponentu ktora sa nachadza na pozicii zadanej parametrom <b>pos</b>
     * @param pos Pozicia komponenty ktoru vymaze
     */
    public void removeCompAtPosition(int pos) {
        if (pos < containers.size()) {                
            
            if (containers.get(pos).getComponent().isNoData()) return;
            
            if (selected > pos) {
                selected--;
                return;
            } else 
                if (selected == pos) {
                    selected = -1;
                }
            model.remove(pos);
            this.removeAll();
            containers.remove(pos);
            initializeListElements();
            
        }
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda vrati z modelu pre list stlpce podla ktorych doplna data do listu.
     * @return Pole Stringov ako vypis stlpcov
     */
    public String[] getColumns() {
        return model.getCursor().getColumns();
    }
    
    /**
     * Metoda vrati oznacenu komponentu v liste
     * @return Komponenta ktora je oznacena v liste
     */
    public Component getSelectedComponent() {
        return containers.get(selected).getComponent();
    }
    
    /**
     * Metoda vrati cislo oznaceneho elementu
     * @return Cislo oznaceneho/selected elementu => premenna selected
     */
    public int getSelectedIndex() {
        return selected;
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
                
    /**
     * Metoda update volana pri kazdom aktualizovani okna, komponenty v ktorom sa nachadza tento list.
     * Kedze list je skalovatelny a jeho prvky takisto moze dojst pri zvacsanie kontajneru 
     * k zmene vykresleni poctu vsetkych elementov v liste. V tomto pripade vymazavame vsetky doposial vytvorene elementy
     * z listu (v istej dobe by sa dalo doplnit rozdelenie tejto funkcie ze ked
     * zostava pocet elementov rovnaky tak nebudeme vymazavat komponenty ale
     * iba nahradime doposial vytvorene s novymi udajmi) a nahradzujeme ich novymi 
     * poctom zistenych podla prislusnych funkcii (getMaxRows - zistenie maximalneho poctu riadkov,
     * getElementsDimensions - kolko zaberaju elementy v liste, cropRowsbyCount - orezanie 
     * na pocet dat ulozenych v modele). Nasledne vytvorime rows-krat pocet instancii
     * okopirovanych z prvku nachadzajucom sa v liste (definovanom v xml), ktory sluzi ako template pre vsetky vytvorene
     * a pridame do listu funkciou add zdedenu od Component (tymto padom nam opadne 
     * dalsia aktualizacia a aj prekreslovanie komponenty kedze List dedi od JPanelu, 
     * a ten si riesi vykreslovanie Swingom)
     */
    @Override
    public void update() {
        // resetnutie pocitadla oznaceneho elementu
        selected = -1;
        // Vymazanie doposial vsetkych komponentov vlozenych do listu
        this.removeAll();
        // Vybratie typu a pretypovanie na ListType (musi byt korektne)
        ListType lType = (ListType)type;
        // Pocet riadkov vo formate popisanom v ListType
        rows = lType.getMaxRows();  
        /*
         * Pocet stlpcov. Pri automatickom type vrati maximalnu moznu hodnotu integeru.
         * Tym padom budeme vzdy urcite zrezavat na velkost kontajneru.
         */
        cols = getRCValues(lType.getMaxCols());
        // Velkost tabulky
        table = 0;
        // Velkost prvku v liste ziskany z metody getElementsDimensions
        // dim[0] - width, dim[1] - height
        int[] dim = getElementsDimensions(componentContainer);
        
        switch (rows) {            
            case ListType.AUTOTYPE : {
                // Pri autotype zistime pocet moznych prvkov na pridanie do riadkov a stlpcov
                int hdivd = getHeight() / dim[1];
                int wdivd = getWidth() / dim[0];                
                switch (lType.getLayout()) {
                    case ListType.RowLayout.HORIZONTALROWS : {                               
                        // Ked uvazujeme horizontalne riadky tak stlpce obmedzujeme
                        // podla poctu prvkov na vysku. Riadky je maximum poctu prvkov na sirku                        
                        if (cols > hdivd) {
                            cols = hdivd;
                        }                        
                        rows = wdivd;
                        // Pocet prvkov v liste bude obmedzeny poctom dat v modeli
                        // a poctom volnych miest rows * cols
                        table = cropRowsByCount(rows * cols);
                    } break;
                    case ListType.RowLayout.VERTICALROWS : {
                        // Ked uvazujeme normalne vertikalne riadky tak stlpce obmedzujeme
                        // podla poctu prvkov na sirku (stlpce). Riadky naopak.    
                        if (cols > wdivd) {
                            cols = wdivd;
                        }                        
                        rows = hdivd;

                        // Pocet prvkov v liste bude obmedzeny poctom dat v modeli
                        // a poctom volnych miest rows * cols
                        table = cropRowsByCount(rows * cols);
                    } break;
                    case ListType.RowLayout.DIAGONALROWS : {
                        // Ked uvazujeme diagonalne riadky tak stlpce obmedzujeme
                        // podla poctu prvkov na sirku (stlpce). Riadky naopak. 
                        if (cols > wdivd) {
                            cols = wdivd;
                        }                        
                        rows = hdivd;

                        // Pocet prvkov v liste bude obmedzeny poctom dat v modeli
                        // a poctom prvkov ktore je minimum z rows a cols.
                        // Keby boli 2 riadky a 3 stlpce tak diagonala su iba 2 prvky.
                        // Keby boli 3 riadky a 2 stlpce tak diagonala su znova iba 2 prvky.
                        table = cropRowsByCount(Math.min(rows,cols));                        
                    } break;
                    case ListType.RowLayout.HORIZONTALROWSFIT : {
                        // Ked uvazujeme horizontalne riadky s plnou naplnou tak
                        // neberieme do uvahy riadky ani stlpce.  
                        // Stlpcov bude pocet prvkov na vysku, riadkov pocet prvkov
                        // na sirku
                        cols = hdivd;
                        rows = wdivd;
                        // Pocet prvkov v liste bude obmedzeny poctom dat v modeli
                        // a poctom volnych miest cols * rows
                        table = cropRowsByCount(cols * rows);
                    } break;
                    case ListType.RowLayout.VERTICALROWSFIT : {
                        // Ked uvazujeme vertikalne riadky s plnou naplnou tak
                        // neberieme do uvahy riadky ani stlpce.
                        // Stlpcov bude pocet prvkov na sirku, riadkov pocet prvkov
                        // na vysku.
                        cols = wdivd;
                        rows = hdivd;
                        // Pocet prvkov v liste bude obmedzeny poctom dat v modeli
                        // a poctom volnych miest cols * rows
                        table = cropRowsByCount(cols * rows);
                    } break;
                }
            } break;
            case ListType.DEFAULTTYPE : {
                // Default typ ma pocet riadkov rovny 1. 
                rows = 1;
                // Pocet prvkov v liste bude obmedzeny poctom stlpcov.
                // List bude 1 x [cols] velky. 
                table = cropRowsByCount(cols);
            } break;
            default : {
                // Vetva default v sebe ma explicitne zadane hodnoty rows.
                // Cols obmedzi podla poctu prvkov danych na sirku
                // Takze aj keby cols bolo auto (maxinteger) tak sa obmedzi na maximalny pocet
                // prvkov na sirku
                int wdivd = getWidth() / dim[0];
                if (cols > wdivd) {
                    cols = wdivd;
                }
                // Pocet prvkov v liste bude obmedzeny poctom dat v modeli 
                // a poctom volnych miest rows * cols 
                switch (lType.getLayout()) {
                    case ListType.RowLayout.DIAGONALROWS : {
                        table = cropRowsByCount(Math.min(rows, cols));
                    }
                        break;
                    default : {
                        table = cropRowsByCount(rows * cols);
                    }                    
                }
                
            } break;            
        }
        
        initializeListElements();
    }
    
    /**
     * Metoda initializeListElements ako nazov napoveda zinicializuje vsetky podelementy tohoto listu.
     * Pri Tabulke velkosti nula vytvori defaultnu tabulku s tym co sa nachadza v xml (napriklad NoData textove pole).
     * Inak vytvori tabulku velkosti table a podla LayoutType (GridBagSwing) a RowLayoutu elementov 
     * rozmiestni vsetky podelementy. Vyuzivam GridBagConstraints aj ked podelement nema layout GridBagSwing preto
     * lebo vyuzivam z neho na akom gride sa nachadzam a kontrolujem ci som nepresiahol
     * velkost tabulky (rows, cols). Pri layout type INGAME dam do kontajnera pozicie 
     * podelementu so startovacimi poziciami a zavolam metodu setBounds aby bol tento podelement spravne umiestneny.
     */
    public void initializeListElements() {        
        if (model.getSize() == 0 || table == 0) {
            makeTemplateList(1);
            changedList = true;
            
            GridBagConstraints gc;
            UiResource res = containers.get(0).getResource();
            // Pokolko sa zvacsuju x-ova a y-ova pozicia komponent.
            int xplus, yplus, x = 0, y = 0;
            // Branie do uvahy aj positioning zadany v xml.
            
            if (res.getLayoutType() == LayoutType.INGAME) {
                gc = new GridBagConstraints();
                int[] cpos = MathUtils.getStartPositions(res.getPosition(),
                    getWidth(), getHeight(), containers.get(0).getWidth(), containers.get(0).getHeight());
                // Zaciatocne x-ove a y-ove pozicie nastavime podla tych ziskanych z predchadzajucej metody (kedze positioning je prvorady)
                x = cpos[0];
                y = cpos[1];
            
            } else {
                gc = (GridBagConstraints) res.getType().getConstraints();
            }
            // Implicitne nastavene gridx a gridy aby mi komponenty neutekali
            // dobokov alebo aby neprekryvala jedna druhu
            gc.gridx = 0;
            gc.gridy = 0;
            for (Container cont : containers) {
                // Posuvanie x-ovej aj y-ovej pozicie musi byt podla toho aky bol velky kontajner v ktorom je element.
                xplus = cont.getWidth();
                yplus = cont.getHeight();
                // Resource pre kontajner aby sme zistili ci pracujeme s komponentou ktora nema ziadny layout.                
                
                if (res.getLayoutType() == LayoutType.INGAME) {
                    // V tomto pripade mame INGAME layout => nastavujeme pozicie a boundaries.
                    cont.setPositions(new int[] {x, y});
                    cont.getComponent().setBounds(x, y, xplus, yplus);
                }
                
                this.add(cont.getSwingComponent(), gc);
            }            
        } else {
            makeTemplateList(table);
            changedList = true;
            
            GridBagConstraints gc;
            UiResource res = containers.get(0).getResource();
            // Pokolko sa zvacsuju x-ova a y-ova pozicia komponent.
            int xplus, yplus, x = 0, y = 0;
            
            if (res.getLayoutType() == LayoutType.INGAME) {
                // Branie do uvahy aj positioning zadany v xml.
                int[] cpos = MathUtils.getStartPositions(containers.get(0).getResource().getPosition(),
                        getWidth(), getHeight(), containers.get(0).getWidth(), containers.get(0).getHeight());
                // Zaciatocne x-ove a y-ove pozicie nastavime podla tych ziskanych z predchadzajucej metody (kedze positioning je prvorady)
                x = cpos[0];
                y = cpos[1];                     

                // GridBagSwing layout nemusi byt vsade pouzity. V takom pripade sa ani neberie v uvahu.
                // Jedine z neho vyuzijem testovanie nato ci sme dosiahli maximum stlpcov alebo riadkov.
                gc = new GridBagConstraints();            
            } else {
                gc = (GridBagConstraints) res.getType().getConstraints();
            }
            // Implicitne nastavene gridx a gridy aby mi komponenty neutekali
            // dobokov alebo aby neprekryvala jedna druhu. Tieto hodnoty urcuju 
            // Kde v sietovej sustave sa komponenta nachadza. Lahke urcit kolko riadkov a stlpcov mame.
            gc.gridx = 0;
            gc.gridy = 0;
            
            for (int j = jump; j < containers.size(); j++) {
                // Posuvanie x-ovej aj y-ovej pozicie musi byt podla toho aky bol velky kontajner v ktorom je element.
                xplus = containers.get(j).getWidth();
                yplus = containers.get(j).getHeight();
                
                // Zistenie co sa deje pre nas layout (mozne doplnit o dalsie layouty)
                if (res.getLayoutType() == LayoutType.INGAME) {
                    // V tomto pripade mame INGAME layout => nastavujeme pozicie a boundaries.
                    containers.get(j).setPositions(new int[] {x, y});
                    containers.get(j).getComponent().setBounds(x, y, xplus, yplus);
                }
                
                // Pridame komponentu k listu s GridBagConstraints. Ked nie je gridbagswing tak sa to neberie v uvahu.
                this.add(containers.get(j).getSwingComponent(),gc);                
                
                // Zvysovanie riadku alebo stlpcu  
                switch (((ListType)type).getLayout()){
                    case ListType.RowLayout.HORIZONTALROWS : {                                                
                        gc.gridx++; 
                        x += xplus;
                        if (gc.gridx == rows) {
                            gc.gridx = 0;
                            x = 0;
                            y += yplus;
                            gc.gridy ++;                            
                        }                        
                    }
                        break;
                    case ListType.RowLayout.VERTICALROWS : {
                        gc.gridy++; 
                        y += yplus;
                        if (gc.gridy == rows) {
                            gc.gridy = 0;
                            y = 0;
                            x += xplus;
                            gc.gridx++;
                        }
                    }
                        break;
                    case ListType.RowLayout.DIAGONALROWS : {
                        gc.gridy++;
                        y += yplus;
                        x += xplus;
                        gc.gridx++;
                    } 
                        break;
                    case ListType.RowLayout.HORIZONTALROWSFIT : {
                        gc.gridx++;
                        x += xplus;
                        if (gc.gridx >= rows) {
                            gc.gridy++;
                            y += yplus;
                            x = 0;
                            gc.gridx = 0;
                        }
                    }
                        break;
                    case ListType.RowLayout.VERTICALROWSFIT : {
                        gc.gridy++;
                        if (gc.gridy  >= rows) {
                            gc.gridx++;
                            x += xplus;
                            y = 0;
                            gc.gridy = 0;
                        }
                    }
                        break;
                    default : {
                        String[] param = new String[]{"rowtype", componentContainer.getResource().getId()};
                        Logger.getLogger(getClass().getName()).log(Level.WARNING, StringResource.getResource("_rparam", param));
                    }
                }             

            }
        }
        
        this.updateUI();
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Eventy ">   
    
    @Override
    public void fireMouseEvent(Action action, ActionEvent event) {
        if (event.getClicks() >= action.getClicks()) {
            if (action.getClickType() == null) {
                performAction(action, event);
                return;
            }
            
            switch ((ClickType)action.getClickType()) {   
                case onListElement : {
                    if (event.getParam() instanceof Cursor) {
                        if (selected >= 0) {
                            Cursor c = (Cursor)event.getParam();
                            c.moveToPosition(selected);
                            performAction(action, event);
                        }
                    }
                } break;
                default : {
                    performAction(action, event);
                }
            }                    
        }                            
    }
    
    /**
     * Parametre : <br>
     * <b>onListElement</b>: Vykona akciu s tym ze parametre su na pozicii, ktora bola oznacena/selected =>
     * posunie poziciu kurzoru na selected poziciu.
     * <b>default</b>: Vykona sa defaultna akcia bez nejakeho posuvania kurzoru.
     * 
     * 
     * @param action
     * @param event 
     */
    @Override
    public void fireKeyEvent(Action action, ActionEvent event) {
        if (action.getClickType() == null) {
            performAction(action, event);
            return;
        }
        
        switch ((ClickType)action.getClickType()) {   
            case onListElement : {
                if (event.getParam() instanceof Cursor) {
                    if (selected >= 0) {
                        Cursor c = (Cursor)event.getParam();
                        c.moveToPosition(selected);
                        performAction(action, event);
                    }
                }
            } break;
            default : {
                performAction(action, event);
            }
        }                           
    }
    
    
    @Override
    public void processKeyEvents(InputHandle input) {     
        if (active) {
            if (_klisteners != null && !_klisteners.isEmpty()) {                                   
                for (Action action : _klisteners) {
                    if (input.clickedKeys.contains(action.getKey())) {
                        fireKeyEvent(action, new ActionEvent(this, 0, -1, action.getAction(), model.getCursor()));
                    }
                }
            }
        }
    }
    
    @Override
    public void mouseClicked(MouseEvent e) { 
        // System.out.println(this.getMouseListeners().length);
        if (active) {
            if (_mlisteners != null) {  
                if (selectItem(e.getX(), e.getY())) { 
                    ActionEvent event = new ActionEvent(this, 0, e.getClickCount(), null, model.getCursor());
                    for (int i = 0;i<_mlisteners.size() ;i++ ){  
                        if (_mlisteners.get(i) instanceof ActionListener) {
                            ActionListener listener = (ActionListener)_mlisteners.get(i);                
                            listener.actionPerformed(event);  
                            continue;
                        }
                        if (_mlisteners.get(i) instanceof Action) {
                            Action action = (Action)_mlisteners.get(i);

                            fireMouseEvent(action, event);

                            if (action.isTransparent()) {
                                ((Component)this.getParent()).mouseClicked(e);
                            }

                            continue;
                        }            
                    }
                }       
            }
        }
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

    // </editor-fold>
    
    // </editor-fold>
}
