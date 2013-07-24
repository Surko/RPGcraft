/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.graphics.spriteoperation;

/**
 *
 * @author doma
 */
import java.awt.image.BufferedImage;
import java.util.HashMap;

/**
 * Trieda ktora uchovava SpriteSheet a jej zakladne udaje (Obrazok, sirku a vysku SpriteSheet)
 * spolu s metodami na vyber jednotlivych obrazkov zo sheetu.
 */
public class SpriteSheet {
        /**
         * Sirka a vyska SpriteSheet
         */
	private int width, height;
        /**
         * Obrazok SpriteSheet
         */
        private BufferedImage image;
        
        /**
         * vypis vsetkych SpriteSheet v Hashmap s klucom nazov suboru
         */
        private static HashMap<String,SpriteSheet> sheets = new HashMap<>(); 

        /**
         * Konstruktor ktory vytvori novy SpriteSheet s obrazkom predanym ako paramater
         * image. Vysku a sirku si urci z tohoto obrazku metodami get###.
         * @param image 
         */
	public SpriteSheet(BufferedImage image) {
                this.image = image;
		this.width = image.getWidth();
		this.height = image.getHeight();		
                
	}
        
        /**
         * Metoda ktora vrati SpriteSheet ako obrazok.
         * @return SpriteSheet
         */
        public BufferedImage getImage() {
            return image;
        }
        
        /**
         * Metoda ktora vrati cely objekt SpriteSheet, ktore su ulozene v premennej <b>sheets</b> so vsetkymi 
         * SpriteSheet. Rozhoduje sa pomocou parametru name (meno suboru v adresari sheets) ktory pouzijeme ako kluc k Hashmape.
         * 
         * @param name Kluc k SpriteSheet
         * @return SpriteSheet pre subor s menom name
         */
        public static SpriteSheet getSheet(String name) {
            return sheets.get(name);
        }
        
        /**
         * Metoda ktora do Hashmapy so vsetkymi SpriteSheet vsunie novu danu parametrom sheet a s klucom name,
         * ktory predstavuje meno suboru v adresari sheets.
         * @param name Kluc k SpriteSheet / meno suboru v adresari sheets.
         * @param sheet SpriteSheet v Hashmape namapovany na kluc
         */
        public static void putSheet(String name, SpriteSheet sheet) {
            sheets.put(name, sheet);
        }
}
