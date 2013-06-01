/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rpgcraft.entities.ai;

import rpgcraft.entities.Entity;
import rpgcraft.entities.MovingEntity;
import rpgcraft.resource.StatResource;

/**
 *
 * @author kirrie
 */
public class DefaultAi extends Ai{   
    
    private static final String NAME = "default";
    
    private static DefaultAi instance;    
        
    public static DefaultAi getInstance() {
        if (instance == null) {
            instance = new DefaultAi();
            aiList.put(NAME, instance);
        }
        return instance;
    }

    @Override
    public String getName() {
        return NAME;
    }

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
                             System.out.println(e.getAcumPower() + " " + e.getMaxPower());
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
                e.setxGo(0d);
                e.setyGo(0d);
                e.setTarget(null);
            }
        } else {                
            if (random.nextInt(50) == 0) {
                e.setxDelay(random.nextInt(3) -1); 
                e.setyDelay(random.nextInt(3) -1);
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
                e.setxDelay(random.nextInt(3) -1); 
                e.setyDelay(random.nextInt(3) -1);
           } 
           e.incDoublexGo(1, 0);
           e.incDoubleyGo(1, 0);
        }

        return true;
    }
   
    
}
