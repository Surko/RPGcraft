/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.map.generators;

import rpgcraft.plugins.GeneratorPlugin;

/** 
 * Trieda DefaultGenerator implementujuca Plugin na vytvorenie map.
 * Generator vygeneruje zakladny teren v metode generate. Instancie tejto triedy je vytvorena
 * pri neexistencii generatov ktore by vytvorili mapu. Takisto sa moze vyskytovat v liste generatorov podla ktorych 
 * vytvara mapu. V tomto pripade je volana metoda generate tejto triedy ako prva => preinicializacia terenu.
 * Nasledne su volane dalsie generatory, ktore doplnaju teren mapy podla dalsich pravidiel.
 * @author kirrie
 */
public class DefaultGenerator implements GeneratorPlugin {
    
    private static final int SKY = 64;
    
    /**
     * <i>
     * {@inheritDoc }
     * </i>
     * <p>
     * V tomto pripade sa generuje zakladny teren, nic algoritmicke.
     * Metoda prechadza vsetkymi prvkami a vyuziva metody setTile na
     * vyplnenie pola/mapy dlazdicami (trava a kamen).
     * </p>
     * @param mapGenerator {@inheritDoc }
     * @throws Vynimka pri generovani terenu. Vacsinou priradovanie dlazdic ktore neexistuju. 
     */
    @Override
    public void generate(MapGenerator mapGenerator) throws Exception {                     
        for (int k=0;k<mapGenerator.getDepth();k++) {
            if (k < SKY) {
                generateGround(mapGenerator, k);
            }
        }            
        
    }
    
    /**
     * Metoda defaultne vygeneruje chunk vo vyske ktora je vacsia ako zadana premenna <b>SKY</b>.
     * @param mapGenerator Generator mapy
     * @param level Poschodie ktore generujeme
     * @throws Vynimka pri generovani terenu. Vacsinou priradovanie dlazdic ktore neexistuju. 
     */
    private void generateSky(MapGenerator mapGenerator, int level) throws Exception {
        for (int i=0;i<mapGenerator.getSize();i++) {
            for (int j=0;j<mapGenerator.getSize();j++) {                                        
                mapGenerator.setTile(level, i, j, 0);                
            }
        }
    }
        
    /**
     * Metoda defaultne vygeneruje chunk po vysku zadanu premennou <b>SKY</b>.
     * @param mapGenerator Generator mapy
     * @param level Poschodie ktore generujeme
     * @throws Vynimka pri generovani terenu. Vacsinou priradovanie dlazdic ktore neexistuju. 
     */
    private void generateGround(MapGenerator mapGenerator, int level) throws Exception{
        for (int i=0;i<mapGenerator.getSize();i++) {
            for (int j=0;j<mapGenerator.getSize();j++) {                                        
                if (j >8){
                    mapGenerator.setTile(level, i, j, 2);
                } else {
                    mapGenerator.setTile(level, i, j, 1);
                }
            }
        }
    }

    /**
     * <i>
     * {@inheritDoc }
     * </i>
     * <p>
     * V tomto pripade je metoda priamociara a zatial nic nevypisuje.
     * </p>
     */
    @Override
    public void run() {
        
    }
    
}
