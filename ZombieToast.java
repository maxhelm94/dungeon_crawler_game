package dungeonmania.Entities.MovingEntities;

import dungeonmania.GameMap.GameMap;
import dungeonmania.util.Position;

public class ZombieToast extends MovingEntity {
    
    // static variables that save the configurations of zombieToast when it spawns
    private static double templateAttack;
    private static double templateMaxHealth;

    public ZombieToast(String id, String type, Position position, double newAttack, double newHealth) {
        super(id, type, position, newAttack, newHealth);
    }

    public ZombieToast(String id, Position position) {
        super(id, "zombie_toast", position, templateAttack, templateMaxHealth);
    }

    /**
     * 
     * 
     */
    @Override
    public void nextStep(GameMap gameMap) {
        if (gameMap.getPlayer().getActivePotion().equals("invincable")) {
            if (runAway(gameMap) == null) return;
            else moveInDirection(gameMap, runAway(gameMap));
        } 
        else moveRandom(gameMap);
    }

    public static void setTemplateHealth(double newTemplateHealth) {
        templateMaxHealth = newTemplateHealth;
    }

    public static void setTemplateAttack(double newTemplateAttack) {
        templateAttack = newTemplateAttack;
    }
}