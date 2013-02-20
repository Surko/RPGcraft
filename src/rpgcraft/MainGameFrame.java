/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft;

import java.awt.GridBagLayout;
import java.util.logging.Logger;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.logging.Level;
import javax.swing.JFrame;
import rpgcraft.handlers.InputHandle;
import rpgcraft.manager.PathManager;
import rpgcraft.resource.StringResource;
import rpgcraft.utils.Framer;
/**
 * 20.7 1st revision
 * 1.8 2nd revision
 * 20.8 3rd revision
 * 31.8 4rd revision
 * @author Kirrie
 */

public class MainGameFrame {
    // Definovane velkosti okna
    public static final int Fwidth = 800;
    public static final int Fheight = 600;
    
    private final static Logger logger = Logger.getLogger("MainGameFrame");
    
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
        mFrame.addComponentListener(new UpdateGameWidthandHeight());
                
        game = new GamePane();  
        game.setLayout(null);
        game.add(Framer.frameLabel);

        mFrame.add(game);
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
        
    
    // Triedy
    
    
    /**
     * Staticka trieda implementujuca ComponentListener, 
     * ktora ma za ulohu menit velkost obrazovky pri kazdom
     * zvacsovani ci zmensovani obrazovky (frame) mysou v hre.
     * @see ComponentListener
     * 
     */
    static class UpdateGameWidthandHeight implements ComponentListener {

        /**
         * Pri Zmene okna zmeni velkost frame.
         * @param e 
         */
        @Override
        public void componentResized(ComponentEvent e) {
            game.setWidthHeight(mFrame.getWidth(), mFrame.getHeight());            
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
