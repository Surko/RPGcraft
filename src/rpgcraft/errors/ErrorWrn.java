/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.errors;

import java.awt.*;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import rpgcraft.MainGameFrame;
import rpgcraft.utils.TextUtils;

/**
 * Trieda definuje podklad pre vypis chyb vytvorenim noveho okna s popisom chyb.
 * @author Kirrie
 */
public class ErrorWrn{
    
    protected static Logger LOG = Logger.getLogger(ErrorWrn.class.getName());
    protected String msg;
    protected Color cl;
    protected Exception e;
    private static JPanel errorPanel;
    
    /**
     * Protected metoda umozni generovanie lepsie vyzerajucich errorov pri triedach zdedenych od tejto.
     * @param frame Frame okno ktore sa ma nahradit
     * @param c Komponent ktory bude pridany do Frame.
     * @param nameofFrame Meno novo vytvoreneho chyboveho okna.
     */
    protected void changeWindow(JFrame frame, Component c,String nameofFrame) {
        frame.setVisible(false);      
        frame.dispose();
        
        frame = new JFrame(nameofFrame);      
        frame.add(c);
        frame.pack();
        frame.setVisible(true);       
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                
    }
    
    public void renderSpecific(String errorType) {
        MainGameFrame.endGame();
        if (errorPanel == null) {
            changeWindow(MainGameFrame.getFrame(), Render(),errorType);
        } else {
            Render();
        }
        
        
    }
    
    protected JPanel Render() {
        if (errorPanel == null) {
            errorPanel = new JPanel(new GridBagLayout());
            errorPanel.setBackground(cl);
            GridBagConstraints c = new GridBagConstraints();
            c.insets = new Insets(100, 100, 100, 100);
            c.fill = GridBagConstraints.BOTH;
            c.weightx=1;
            c.weighty=1;
            JTextArea eText = new JTextArea();     
            eText.setEditable(false);
            String fullErrorText = msg + ": \n" + TextUtils.stack2string(e);    
            LOG.log(Level.SEVERE, fullErrorText);
            eText.setText(fullErrorText);
            errorPanel.add(new JScrollPane(eText),c);            
        } else {
            try {
                
                JScrollPane sPane = (JScrollPane) errorPanel.getComponent(0);
                JViewport eView = (JViewport) sPane.getComponent(0);
                JTextArea eText = (JTextArea) eView.getComponent(0);
                String fullErrorText = msg + ": \n" + TextUtils.stack2string(e);
                LOG.log(Level.SEVERE, fullErrorText);
                eText.append(fullErrorText);
            } catch (Exception e) {
                LOG.log(Level.SEVERE, "Fatal error : different component structure", e);
                System.exit(0);
            }
            
        }                                       
                
        return errorPanel;
    }        
}
    
    
    
