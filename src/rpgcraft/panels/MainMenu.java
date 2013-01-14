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
import javax.swing.JPanel;
import rpgcraft.graphics.Colors;
import rpgcraft.handlers.InputHandle;
import rpgcraft.panels.components.Container;
import rpgcraft.panels.components.swing.SwingImageButton;
import rpgcraft.resource.StringResource;
import rpgcraft.resource.ImageResource;
import rpgcraft.resource.UiResource;

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
        menuMap.put("mainmenu", this);
    }

    
    
    @Override
    public void update() {
       super.update();  
       
    }

    @Override
    public void inputHandling() {
    }         

    @Override
    public void setWidthHeight(int w, int h) {}

    
    
} 
