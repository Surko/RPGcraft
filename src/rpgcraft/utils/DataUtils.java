/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Color;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.MainGameFrame;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.graphics.ui.menu.Menu;
import rpgcraft.panels.GameMenu;
import rpgcraft.panels.components.Component;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingBar;
import rpgcraft.panels.components.swing.SwingImage;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.panels.components.swing.SwingImageList;
import rpgcraft.panels.components.swing.SwingImagePanel;
import rpgcraft.panels.components.swing.SwingInputText;
import rpgcraft.panels.components.swing.SwingText;
import rpgcraft.panels.listeners.Action;
import rpgcraft.panels.listeners.ActionEvent;
import rpgcraft.panels.listeners.ListenerFactory;
import rpgcraft.plugins.AbstractMenu;
import rpgcraft.plugins.DataPlugin;
import rpgcraft.plugins.Listener;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.UiResource;

/**
 * Utility trieda ktora v sebe zdruzuje rozne metody pre pracu s datami. Trieda je cela staticka =>
 * mozne k nej pristupovat z kazdej inej triedy ci instancie. 
 * Metoda dokaze rozparsovavat text a vytvori polia s datami, ci rozdelovat text
 * a vratit jednotlive rozparsovane casti.
 */
public class DataUtils {        
    // <editor-fold defaultstate="collapsed" desc=" Pomocne triedy a enumy ">           
    
    /**
     * Mozne datove polozky napriklad v SwingBare.
     */
    public enum DataValues {
        HEALTH,
        MAXHEALTH,
        STAMINA,
        MAXSTAMINA,        
        MANA,
        MAXMANA
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final Logger LOG = Logger.getLogger(DataUtils.class.getName());    
    
    private volatile static ExecutorService es = Executors.newCachedThreadPool();
    private volatile static int depth; 
    private volatile static int cycleCounter;
    
    public static ConcurrentHashMap<String, Object> variables = new ConcurrentHashMap<>();
    // </editor-fold>
    
    /**
     * Metoda ktora rozparsuje text zadany parametrom <b>data</b>. Text rozparsuvavame
     * pomocou rozdelovaca <b>delim</b>. Metoda rozparsovava iba plytko, co znamena 
     * ze ked najdeme znak ktory naznacuje hlbku parameter <b>sDepth</b> tak najdene oddelovace
     * v takej hlbke vynechavame.
     * @param data Text ktory rozparsovavame
     * @param delim Oddelovac ktorym rozdelujeme
     * @param sDepth Zaciatocny znak pre hlbkovost
     * @param eDepth Konecny znak pre hlbkovost
     * @return Plytko rozparsovany text
     */
    public static String[] split(String data, char delim, char sDepth, char eDepth) {
        if (data.equals("")) {
            return null;
        }
        int depth = 0;
        
        ArrayList<String> retList = new ArrayList<>();
        int beg = 0;
        for (int i = 0; i < data.length(); i++) {
            if (data.charAt(i) == sDepth) {
                depth++;
                continue;
            }
            if (data.charAt(i) == eDepth) {
                depth--;
                continue;
            }
            if (data.charAt(i) == delim && depth == 0) {                
                retList.add(data.substring(beg,i));
                beg = i + 1;
            }
        }
        retList.add(data.substring(beg));
        return retList.toArray(new String[0]);        
        
    }
    
