/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.chunks;

import java.io.Externalizable;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectOutput;
import java.util.ArrayList;
import rpgcraft.entities.Entity;

/**
 *
 * @author doma
 */
public class Chunk implements Externalizable {
     private static final long serialVersionUID = 912804676578087866L;
     private static final int CHUNK_SIZE = 16;
     private static final int DEPTH = 128;
    
     private ChunkContent blocks;
     private int x;
     private int y;
     private boolean loaded = false;
        
     public Chunk() {
         this.blocks = new ChunkContent();
     }
        
     public Chunk(int x, int y) {
         this.blocks = new ChunkContent();
         this.x = x;
         this.y = y;
     }   
     
     public Chunk(ChunkContent blocks, int x, int y) {
         this.blocks = blocks;
         this.x = x;
         this.y = y;
     }
        
        public static int getSize() {
            return CHUNK_SIZE;
        }
        
        public static int getDepth() {
            return DEPTH;
        }
        
        public int getX() {
            return x;
        }
        
        public int getY() {
            return y;
        }
        
        public int[][] getLayer(int layer) {
            return blocks.getLayer(layer);
        }
        
        public int getTile(int layer, int x, int y) {
            return blocks.getIntOnPosition(layer, x & 15, y & 15);
        }
        
        public void setLoaded(Boolean loaded) {
            this.loaded = loaded;
        }
        
        public boolean getLoaded() {
            return loaded;
        }

    @Override
    public void writeExternal(ObjectOutput out) throws IOException {    
        out.writeInt(x);
        out.writeInt(y);
        for (int k = 0; k < DEPTH; k++) {
            for (int i = 0; i < CHUNK_SIZE; i++ ) {
                for (int j = 0; j< CHUNK_SIZE; j++) {                    
                    out.writeInt(blocks.getIntOnPosition(k, i, j));
                }
            }
        }
    }

    @Override
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {                
        this.x = in.readInt();       
        this.y = in.readInt();
        for (int k = 0; k < DEPTH; k++) {
            for (int i = 0; i < CHUNK_SIZE; i++ ) {
                for (int j = 0; j< CHUNK_SIZE; j++) {
                        blocks.setIntOnPosition(k, i, j, in.readInt());
                    
                }
            }
        }                
        
    }
    
        
}
