/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map;

import java.awt.Color;
import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import rpgcraft.entities.Entity;
import rpgcraft.entities.Player;
import rpgcraft.errors.MultiTypeWrn;
import rpgcraft.map.chunks.Chunk;

/**
 *
 * @author kirrie
 */
public class SaveChunk implements Externalizable {
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne + Pomocne triedy" >
    // Serializacne premenne, UID a verzia pre buducnost pri zmene archivov.
    private static final long serialVersionUID = 912804676578087866L;
    private static final int serializationVersion = 1;       
    
    // Entity ktore sa budu ukladat alebo nacitavat.
    private ArrayList<Entity> entities;
    // Chunk s mapou ktory sa bude ukladat alebo nacitavat.
    private Chunk chunk;
    
    /**
     * Staticka Trieda EOF sluzi na rozpoznanie pri externalizacii ci subor dosiahol koniec.
     * Prazdna trieda bez dalsieho ineho vyuzitia. V buducnosti mozne doplnit o nejake dodatocne
     * informacie + dodatocna kontrola archivu.
     */
    static class EOF implements Externalizable {
        // Konstruktor EOF priznaku
        public EOF() {
            
        }
        
        @Override
        public void writeExternal(ObjectOutput out) throws IOException {
            
        }

        @Override
        public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
            
        }
        
    }
    
    
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktory ">
    
    /**
     * Prazdny konstruktor dolezity pri nacitavani Chunkov z disku.
     * Externalizacia potrebuje pri vytvarani chunkov zavolat tento konstruktor
     * takze jeho pritomnost nevyhnutna.
     */
    public SaveChunk() {        
    }
    
    /**
     * Konstruktor s dvoma parametrami ktory inicializuje novy Chunk.
     * Kazdy Chunk si v sebe uchovava entity ktore sa v nom nachadzaju ako aj object
     * so samotnym Chunkom a jeho datami.
     * @param entities Entity nachadzajuce sa v Chunku
     * @param chunk Samotny chunk s datami.
     */
    public SaveChunk(ArrayList<Entity> entities, Chunk chunk) {
        this.entities = entities;
        this.chunk = chunk;
    } 
    
    // </editor-fold>
         
    // <editor-fold defaultstate="collapsed" desc=" Externalizacne metody ">
    @Override
    public void writeExternal(ObjectOutput out) throws IOException {
        out.writeInt(serializationVersion);        
        out.writeObject(chunk);
        for (Entity e : entities) {
            out.writeObject(e);
        }
        out.writeObject(new EOF());
        
    }

    @Override
    @SuppressWarnings("empty-statement")
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
        if (in.readInt() != serializationVersion) {
            throw new IOException("Neznama verzia archivu");
        }        
        this.chunk = (Chunk) in.readObject();
        
        do {        
        Object o = in.readObject();
        if (o instanceof EOF) {
                return;
        }
        
        if (entities==null) {
            entities = new ArrayList<>();
        }
        
        entities.add((Entity)o);
        
        } while (true);
        
        
    }
    
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    
    /**
     * Metoda ktora vrati vsetky entity v tomto chunku.
     * @return List s entitami.
     */
    public ArrayList<Entity> getEntities() {
        return entities;
    }
    
    /**
     * Metoda ktora vrati samotny chunk s datami a polom s vypisom dlazdic.
     * @return Chunk so samotnymi datami
     */
    public Chunk getChunk() {
        return chunk;
    }
    
    // </editor-fold>
}
