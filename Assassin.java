package dungeonmania.Entities.MovingEntities;

import java.util.List;
import java.util.Random;

import dungeonmania.GameMap.GameMap;
import dungeonmania.Entities.Entity;
import dungeonmania.util.Position;
import dungeonmania.Helper;

public class Assassin extends Mercenary {

    private static int radius;
    private static double bribeFailRate;

    public Assassin(String id, 
                    String type, 
                    Position position, 
                    double newAttack, 
                    double newHealth, 
                    Integer newAssassinBribeAmount, 
                    Integer newAssassinBribeRadius,
                    double newAssassinBribeFailRate, 
                    Integer newRadius) {
        super(id, "assassin", position, newAttack, newHealth, newAssassinBribeAmount, newAssassinBribeRadius);
        
        radius = newRadius;
        bribeFailRate = newAssassinBribeFailRate;
        setIsInteractable(true);
    }

    @Override
    public void nextStep(GameMap gameMap) {
    
        if (gameMap.getPlayer().getActivePotion().equals("invisible") && inRadius(gameMap))
            moveInDirection(gameMap, advancedDirectionToPlayer(gameMap));
        else if (gameMap.getPlayer().getActivePotion().equals("invisible")) moveRandom(gameMap);
        else stepping(gameMap);
    }

    public void setRadius(int newRadius) {
        radius = newRadius;
    }

    public int getRadius() {
        return radius;
    }

    public void setBribeFailRate(double newBribeFailRate) {
        bribeFailRate = newBribeFailRate;
    }

    public boolean inRadius(GameMap gameMap) {
        Position here = this.getPosition();
        int up = recursiveRadius(gameMap, here.getX(), here.getY() - 1, radius);
        int right = recursiveRadius(gameMap, here.getX() + 1, here.getY(), radius);
        int down = recursiveRadius(gameMap, here.getX(), here.getY() + 1, radius);
        int left = recursiveRadius(gameMap, here.getX() - 1, here.getY(), radius);

        if (Helper.maximum(Helper.maximum(up, down), Helper.maximum(right, left)) > 0) return true;
        return false;
    }

    private int recursiveRadius(GameMap gameMap, int x, int y, int z) {
        List<Entity> entityList = gameMap.getEntityListByCoordinate(x, y);
        for (Entity entity : entityList) {
            if (entity.getType() == "player") return z;
        }

        if (z == 0) return 0;
        int up = recursiveRadius(gameMap, x, y - 1, z - 1);
        int right = recursiveRadius(gameMap, x + 1, y, z - 1);
        int down = recursiveRadius(gameMap, x, y + 1, z - 1);
        int left = recursiveRadius(gameMap, x - 1, y, z - 1);

        return Helper.maximum(Helper.maximum(up, down), Helper.maximum(right, left));
    }

    @Override
    public Boolean bribe(Player player) {
        // we dont bribe ally
        if (getFriendly() == true) {
            return true;
        } 

        if (super.withinBribeRadius(player.getPosition(), player.getInventory())) {
            
            if (super.hasEnoughItemsToBribe(player.getInventory())) {
                player.getInventory().deleteItemByTypeAndQuantity("treasure", this.getBribeAmount());
                
                // if the player has sceptre, ignore fail rate and mind controll it
                if (player.getInventory().getItemCountByType("sceptre") >= 1) {
                    setIsInteractable(false);
                    setMindControlled(true);
                    setFriendly(true);
                    return true;
                // if the player doesnt have sceptre, try to bribe it
                } else {
                    Random rand = new Random();
                    int randomNumber = rand.nextInt(100);
                    
                    if (randomNumber > (Assassin.bribeFailRate * 100)) {
                        
                        setIsInteractable(false);
                        setFriendly(true);
                        return true;
                    }
                }           
            }
        }

        setFriendly(false);
        return false;
    }
}
