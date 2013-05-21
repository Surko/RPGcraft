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
        
        if ((e.getTargetEntity() != null)) {
            double randomValue = 0d;
            Entity target = e.getTargetEntity();
                int xP = target.getXPix() - e.getXPix();
                int yP = target.getYPix() - e.getYPix();
                int circleCoor = xP * xP + yP* yP;
                if (circleCoor < target.getSound() * target.getSound()) {                    
                    if (circleCoor < Math.pow(e.getActiveItem().getAttackRadius(), 2)) {
                        if (e.getStamina() > 0) { 
                            if (!e.hasAttacked()) {
                                randomValue = random.nextDouble();
                                e.setAttackStarted(true);                                                               
                            } else {
                                 e.incStamina(-1, 0);                             
                                 e.incAcumPower(1, 0);
                                 //poweringBar.setValue(activeItem.hpower);
                                 if (e.getAcumPower() >= randomValue * e.getMaxPower()) {
                                     e.attack(randomValue);
                                     e.incAcumPower(0, 0);
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
