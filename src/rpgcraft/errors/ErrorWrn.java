/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.errors;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import rpgcraft.MainGameFrame;
import rpgcraft.utils.TextUtils;

/**
 * Trieda definue podklad pre vypis chyb vytvorenim noveho okna s popisom chyb. <br>
 * changeWindow nastavuje novy chybovy ram <br>
 * renderSpecific vymeni aktivny ram s nami vytvoreny <br>
 * renderPanel vrati panel s chybovymi hlaskami
 * @author Kirrie
 */
public class ErrorWrn{
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    /**
     * Logger pre Error okna
     */
    protected static final Logger LOG = Logger.getLogger(ErrorWrn.class.getName());
    /**
     * Dodatocny textna vypis
     */
    protected String msg;
    /**
     * Farba vynimky
     */
    protected Color cl;
    /**
     * Vynimka ktoru chceme vypisat
     */
    protected Exception e;
    /**
     * Chybove okno s hlaskami.
     */
    private static JPanel errorPanel;
    // </editor-fold>
    
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
    
    /**
     * Metoda ktora zmeni aktivny ram na novy s nazvom ktory je zadany v parametri <b>errorType</b>.
     * Metodu musime volat na zobrazenie chyboveho ramu/okna
     * @param errorType Meno ramu
     */
    public void renderSpecific(String errorType) {
        MainGameFrame.endGame();
        if (errorPanel == null) {
            changeWindow(MainGameFrame.getFrame(), renderPanel(),errorType);
        } else {
            renderPanel();
        }
        
        
    }
    
    /**
     * Metoda ktora vyrenderuje chybove okno vytvorenim noveho JPanelu + JScrollPane s JTextArea.
     * V textovej ploche vypiseme hlaskove chyby ktore mame zobrazit. Pri rendere dalsej chyby
     * doplname uz do existujuceho textoveho panelu text s novou chybou.
     * @return Panel s chybovymi hlaskami
     */
    protected JPanel renderPanel() {
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
    
    
    
