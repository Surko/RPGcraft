/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.sound;

import java.io.File;
import java.util.ArrayList;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import rpgcraft.manager.PathManager;
import rpgcraft.resource.SoundResource;
import rpgcraft.resource.StringResource;

/**
 * Trieda ktorej instancia stara o spustenie zvuku v hre. Zaatial nie je trieda 
 * spravne nastavene kedze nemam ziadne rychle kniznice na spustanie zvuku. Defaultne
 * je nastavene spustanie zvukov pomocou Clip a AudioStreamu.
 */
public class Sound {
    // Klip ktory prehravame
    private Clip clip;
    // Meno zvuku ktory chcem prehrat
    private ArrayList<String> soundNames;
    // Aktualne prehravany zvuk
    private String soundName;
    // Boolean ci sa hudba opakuje, nahodne vybera a ci je ukoncena
    private boolean repeat, shuffle, done;
    
    /**
     * Trieda SoundListener ktora reaguje na zmeny klipu ako je stop ci start.
     * Na spravne fungovanie treba implementovat od LineListener metodu update ktora
     * je volana pri spominanych zmenach.
     */
    class SoundListener implements LineListener {

        /**
         * Metoda ktora vykona update na klipe. V metode urcime zdrojovy klip z ktoreho bol listener
         * volany a podla toho ci skoncil zavolame metody flush a close s moznostou opakovania
         * celeho klipu zavolanim metody play.
         * @param event Udalost ktora vykonala metodu 
         */
        @Override
        public void update(LineEvent event) {                                    
            Clip srcClip = (Clip)event.getSource();
            if (srcClip.isOpen() && event.getFramePosition() >= srcClip.getFrameLength()) {  
                srcClip.close();               
                if (!done && repeat) {                    
                    play();
                }
            }            
        }        
    }
    
    /**
     * Konstruktor ktory vytvori novu instanciu Sound. Zvuk na prehratie ziskavame zo
     * SoundResource res.
     * @param res SoundResource z ktoreho ziskavame hudbu na prehratie
     */
    public Sound(SoundResource res) {
        if (res != null) {
            this.soundNames = res.getSoundNames();
            this.repeat = res.isRepeatable();
            this.shuffle = res.isShuffle();
            done = false;
        }
    }
    
    /**
     * Metoda ktora prehra hudbu zadanu v tomto objekte. Hudbu nacitavame zo suboru
     * danu menom soundName. Subor prevedieme na AudioStream, ktory otvorime a prehrame pomocou 
     * clip objektu.
     */
    public void play() {
        try {
            int iSound = 0;            
            if (shuffle) {
                iSound = new Random().nextInt(soundNames.size());
                soundName = soundNames.get(iSound);
            } else {
                iSound++;
                if (iSound >= soundNames.size()) {
                    iSound = 0;
                }
            }
            if (soundName != null) {
                new Thread() {
                    @Override
                    public void run() {
                        File soundFile = PathManager.getInstance().getSoundPath(soundName, false);                    
                        try {
                            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
                            clip = AudioSystem.getClip();
                            clip.open(audioIn);                        
                        } catch (Exception ex) {
                            Logger.getLogger(Sound.class.getName()).log(Level.WARNING, null, ex);
                        }
                        clip.addLineListener(new SoundListener());
                        clip.start();                            
                    }                
                }.start();                
            }            
        } catch (Exception e) {
            Logger.getLogger(getClass().getName()).log(Level.WARNING, StringResource.getResource("_esound"),soundName);
        }
    }
    
    /**
     * Metoda ktora zastavi prehravany zvuk.
     */
    public void close() {
        if (clip != null) {            
            clip.close();
            done = true;
        }
    }
    
}
