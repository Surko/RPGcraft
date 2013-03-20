/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.panels;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JPanel;
import rpgcraft.GamePane;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;
import rpgcraft.utils.ImageUtils;

/**
 *
 * @author Kirrie
 */
public class MainMenu extends AbstractMenu{
    
    public MainMenu(UiResource res) {  
        this.res = res;
    }

    @Override
    public void initialize(Container gameContainer, InputHandle input) {
        super.initialize(gameContainer, input);
        menuMap.put("mainMenu", this);
    }

    
    @Override
    public void update() {
       super.update();  
       
    }

    @Override
    public void inputHandling() {
        super.inputHandling();
    }         

    @Override
    public void setWidthHeight(int w, int h) {}

    
    
} 
