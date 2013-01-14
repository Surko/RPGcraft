/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import rpgcraft.GamePane;
import rpgcraft.MainGameFrame;
import rpgcraft.entities.Item;
import rpgcraft.entities.MovingEntity;
import rpgcraft.entities.Player;
import rpgcraft.graphics.inmenu.AbstractInMenu;
import rpgcraft.graphics.inmenu.InventoryMenu;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.map.Map;
import rpgcraft.panels.components.Container;
import rpgcraft.resource.EntityResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.Framer;

/**
 *
 * @author Surko
 */
public class GameMenu extends AbstractMenu {

    // Objekt pre hraca typu Player zdedeny po Entite    
    public Player player;
    private AbstractInMenu menu;
    private Map map;
    
    public GameMenu(UiResource res) {
        this.res = res;        
    }    

    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put("gamemenu",this);
    }
    
    
    
    @Override
    public void inputHandling() {
        map.inputHandling();
    }

    @Override
    protected void paintElement(Graphics g, UiResource resource, JPanel panel) {
        if (map != null) {
            map.paint(g);
        }
    }
    
    public Map getMap() {
        return map;
    }
    
    @Override
    public void update() {
        if (map.player != null) {
                    if (Framer.tick > 2500) {
                        if (map.gameTime==24) {
                            map.gameTime = 0;
                        }
                        map.gameTime++;
                        map.setLightState(true);
                        Framer.tick = 0;
                    }          
                    //input.keyUpdates();
                    map.update();
                }
    }
    
       
    public void newMapInstance() {
        gamePane.setSize(MainGameFrame.Fwidth, MainGameFrame.Fheight);
        
        this.map = new Map(gamePane, input);
        Logger.getLogger(getClass().getName()).log(Level.INFO, "Game map set");
        
        if (!gamePane.hasXmlInitialized()) {                         
            gamePane.initializeXmlFiles(PathManager.getInstance().getXmlPath().listFiles());                                  
        } else {
            Logger.getLogger(getClass().getName()).log(Level.INFO, "Xml files were already initialized ---> ABORT");
        }
        
        map.loadMapAround(0,0);   
        map.initializeTiles();
        
        // Testovacie riadky na vytvorenie dvoch entit.
        MovingEntity entity = new MovingEntity("Zombie", map, EntityResource.getResource("Zombie"));        
        entity.initialize();        
        entity.trySpawn();
        MovingEntity entity2 = new MovingEntity("Zombie", map, EntityResource.getResource("Zombie"));        
        entity2.initialize(); 
        entity2.trySpawn();
        //entity.setImpassableTile(1); 
        player = new Player("Surko", map, EntityResource.getResource("_player"));
        player.initialize();
        player.trySpawn();
        player.setHandling(input);
        player.setLightRadius(96);
        player.setSound(128);
        //player.setImpassableTile(1);
        
        Item item = new Item("Healing Potion",EntityResource.getResource("HealingPotion"));
        player.getInventory().add(item);
        
        map.addEntity(player);
        map.addEntity(entity); 
        map.addEntity(entity2);
        
        InventoryMenu inventory = new InventoryMenu(player, input, map);
        
    }

    @Override
    public void setWidthHeight(int w, int h) {
        map.setWidthHeight(w, h);
    }
    
}
