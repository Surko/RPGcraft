/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities.ai;

import rpgcraft.plugins.Ai;
import rpgcraft.entities.Entity;
import rpgcraft.entities.MovingEntity;

/**
 * Zakladna inteligencia pre pohyblive entity pri aktualizacii entit. Trieda dedi od Ai => musi mat implementovane
 * abstraktne metody z triedy Ai. Navyse ma metodu getInstance ktora vrati instanciu tejto
 * inteligencie a kedze ma jedine privatny konstruktor tak sa z nej tymto padom stava singleton trieda. Zaklad inteligencie je 
 * v metode aiMove.
 * @author kirrie
 */
public class DefaultAi extends Ai{   
    
    // <editor-fold defaultstate="collapsed" desc=" Premenne ">
    // Meno pre Ai
    private static final String NAME = "default";
    // instancia default ai sluzi ako singleton
    private static DefaultAi instance;    
        
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Konstruktor ">
    /**
     * Privatny konstruktor ktory vytvori instanciu DefaultAi. Mozne ho volat
     * jedine z tejto triedy
     */
    private DefaultAi() {
        
    }
    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Gettery ">
    /**
     * Metoda ktora vrati instanciu zakladnej inteligencie (singleton vzor). Pri vytvoreni hu prida do listu
     * definovanom v triede Ai.
     * @return Instanciu DefaultAi
     */
    public static DefaultAi getInstance() {
        if (instance == null) {
            instance = new DefaultAi();
            aiList.put(NAME, instance);
        }
        return instance;
    }

    /**
     * Metoda ktora vrati meno tohoto Ai = premenna NAME
     * @return Meno tejto Ai
     */
    @Override
    public String getName() {
        return NAME;
    }

    // </editor-fold>
    
    // <editor-fold defaultstate="collapsed" desc=" Update ">
    /**
     * Zakladna metoda ktora vykonava inteligenciu pre entitu zadanu parametrom <b>e</b>.
     * Vsetky operacie s entitou sa daju uskutocnovat spristupnenim metod z tried Entity a MovingEntity.
     * <p>
     * V tomto pripade si vyberame target/cielovu entitu na ktoru utocime. Kontrolujeme ci 
     * su na rovnakom leveli a ci sa nachadzaju v okruhu utocenia. Ked to tak nie je
     * tak vynulujeme tuto cielovu entitu. Pri existencii cielovej entity v okruhu kontrolujeme ci ma dana
     * entita staminu vacsiu ako 0 a ci uz zacala utocit. Ked este entita nezautocila
     * tak zahajujeme utok nastavenim nahodnej hodnoty. Pri zahajeni utoku znizujeme staminu
     * a zvysujeme silu utoku. Pri dosiahnuti sily utoku viac nez je nami zadana nahodna hodnota krat
     * maximalna hodnota utocime na entitu a ukoncime utok. Pri nevykonani utoku nasilu
     * pohybujeme nasou entitou metodou knockMove (ked bola nahodou entita potlacena). <br>
     * Pri neexistencii utoku pohybujeme entitou do prislusnych stran jednoducho nastavenou inteligenciou
     * ze sa nasa entita pohybuje za cielovou kontrolovanim rozdielu x-ovych a y-ovych pozicii. <br>
     * Pri neexistencii cielovej entity nahodne nastavujeme hodnoty aby sa entita nahodne pohybovala po plani.
     * </p>
     * @param e {@inheritDoc }
     * @return True/false ci sa podarilo pohybovat s entitou
     */
    @Override
    public boolean aiMove(MovingEntity e) {        
        
        Entity target = e.getTargetEntity();            
        if (target != null && target.getLevel() == e.getLevel()) { 
            double value = e.getRandomValue();
            int xP = target.getXPix() - e.getXPix();
            int yP = target.getYPix() - e.getYPix();
            int circleCoor = xP * xP + yP* yP;
            if (circleCoor < target.getSound() * target.getSound()) {                    
                /* Ziskanie predmetu ktory dame do kontajneru entita.
                   Ked je to null tak vyberame vlastnosti zo sameho seba */
                Entity entItem = e.getActiveItem();                
                if (entItem == null) {
                    entItem = e;
                }
                //
                if (circleCoor < Math.pow(entItem.getAttackRadius(), 2)) {
                    if (e.getStamina() > 0) { 
                        if (!e.hasAttacked()) {
                            e.setRandomValue(random.nextDouble());
                            e.setAttackStarted(true);                                                               
                        } else {
                             e.incStamina(e.getAttackPower(), -1, 0);                             
                             e.incAcumPower(e.getAttackPower(), 1, 0);
                             //System.out.println(e.getAcumPower() + " " + e.getMaxPower());
                             //poweringBar.setValue(activeItem.hpower);
                             if (e.getAcumPower() >= value * e.getMaxPower()) {
                                 e.attack(value);
                                 e.clearPowers();
                                 e.setAttackStarted(false);    
                             }
                        }
                    }
                    e.knockMove();
                    return true;
                } else {
                   e.setAttackStarted(false);  
                }

                if (xP > 0) {
                    e.incxGo(1, 0);                        
                }
                if (xP < 0) {
                    e.incxGo(-1, 0);
                }
                if (yP < 0) {
                    e.incyGo(-1, 0);
                }
                if (yP > 0) {
                    e.incyGo(1, 0);
                }                    
            } else {
                e.setXGo(0d);
                e.setYGo(0d);
                e.setTarget(null);
            }
        } else {                
            if (random.nextInt(50) == 0) {
                e.setXDelay(random.nextInt(3) -1); 
                e.setYDelay(random.nextInt(3) -1);
            } 
            e.incDoublexGo(1, 0);
            e.incDoubleyGo(1, 0);
        }

        if ((!e.updateCoordinates())) {

                 /* Aby boli zahrnute vsetky moznosti pri pohybe
                 * tak vygenerujeme nahodne cislo 0-2. Pricitame -1 a 
                 * potom vynasobime pre moment nahody kedy sa entita nepohne.
                 */
           if (random.nextInt(100) == 0) {
                e.setXDelay(random.nextInt(3) -1); 
                e.setYDelay(random.nextInt(3) -1);
           } 
           e.incDoublexGo(1, 0);
           e.incDoubleyGo(1, 0);
        }

        return true;
    }
    
    // </editor-fold>       
}
