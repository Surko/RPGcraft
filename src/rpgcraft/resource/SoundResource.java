/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.resource;

import java.util.HashMap;
import javax.sound.sampled.*;
import org.w3c.dom.Element;

/**
 * -- NOT DONE --
 * Trieda ktora dedi po abstraktnej triede AbstractResource. Sluzi pre uchovanie
 * udajov o vsetkych zvukoch ktore sa v hre mozu vyskytnut.
 * V tomto stadiu nebude implementovana, ale v buducnosti to bude mozne.
 * -- NOT DONE --
 * @author Surko
 */
public class SoundResource extends AbstractResource<SoundResource> {
    
    private static HashMap<String, SoundResource> soundResources = new HashMap<>();
    
    private String id;
    private String soundName;
    
    public static SoundResource getResource(String name) {
        return soundResources.get(name);
    }    
    
    private SoundResource(Element elem) {
        // parse(elem);
        
        soundResources.put(id, this);
    }
    
    public static SoundResource newBundledResource(Element elem) {
        return new SoundResource(elem);                
    }
    
    @Override
    protected void parse(Element elem) {
    }

    @Override
    protected void copy(SoundResource res) throws Exception {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
    public String getSoundName() {
        return soundName;
    }
    
}