    /**
     * Metoda ktora rozparsuje data zadane v parametri <b>data</b>. O rozparsovanie
     * sa postarametoda getDataFromString ktora vrati list listov s jednotlivymi riadkami
     * v ktorych su jednotlive data ziskane z textu.
     * @param data Text na rozparsovanie a ziskanie dat
     * @param srcObject Zdrojovy objekt z ktoreho ziskavame data
     * @return 2D pole objektov ktore ziskame z listu listov z metody getDataFromString.
     */
    public synchronized static Object[][] getDataArrays(String data, Object srcObject) {                
        ArrayList<ArrayList<Object>> resultList = new ArrayList<>();
        
        depth = 0;
        cycleCounter = 0;
        
        // Navratenie dat podla stringu data
        getDataFromString(data, srcObject, resultList);   
        
        if (depth != 0) {
            LOG.log(Level.SEVERE, StringResource.getResource("_dldepth"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("_dldepth"),
                    null).renderSpecific(StringResource.getResource("_label_parsingerror"));           
        }
        
        int height = resultList.size();
        int width = 0;
        if (height > 0) {
            width = resultList.get(0).size();
        }
        Object[][] resultArray = new Object[height][width];
        
        for (int i = 0; i < resultArray.length; i++) {
            resultArray[i] = resultList.get(i).toArray();
        }
        
        depth = 0;
        return resultArray;
    }    
    
    /**
     * Metoda ktora rozparsuje data zadane v parametri <b>data</b>. V texte sa mozu nachadzat rozne znaky
     * ( '[]#' ), podla ktorych rozhodujeme ako vytvarame list s datami (viac popisane v manuali
     * ako tvorit data). Metoda je vacsinou volana z inych metod na ziskanie listu listov.<br>
     * <p>
     * Zakladny popis znakov : <br>
     * '@' : pouzitie StringResource na ziskanie textu <br>
     * '[]' : zacatie/ukoncenie jedneho riadku pola <br>
     * '#' : pouzitie nadefinovanych dat z DataPluginov.
     * ',' : oddelovac jednotlivych stlpcov v riadku
     * </p>
     * @param data Data z ktorych ziskavame polia
     * @param srcObject Zdrojovy objekt z ktoreho ziskavame udaje
     * @param result Navratovy list listov s riadkami s jednotlivymi datami
     * @return Index na ktorom sme skoncili parsovanie.
     */
    private static int getDataFromString(String data, Object srcObject, ArrayList<ArrayList<Object>> result) {
        int index = 0;
        
        cycleCounter++;
        if (cycleCounter > 100) {
            LOG.log(Level.SEVERE, StringResource.getResource("cyclestate"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("cyclestate"),
                    null).renderSpecific(StringResource.getResource("_label_cycle"));
        }
        while (data.length() > index) {                        
            switch (data.charAt(index)) {                
                case '[' : {
                   if (depth == 0) {
                       index++;
                       depth++;
                       break;
                   }
                   if (depth == 1) {                       
                       depth++;
                       index++;
                       ArrayList<Object> row = new ArrayList<>();                       
                       index += getDataArray(data.substring(index), srcObject, row);
                       result.add(row);
                   }                                       
                } break; 
                case ']' : {
                    depth--;  
                    index++;
                } break;
                case ',' : {
                    index++;
                } break;
                case '#' : {
                    index++;
                    if (depth == 1) {
                        String inf = "";
                        char c = data.charAt(index);
                        while (c != '(') {
                            inf += c;
                            c = data.charAt(++index);
                        }           
                        
                        c = data.charAt(++index);
                        ArrayList<String> intParams = new ArrayList();
                        String param = "";
                        while (c != ')') {
                            if (c == ',') {
                                c = data.charAt(++index);
                                intParams.add(param);
                                param = "";
                            } else {                                
                                param += c;
                                c = data.charAt(++index);
                                if (c == ')') {
                                    index++;
                                    intParams.add(param);
                                    param = "";
                                }
                            }                            
                        }
                        getInfToList(inf, intParams, srcObject, result, null);                        
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                    }
                }                                    
            }
            
        }
        return index;
    }
    
    /**
     * Metoda ktora rozparsuje data zadane v parametri <b>data</b>. V texte sa mozu nachadzat rozne znaky
     * ( '[]#' ), podla ktorych rozhodujeme ako vytvarame list s datami (viac popisane v manuali
     * ako tvorit data). Metoda je vacsinou volana z inych metod na ziskanie zanorovacich
     * poli len do jedneho.<br>
     * <p>
     * Zakladny popis znakov : <br>
     * '@' : pouzitie StringResource na ziskanie textu <br>
     * '[]' : zacatie/ukoncenie jedneho riadku pola <br>
     * '#' : pouzitie nadefinovanych dat z DataPluginov.
     * ',' : oddelovac jednotlivych stlpcov v riadku
     * </p>
     * @param data Data z ktorych ziskavame polia
     * @param srcObject Zdrojovy objekt z ktoreho ziskavame udaje
     * @param result Navratovy list s jednotlivymi datami
     * @return Index na ktorom sme skoncili parsovanie.
     */
    private static int getDataArray(String data, Object srcObject, ArrayList<Object> result) {
        int index = 0;
        
        cycleCounter++;
        if (cycleCounter > 100) {
            LOG.log(Level.SEVERE, StringResource.getResource("cyclestate"));
            new MultiTypeWrn(null, Color.red, StringResource.getResource("cyclestate"),
                    null).renderSpecific(StringResource.getResource("_label_cycle")); 
            return 0;
        }
        
        while (data.length() > index) {      
            switch (data.charAt(index)) {
                case '@' : {
                        index++;
                        if (depth > 1) {
                            String parsed = "";
                            char c = data.charAt(index);
                            while ((c != ']')&&(c != ',')) {
                                parsed += c;
                                c = data.charAt(++index);
                            }                            
                            result.add(StringResource.getResource(parsed));
                        } else {
                            LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                        }
                    } break;
                case ']' : {
                    depth--;  
                    index++;
                    return index;
                }
                case '[' : {
                    index++;
                    if (depth > 1) {
                        depth++;                    
                       getDataArray(data.substring(index), srcObject, result);
                    }
                } break;
                case '#' : {
                    index++;
                    if (depth > 1) {
                        String inf = "";
                        char c = data.charAt(index);
                        while (c != '(') {
                            inf += c;
                            c = data.charAt(++index);
                        }           
                        
                        index++;
                        ArrayList<String> intParams = new ArrayList();
                        String param = "";
                        while (c != ')') {
                            if (c == ',') {
                                intParams.add(param);
                            } else {
                                param += c;
                            }                            
                        }
                        getInfToList(inf, intParams, srcObject, null, result);                        
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                    }
                } break; 
                case ',' : {
                    index++;
                } break;
                default : {
                    if (depth > 1) {
                        String parsed = "";
                        char c = data.charAt(index);
                        while ((c != ']')&&(c != ',')) {
                            parsed += c;
                            c = data.charAt(++index);
                        }
                        result.add(parsed);                   
                    } else {
                        LOG.log(Level.WARNING, StringResource.getResource("reachstate"));
                    }
                }    
            }
        }
        return index;
    }

    
    /**
     * Metoda ma za ulohu poskladat do jedneho ArrayListu result informacie podla daneho tagu
     * Informacie mozme dostat dvoma sposobmi : <br>
     * - v liste <b>result1</b> je list listov. Tato moznost pouzivana ked volame tuto metodu
     * z metody getDataFromString. <br>
     * - v liste <b>result2</b> je list s datami. Tato moznost je pouzivane ked volame tuto metodu
     * z metody getDataArray. <br> 
     * Pocitame s tym ze jeden z listov predanych ako parameter je nenulovy.
     * @param inf Tag podla ktoreho vyberie informacie do listu
     * @param param Parametre podla ktorych vyplni vysledny list.
     * @param result1 List listov v ktorom su ulozene informacie po riadkoch.
     * @param result2 List s informaciami/datami v ktorom su ulozene informacie sekvencne.List sluzi ako jeden riadok
     */    
    private static void getInfToList(String inf, ArrayList<String> param, Object srcObject,  
            ArrayList<ArrayList<Object>> result1, ArrayList<Object> result2) {         
        for (DataPlugin plug : DataPlugin.getAllPlugins()) {
            if (plug.getData(inf, param, srcObject, result1, result2)) {
                return;
            }
        }
    }
    
    /**
     * Metoda ktora vraci specificky aktualny cas a datum podla vzoru zadaneho parametrom <b>param</b>
     * Tento vzor dokazu spracovavat objekty dediace od DateFormat. Na zistenie casu
     * pouzivame metodu getTime z instancie Calendar.
     * @param param Format datumu.
     * @return Text s aktualnym datumom
     * @see DateFormat
     */
    public static String getSpecificDate(String param) {
        DateFormat dateFormat = new SimpleDateFormat(param);
        
        Calendar calendar = Calendar.getInstance();
        
        return dateFormat.format(calendar.getTime());
    }
    
    /**
     * Metoda ktora vraci specificky cas a datum podla vzoru zadaneho parametrom <b>param</b>
     * Tento vzor dokazu spracovavat objekty dediace od DateFormat. Na zistenie casu
     * pouzivame metodu getTime z instancie Calendar. Cas ziskavame z parametru <b>time</b>
     * @param time Cas ulozeny v long tvare.
     * @return Text s casom
     * @see DateFormat
     */
    public static String getSpecificDate(long time) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date = new Date(time);                
        return dateFormat.format(date);
    }
    
    /**
     * Metoda ktora vraci specificky aktualny cas a datum podla vzoru dd/MM/yyyy HH:mm:ss.
     * Tento vzor dokazu spracovavat objekty dediace od DateFormat. Na zistenie casu
     * pouzivame metodu getTime z instancie Calendar.     
     * @return Text s aktualnym datumom
     * @see DateFormat
     */
    public static String getCurrentDate() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        
        Calendar calendar = Calendar.getInstance();
        
        return dateFormat.format(calendar.getTime());
        
    }
    
