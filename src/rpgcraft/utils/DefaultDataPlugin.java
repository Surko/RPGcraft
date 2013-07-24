/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.utils;

import java.awt.Color;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Item;
import rpgcraft.entities.Player;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.map.Save;
import rpgcraft.panels.GameMenu;
import rpgcraft.plugins.DataPlugin;
import rpgcraft.plugins.RenderPlugin;
import rpgcraft.resource.StringResource;

/**
 * Zakladny plugin pre ziskanie dat do listov a pre dalsie pouzitie v listeneroch ci lua-skriptoch,
 * dediaci od DataPlugin. V enume Data zdruzujeme ake data dokazeme vratit (iba 4).
 * Na pouzitie treba zavolat getInstance pre ziskanie instancie a potom pouzity getData
 */
public class DefaultDataPlugin extends DataPlugin {
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre DefaultDataPluggin
     */
    private static final Logger LOG = Logger.getLogger(DefaultDataPlugin.class.getName());
    /**
     * Instancia pluginu
     */
    private static DefaultDataPlugin instance;
    /**
     * Mozne data ktore mozme ziskat do listu listov
     */
    public enum Data {
        SAVE,
        PLAYER_ITEMS,
        RENDER,
        ITEM
    }
    // </editor-fold>
    
    /**
     * Privatny konstruktor. Nemozne vytvori instanciu z inej triedy ako z tejto.
     */
    private DefaultDataPlugin() {
        
    }
    
    /**
     * Metoda ktora vrati instanciu DefaultDataPlugin (singleton vzor).
     * Jediny sposob ako vytvori instanciu tohoto pluginu.
     */
    public static DefaultDataPlugin getInstance() {
        if (instance == null) {
            instance = new DefaultDataPlugin();
        }
        return instance;
    }
    
    /**
     * Metoda ma za ulohu poskladat do jedneho ArrayListu result informacie podla daneho tagu
     * SAVE, PLAYER_ITEMS, etc... . Moznost pridat dalsie tagy je len na programatorovi.
     * Informacie mozme dostat dvoma sposobmi : <br>
     * - v liste <b>result1</b> je list listov. Tato moznost pouzivana ked volame tuto metodu
     * z metody getDataFromString. <br>
     * - v liste <b>result2</b> je list s datami. Tato moznost je pouzivane ked volame tuto metodu
     * z metody getDataArray. <br>
     * Dolezite je vediet ze cele data su vzdy predavane ako 2-dimenzionalne pole (rows, cols) a pri metode getDataFromString
     * mozme pouzivat priamo ziskavanie informacii z tagov. Kedze ale informacie z tagov su tiez uz
     * v 2-dimenzionalnom poli tak musime pouzivat referenciu na list listov aby sme to vedeli rozpoznat.
     * V druhom pripade pri volani z metody getDaraArray uz mame vytvorene pole poli a pracujeme
     * len s jednym riadkom a ked sa tam nachadza nejaky tag tak nevytvarame dalsie pole
     * ale iba naskladame vsetky informacie ako keby do jedneho riadku.
     * List s tymto musi ale vediet pracovat, preto je dolezite aby sa nezabudlo
     * ze keby nahodou list ma podelement dalsi list tak Cursor v liste musi pracovat sekvencne 
     * aby vyplnil ten podelement.    
     * Pocitame s tym ze jeden z listov predanych ako parameter je nenulovy.
     * @param inf Tag podla ktoreho vyberie informacie do listu
     * @param param Parametre podla ktorych vyplni vysledny list.
     * @param srcObject Zdrojovy objekt z ktoreho ziskavame data
     * @param result1 List listov v ktorom su ulozene informacie po riadkoch.
     * @param result2 List s informaciami/datami v ktorom su ulozene informacie sekvencne.List sluzi ako jeden riadok
     * @return True/false ci sa podarilo ziskat data
     */
    @Override
    public boolean getData(String inf, ArrayList<String> param, Object srcObject,  
            ArrayList<ArrayList<Object>> result1, ArrayList<Object> result2) {
        ArrayList<ArrayList<Object>> infList = null;
        try {
            switch (Data.valueOf(inf)) {
                case SAVE : {
                    infList = Save.getGameSavesParam(param);                    
                } break;                
                case PLAYER_ITEMS : {
                    // Tolerovany srcObject - Entity alebo Gamemenu z ktoreho si vytiahneme Playera.
                    if (srcObject instanceof Entity) {                                                
                        infList = Player.getInventoryItemsParam((Entity)srcObject, param);
                        break;
                    }
                    if (srcObject instanceof GameMenu) {
                        infList = Player.getInventoryItemsParam(((GameMenu)srcObject).player, param);
                        break;
                    }
                    LOG.log(Level.SEVERE, StringResource.getResource(null));
                    new MultiTypeWrn(null, Color.red, StringResource.getResource(null),
                            null).renderSpecific(StringResource.getResource(null));                    
                } break;
                case RENDER : {
                    if (srcObject instanceof GameMenu) {                        
                        infList = RenderPlugin.getRenderParam(param);
                    }
                } break;
                case ITEM : {
                    if (srcObject instanceof Item) {
                        infList = Item.getItemInfo((Item)srcObject);
                    }
                } break;
            }
        } catch (Exception e) {
            LOG.log(Level.WARNING, StringResource.getResource("_ndinfo"));
            new MultiTypeWrn(e, Color.red, StringResource.getResource("_ndinfo"),
                null).renderSpecific(StringResource.getResource("_label_parsingerror")); 
            return false;                
        }
        
        if (result1 != null) {
            for (ArrayList<Object> list : infList) {
                result1.add(list);
            }
        } else {
            for (ArrayList<Object> list : infList) {
                result2.addAll(list);
            }
        }
        
        return true;
    }                
}
