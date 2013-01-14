/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.sound;

import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.Clip;
import rpgcraft.resource.SoundResource;
import rpgcraft.resource.StringResource;

/**
 *
 * @author Surko
 */
public class Sound {
    
    private Clip clip;
    private String soundName;
    
    public Sound(SoundResource res) {
        this.soundName = res.getSoundName();
    }
    
    public void play() {
        try {
            new Thread() {
                @Override
                public void run() {
                    clip.start();
                }                
            }.start();
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, StringResource.getResource("_esound"),soundName);
        }
    }
    
}