    /**
     * Metoda ktora vrati kolekciu kontajnerov nachadzajuce sa v hlavnom game kontajneri.
     * @return Kolekcia kontajnerov
     */
    public static Collection<rpgcraft.panels.components.Container> getPanelContainers() {
        return MainGameFrame.game.getChildContainers();
    }
    
    /**
     * Metoda ktora vrati komponentu ktora sa nachadza v kontajneri s id rovnym zadanemu 
     * parametru <b>id</b>.
     * @param id Id kontajneru v ktorom hladame komponentu.
     * @return Komponenta pod danym id.
     */
    public static Component getComponentOfId(String id) {
        Collection<rpgcraft.panels.components.Container> containers = MainGameFrame.game.getChildContainers();
        for (rpgcraft.panels.components.Container cont : containers) {
            if (cont.getResource().getId().equals(id)) {
                return cont.getComponent();
            }
        }
        return null;
    }
    
    /**
     * Metoda ktora vrati vsetky kontajnery ktore sa nachadzaju v menu zadanom parametrom
     * <b>menu</b>
     * @param menu Menu v ktorom hladame kontajneri.
     * @return Kolekcia kontajnerov v menu.
     */
    public static Collection<rpgcraft.panels.components.Container> getMenuContainers(AbstractMenu menu) {
        return menu.getContainers();
    }
    
