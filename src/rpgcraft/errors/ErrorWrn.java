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

/**
 * Trieda definuje podklad pre vypis chyb vytvorenim noveho okna s popisom chyb.
 * @author Kirrie
 */
public class ErrorWrn{
    
    protected Logger logger = Logger.getLogger(getClass().getName());
    protected String msg;
    protected Color cl;
    protected Exception e;
    
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
        
        changeWindow(MainGameFrame.getFrame(), Render(),errorType);
        
    }
    
    protected JPanel Render() {
        JPanel errorPanel = new JPanel(new GridBagLayout());
        errorPanel.setBackground(cl);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(100, 100, 100, 100);
        c.fill = GridBagConstraints.BOTH;
        c.weightx=1;
        c.weighty=1;
                
        JTextArea eText = new JTextArea();     
        eText.setEditable(false);
        String fullErrorText = msg + ": \n" + stack2string(e);
        logger.log(Level.SEVERE, fullErrorText);
        eText.setText(fullErrorText);
        errorPanel.add(new JScrollPane(eText),c);
                
        return errorPanel;
    }
    
    /**
     * Prevadza danu vynimku typu Exception do Stringu. Pri neznamej vynimke vrati String s informaciou
     * o neznamej vynimke
     * @param e Vynimka na prevedenie
     * @return Vynimku prevedenu do Stringu [type:STRING]
     */
    
    private static String stack2string(Exception e) {
        try {
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            e.printStackTrace(pw);
            return "------\r\n" + sw.toString() + "------\r\n";
            } catch(Exception e2) {
                return "Unknown printStackTrace " + e2;
            }
    }
}
    
    
    
