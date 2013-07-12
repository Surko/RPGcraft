/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import rpgcraft.entities.Item;
import rpgcraft.entities.TileItem;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.map.tiles.Tile;
import rpgcraft.xml.RecipeXML;



/**
 * RecipeResource ktore dedi od AbstraktnehoResource je trieda ktora umoznuje
 * vytvorit recipe resources z xml suborov, ktore sa daju pouzit pri tvoreni predmetov.
 * Na vytvorenie nam pomaha metoda parse,
 * ktorej predavame jeden vrchol z xmlka na rozparsovanie. Vytvaranie noveho resource
 * prevadzame volanim metody newBundledResource. Navrat nejakeho resource pomocou metody
 * getResource. 
 * @see AbstractResource
 */
public class RecipeResource extends AbstractResource<RecipeResource>{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    private static final String rowDelim = "[|]", colDelim = "[,]";    
    private static final Logger LOG = Logger.getLogger(RecipeResource.class.getName());
    
    private static HashMap<String, RecipeResource> recipeResources = new HashMap<>();
    
    private static HashMap<Type, HashMap<Integer, ArrayList<RecipeResource>>> recipesByTypeAmount = new HashMap<>();
    
    public enum Type {
        SHAPED,
        SHAPELESS,
        GRIND
    }
    
    private int w = -1, h = -1;
    private int cellFill;
    private String id;
    private Type type;    
    private ArrayList<String> resultItems;
    private String[][] resCells;
    private String recipeText;
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    /**
     * Konstruktor (privatny) ktory vytvori instanciu RecipeResource z xml suboru.
     * Prvy element ktory parsujeme/prechadzame je zadany v parametri <b>elem</b>.
     * Po rozparsovani zvalidujeme quest a vlozime ho k ostatnym.
     * @param elem Xml element z ktoreho vytvarame resource
     */
    private RecipeResource(Element elem) {
        this.cellFill = 0;
        parse(elem);           
        validate();
        recipeResources.put(id, this);        
        putRecipe();
    }    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Staticke metody ">
    /**
     * Metoda ktora vytvori novy objekt typu RecipeResource. Kedze je staticka a public
     * tak sa tymto padom stava jediny sposob ako vytvorit instanciu RecipeResource.
     * @param elem Element z ktoreho vytvarame recept
     * @return RecipeResource z daneeho elementu
     */
    public static RecipeResource newBundledResource(Element elem) {
        return new RecipeResource(elem);                
    }
    
    /**
     * Metoda ktora vrati RecipeResource podla parametru <b>name</b>. Name
     * je id v xml.
     * @param name Meno RecipeResource ktore hladame
     * @return RecipeResource s danym menom
     */
    public static RecipeResource getResource(String name) {
        return recipeResources.get(name);
    }        
        
    // </editor-fold>        
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati taky RecipeResource ktory vyhovuje parametrom. Najprv 
     * skontrolujeme mozne mozne typy (parameter <b>types</b>) receptu a
     * pocet predmetov ktore tvoria recept parameter <b>cellFill</b>. Z takych resource ktore tomu vyhoveju
     * vyberieme take aby pasovali rozlozeniu predmetov v parametre <b>cells</b>
     * @param cells Vyplnenie pola/crafting okna predmetmi ktore kontrolujeme
     * @param cellFill Pocet predmetov tvoriacich vypln crafting okna.
     * @param types Mozne typy receptov ktore hladame.
     * @return RecipeResource ktory vyhovuje parametrom
     */
    public static RecipeResource getRecipe(Item[][] cells, int cellFill, Type[] types) {        
        if (cells == null) {
            return null;
        }
        
        for (Type type : types) {
            HashMap<Integer, ArrayList<RecipeResource>> typeRecipes = recipesByTypeAmount.get(type);
            if (typeRecipes == null) {
                continue;
            }
            ArrayList<RecipeResource> filledRecipes = typeRecipes.get(cellFill);
            if (filledRecipes == null) {
                continue;
            }
            for (RecipeResource res : filledRecipes) {
                if (isValidRecipe(res, cells)) {
                    return res;
                }
            }   
        }
        
        return null;

    }
    
    /**
     * Metoda ktora kontroluje RecipeResource a jeho rozlozenie predmetov ktore sme nacitali
     * pri inicializacii receptov. Recept je zadany parametrom <b>res</b>. Rozlozenie predmetov
     * ktore kontrolujeme su v parametri <b>cells</b>. Pri najdeni zhody vratime tento resource.
     * @param res Recept resource s ktorym kontrolujeme rozlozenie predmetov
     * @param cells Rozlozenie predmetov na skontrolovanie
     * @return Recept resource ktory vyhovuje rozlozeniu.
     */
    private static boolean isValidRecipe(RecipeResource res, Item[][] cells) {
        for (int i = 0; i < cells.length; i++) {
            for (int j = 0; j < cells[i].length; j++) {
                if (cells[i][j] == null) {
                    if (res.resCells[i][j] == null) {
                        continue;
                    } else {
                        return false;
                    }
                } else {
                    if (cells[i][j].getId().equals(res.resCells[i][j])) {
                        continue;
                    } else {
                        return false;
                    }
                }
            }                
        }
        return true;
    }        
 
    /**
     * Metoda ktora vrati list s textami/idckami predmetov ktore dostaneme pri vytvoreni predmetu.
     * @return List idcok predmetov ktore sa daju vytvorit z receptu
     */
    public ArrayList<String> getCraftedsItems() {
        return resultItems;
    }
    