    /**
     * Metoda ktora vrati Kontajner ktory v sebe obsahuje komponentu ktoru vytvarame
     * z UiResource zadaneho parametrom <b>resource</b>. Novemu kontajneru pridavame rodicovsky kontajner
     * zadany v parametri <b>parent</b> a podla toho ci je parameter <b>src</b> null 
     * sa rozhodneme ci vytvarame novy kontajner alebo menime uz vytvoreny.
     * @param resource UiResource z ktoreho vytvarame kontajner
     * @param menu Menu v ktoom je kontajner.
     * @param src Zdrojovy kontajner ktory menime
     * @param parent Rodicovsky kontajner ktory pridavame novo vytvorenemu kontajneru
     * @return Novo vytvoreny kontajner
     */
    public static Container getComponentFromResource(UiResource resource, AbstractMenu menu, Container src, Container parent) {
        Component c = null;
        
        if (src == null) {        
            src = new Container(resource, 0, 0, 0, 0, parent);
        } else {
            if (src.getComponent() != null) {
                parent.getComponent().removeComponent(src.getComponent());
                src.setParentContainer(parent);
            }
        }
        
        switch (resource.getUiType()) {
            case BAR : c = new SwingBar(src, menu);
                break;
            case BUTTON : c = new SwingImageButton(src, menu);
                break;
            case EDITTEXT : c = new SwingInputText(src, menu);
                break;
            case LIST : c = new SwingImageList(src, menu);
                break;
            case PANEL : c = new SwingImagePanel(src, menu);
                break;
            case TEXT : c = new SwingText(src, menu);
                break;  
            case IMAGE : c = new SwingImage(src, menu);
                break;
        }
        
        if (c != null) {            
            c.addActionListeners(resource.getMouseActions());   
            c.addActionListeners(resource.getKeyActions()); 
            src.setComponent(c);                                                
        }

        parent.addChild(src);        
        
        return src;
    }        
    
