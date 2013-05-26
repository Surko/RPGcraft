/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft;

import java.awt.Dimension;
import java.util.logging.Logger;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.Locale;
import java.util.logging.Level;
import javax.swing.JFrame;
import rpgcraft.graphics.ui.components.MsgDialog;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.MainUtils;

/**
 * 20.7 1st revision
 * 1.8 2nd revision
 * 20.8 3rd revision
 * 31.8 4rd revision
 * xx.1 - 18.3 5rd revision
 * @author Kirrie
 */

public class MainGameFrame {
    
    private final static Logger logger = Logger.getLogger("MainGameFrame");
        
    // Definovane velkosti okna
    public static final int Fwidth = 800;
    public static final int Fheight = 600;        
    // okno v ktorom bude panel s hrou
    private static JFrame mFrame;
    // Panel s hrou
    public static GamePane game;        
    
    /**
     * @param args the command line arguments
     */
    
    public static void main(String[] args) {
        
        try {
        mFrame = new JFrame("RPGcraft!");
        PathManager.getInstance(args);
        loadStringResource();
        
        mFrame.setBounds(0, 0, Fwidth, Fheight);
        mFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mFrame.addComponentListener(new UpdateLengths());
                
        game = new GamePane();  
        game.setLayout(null);
        game.add(MainUtils.FPSCOUNTER);                                       

        mFrame.getContentPane().add(game);
        mFrame.addKeyListener(InputHandle.getInstance());        
        mFrame.setVisible(true);
        
        game.startGame();
        } catch (Throwable t) {
            Logger.getLogger(MainGameFrame.class.getName()).log(Level.SEVERE, "Uncaught Exception", t);
        }
    }
    
    /**
     * Trieda getFrame ktora pri volani vrati frame v ktorom
     * prebieha hra
     * @return Frame s panelom s hrou.
     */
    public static JFrame getFrame() {
        return mFrame;
    }

    private static void loadStringResource() {  
        StringResource.initializeResources();
    }
    
    public static void endGame() {
        if (game != null) {
            game.endGame();
        }
    }
    
    public static int getContentWidth() {
        return mFrame.getContentPane().getWidth();
    }
    
    public static int getContentHeight() {
        return mFrame.getContentPane().getHeight();
    }
    
    // Triedy        
    /**
     * Staticka trieda implementujuca ComponentListener, 
     * ktora ma za ulohu menit velkost obrazovky pri kazdom
     * zvacsovani ci zmensovani obrazovky (frame) mysou v hre.
     * @see ComponentListener
     * 
     */
    static class UpdateLengths implements ComponentListener {

        /**
         * Pri Zmene okna zmeni velkost frame.
         * @param e 
         */
        @Override
        public void componentResized(ComponentEvent e) {
            game.setWidthHeight(mFrame.getContentPane().getWidth(), mFrame.getContentPane().getHeight());            
        }

        @Override
        public void componentMoved(ComponentEvent e) {            
        }

        @Override
        public void componentShown(ComponentEvent e) {
        }

        @Override
        public void componentHidden(ComponentEvent e) {
        }
        
    }
    
    
    
}
