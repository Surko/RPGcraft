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
 *
 * @author kirrie
 */
public class RecipeResource extends AbstractResource<RecipeResource>{
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
    
    public static RecipeResource newBundledResource(Element elem) {
        return new RecipeResource(elem);                
    }
    
    public static RecipeResource getResource(String name) {
        return recipeResources.get(name);
    }    
    
    private RecipeResource(Element elem) {
        this.cellFill = 0;
        parse(elem);           
        validate();
        recipeResources.put(id, this);        
        putRecipe();
    }        
    
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
    
    private void validate() {
        if (type == null) {
            type = Type.SHAPELESS;
        }
    }
    
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
 
    public ArrayList<String> getCraftedsItems() {
        return resultItems;
    }
    
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
    
    @Override
    protected void parse(Element elem) {
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

    @Override
    protected void copy(RecipeResource res) throws Exception {
        
    }
    
}