    /**
     * Metoda ktora vrati data do SwingBar. Mozne hodnoty su iba HEALTH, MAXHEALTH,
     * STAMINA, MAXSTAMINA kedze ostatne hodnoty nie su tak casto menitelne alebo nie su
     * dostatocne zaujimave do SwingBaru.
     * @param c Komponenta z ktorej ziskavame udaje o hracovi.
     * @param dataValue DataValues co sa hodnotu chceme vratit
     * @return Objekt ktory vystihuje aktualnu hodnotu hladaneho atributu.
     */
    public static Object getData(Component c, DataValues dataValue) {
        
        switch (dataValue) {
            case HEALTH : {
                Menu menu = c.getOriginMenu();
                if (menu instanceof GameMenu) {
                    return ((GameMenu)menu).getMap().player.getHealth();
                }
            } break;
            case MAXHEALTH : {
                Menu menu = c.getOriginMenu();
                if (menu instanceof GameMenu) {
                    return ((GameMenu)menu).getMap().player.getMaxHealth();
                }
            } break;
            case STAMINA : {
                Menu menu = c.getOriginMenu();
                if (menu instanceof GameMenu) {
                    return ((GameMenu)menu).getMap().player.getStamina();
                }
            } break;
            case MAXSTAMINA : {
                Menu menu = c.getOriginMenu();
                if (menu instanceof GameMenu) {
                    return ((GameMenu)menu).getMap().player.getMaxStamina();
                }
            } break;     
            default : return null;
                
        }
        return null;                
    }
    
    /**
     * Metoda ktora vrati hodnotu premennej zadanej v liste variables. Metoda je
     * dolezita pre pristup k premennym nastavenych z listenerov ci lua skriptov.
     * @param var Premennu ktorej hodnotu chceme
     * @return Hodnota premennej ako objekt
     */
    public static Object getValueOfVariable(String var) {
        return variables.get(var);
    }
    
    /**
     * Metoda ktora nastavuje hodnotu premennej v liste variables. Metoda je dolezita
     * pre nastavenie premenncyh z listenerov ci lua skriptov.
     * @param val Hodnota premennej ako objekt
     * @param var Premenna v ktorej bude ulozena hodnota
     */
    public static void setValueOfVariable(Object val, Object var) {
        variables.put(var.toString(), val);
    }       
    
    /**
     * Metoda ktora vrati z pola (2 prvkoveho) parsedOp porovnavatelne hodnoty.
     * Pre ziskanie takychto hodnot porovnavame objekty z pola ci su typu Integer
     * co je automaticky podedeny Comparable. Pri type String volame listener
     * na ziskanie navratovej hodnoty ktoru pretypovavame na Comparable, ked taky listener nefunguje
     * tak musi byt text cislo v stringovej podobe => musime parsovat.
     * @param parsedOp Objekty z ktorych ziskavame Comparable
     * @param e ActionEvent ktorym volame potencialny listener. 
     * @return Pole porovnavatelnych hodnot
     * @throws Vynimka pri pretypovavani
     */
    public static Comparable[] getComparableValues(Object[] parsedOp, ActionEvent e) throws Exception {
        Comparable[] compValues = new Comparable[2];
        
        if (parsedOp[0] instanceof Integer) {
            compValues[0] = (Integer)parsedOp[0];        
        }
        if (parsedOp[0] instanceof String) {
            Listener fst = ListenerFactory.getListener(parsedOp[0],false);        
            if (fst != null) {
                fst.actionPerformed(e);
                compValues[0] = (Comparable) e.getReturnValue();
            } else {
                compValues[0] = Integer.parseInt((String)parsedOp[0]);
            }
        }        

        if (parsedOp[1] instanceof Integer) {
            compValues[1] = (Integer)parsedOp[1];        
        }
        if (parsedOp[1] instanceof String) {
            Listener snd = ListenerFactory.getListener(parsedOp[1],false);        
            if (snd != null) {
                snd.actionPerformed(e);
                compValues[1] = (Comparable) e.getReturnValue();
            } else {
                compValues[1] = Integer.parseInt((String)parsedOp[1]);
            }
        } 
        
        return compValues;
        
    }
    
    /**
     * Metoda ktora vykona akciu zadanu parametrom vytvorenim noveho Threadu s touto akciou.
     * Z akcie sa zavola metoda run. Je dolezite aby akcia mala definovany ActionEvent. Inak sa nevykona nic.
     * @param action Akcia na vykonanie
     */
    public static void execute(Action action) {        
        es.execute(new Thread(action));
    }
    
    /**
     * Metoda ktora vykona Thread zadany parametrom t.     
     * @param t Thread ktory sa vykona
     */
    public static void execute(Thread t) {
        es.execute(t);           
    }
    
}