    /**
     * Metoda ktora vrati list s predmetmi ktore dostaneme pri vytvoreni predmetu.
     * @return List predmetov ktore sa daju vytvorit z receptu
     */
    public ArrayList<Item> getCraftedItems() {
        ArrayList<Item> _resultItems = new ArrayList<>();
        
        for (String sItem : resultItems) {
            if (sItem.startsWith("tile:")) {
                int tileId = Integer.parseInt(sItem.substring(5));
                _resultItems.add(new TileItem(null, Tile.tiles.get(tileId)));
            } else {
                _resultItems.add(Item.createItem(null, EntityResource.getResource(sItem)));
            }
        }
        return _resultItems;
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Parsovanie ">
    /**
     * Metoda ktora rozparsuvava xml subor takym sposobom ze dostava ako parameter <b>elem</b>
     * co je jeden elem z xml suboru aj so vsetkymi jeho podelementami. Ulohou je prejst vsetky
     * tieto podelementy a podla mien tychto podelemntov vykonat definovane akcie.
     * (RecipeXML.RECIPETEXT -> do textu receptu prida obsah medzi tagmi.
     * @param elem Element z xml ktory rozparsovavame do RecipeResource
     */
    @Override
    protected final void parse(Element elem) {
        NodeList nl = elem.getChildNodes(); 
                
        for (int i = 0; i< nl.getLength();i++) {
            Node eNode = nl.item(i);
            switch (eNode.getNodeName()) {   
                case RecipeXML.ID : {
                    this.id = eNode.getTextContent();
                } break;
                case RecipeXML.TYPE : {
                    String sType = eNode.getTextContent();                    
                    try {
                        type = Type.valueOf(sType);                         
                    } catch (Exception e) {
                        // Nepoznany typ, pouzitie defaultneho typu
                        LOG.log(Level.WARNING, StringResource.getResource("_"));
                    }
                } break;
                case RecipeXML.RECIPETEXT : {
                    this.recipeText = eNode.getTextContent();
                } break;
                case RecipeXML.CRAFTINGRECIPE : {
                    String recipe = eNode.getTextContent();                    
                    if (recipe == null) {
                        LOG.log(Level.WARNING, StringResource.getResource("_brecipe"));
                    } else {
                        try {
                            parseRecipe(recipe);
                        } catch (Exception e) {
                            LOG.log(Level.SEVERE, StringResource.getResource("_erecipe"));
                            new MultiTypeWrn(e, Color.red, StringResource.getResource("_erecipe"),
                                    null).renderSpecific(StringResource.getResource("_label_resourcerror"));
                        }
                    }
                } break;
                case RecipeXML.RESULT : {
                    String[] results = eNode.getTextContent().split(colDelim);
                    if (results == null || results[0].equals("")) {
                        LOG.log(Level.WARNING, StringResource.getResource("_breciperesult"));
                    } else {
                        if (resultItems == null) {
                            resultItems = new ArrayList<>();
                        }
                        for (String result : results) {
                            resultItems.add(result);
                        }
                    }
                }
                default : {
                    // Nepoznany prikaz ---> preskocenie
                    LOG.log(Level.INFO, StringResource.getResource(""));
                }
                
            }
        }
    }
    
    /**
     * Metoda ktora rozparsuje recept zadany parametrom <b>recipe</b>. Aby bol text 
     * korektny riadky musia byt rozdelene rozdelovacom rowDelim a stlpce rozdelovacom
     * colDelim. Rozparsovane texty ulozime do pola resCells.
     * @param recipe Recipe v textovej podobe (deliminatory + id predmetov)
     */
    private void parseRecipe(String recipe) {
        
        String[] recipeRows = recipe.split(rowDelim);        
        
        h = recipeRows.length;                
        
        for (int i = 0; i < h; i++) {
            String[] recipeCols = recipeRows[i].split(colDelim);                        
            
            if (w == -1) {
                w = recipeCols.length;
                resCells = new String[h][w];
            }
                                    
            for (int j =0; j < w; j++) {
                if (recipeCols[j].equals("")) {
                    resCells[i][j] = null;
                } else {
                    resCells[i][j] = recipeCols[j];
                    cellFill++;
                }                
            }
        }
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc=" Pomocne metody ">
    /**
     * Metoda ktora pri vsunie recept do prislusnych map a listov. Kazdy recept ma nejaky pocet
     * obsadeni predmetov v jednotlivych riadkoch a stlpcoch. Taktiez maju recepty urcity typ (SHAPED, SHAPELESS, atd...)
     * Podla takychto zloziek ukladame recepty do prislusnych map.
     */
    private void putRecipe() {
        
        HashMap<Integer, ArrayList<RecipeResource>> typeRecipes = recipesByTypeAmount.get(type);
        
        if (typeRecipes == null) {
            typeRecipes = new HashMap<>();
            recipesByTypeAmount.put(type, typeRecipes);
        }
        
        // List receptov ktore zodpovedaju vyplni 
        ArrayList<RecipeResource> filledRecipes = typeRecipes.get(cellFill);
        
        if (filledRecipes == null) {
            filledRecipes = new ArrayList<>();
            typeRecipes.put(cellFill, filledRecipes);
        }
        
        filledRecipes.add(this);
        
    }
    
    /**
     * Metoda ktora zvaliduje entitu podla danych konceptov.
     */
    private void validate() {
        if (type == null) {
            type = Type.SHAPELESS;
        }
    }
    
    /**
     * Metoda ktora skopiruje jeden RecipeResource zadany v parametri <b>res</b>
     * do novo vytvoreneho resource.
     * @param res Resource z ktoreho kopirujeme do nasho resource udaje.
     * @throws Exception Vynimka pri nepodareni kopirovania.
     */
    @Override
    protected void copy(RecipeResource res) throws Exception {
        
    }
    
    // </editor-fold>
}
