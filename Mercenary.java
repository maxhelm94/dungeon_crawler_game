package dungeonmania.Entities.MovingEntities;

import dungeonmania.GameMap.GameMap;
import dungeonmania.Entities.Inventory;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;
import dungeonmania.Helper;

public class Mercenary extends MovingEntity {

    private Integer bribeAmount;
    private Boolean friendly;
    private static Integer bribeRadius;
    private Boolean mindControlled; 

    public Mercenary(String id, String type, Position position, double newAttack, double newHealth, Integer newBribeAmount, Integer newBribeRadius) {
        super(id, type, position, newAttack, newHealth);
        friendly = false;
        bribeAmount = newBribeAmount;
        bribeRadius = newBribeRadius;
        setIsInteractable(true);
    }

    public Mercenary(String id, String type, Position position, double newAttack, double newHealth) {
        super(id, type, position, newAttack, newHealth);
        friendly = false;
    }

    @Override
    public void nextStep(GameMap gameMap) {
        if (gameMap.getPlayer().getActivePotion().equals("invisible")) moveRandom(gameMap);
        else stepping(gameMap);
    }


    public void stepping(GameMap gameMap) {
        if (friendly && isOneStepFromPlayer(gameMap)) {
            if (this.getPosition().getX() < gameMap.getPlayer().getPreviousPosition().getX()) moveInDirection(gameMap, Direction.RIGHT);
            else if (this.getPosition().getX() > gameMap.getPlayer().getPreviousPosition().getX()) moveInDirection(gameMap, Direction.LEFT);
            else if (this.getPosition().getY() < gameMap.getPlayer().getPreviousPosition().getY()) moveInDirection(gameMap, Direction.DOWN);
            else if (this.getPosition().getY() > gameMap.getPlayer().getPreviousPosition().getY()) moveInDirection(gameMap, Direction.UP);
        } else if (friendly && samePosition(gameMap)) return; 
        else if (!friendly && gameMap.getPlayer().getActivePotion().equals("invincible")) {
            if (runAway(gameMap) == null) return;
            else moveInDirection(gameMap, runAway(gameMap));
        }
        else {
            if (advancedDirectionToPlayer(gameMap) != null) moveInDirection(gameMap, advancedDirectionToPlayer(gameMap));
        }
    }

    public boolean isOneStepFromPlayer(GameMap gameMap) {
        int x = this.getPosition().getX();
        int y = this.getPosition().getY();

        int i = gameMap.getPlayer().getPreviousPosition().getX();
        int j = gameMap.getPlayer().getPreviousPosition().getY();

        if ((Helper.absolute(x - i) == 1 && Helper.absolute(y - j) == 0) ||
            (Helper.absolute(x - i) == 0 && Helper.absolute(y - j) == 1)) return true;

        return false;

    }

    public Boolean samePosition(GameMap gameMap) {
        int x = this.getPosition().getX();
        int y = this.getPosition().getY();

        int i = gameMap.getPlayer().getPreviousPosition().getX();
        int j = gameMap.getPlayer().getPreviousPosition().getY();

        if ((x - i) == 0 && (y - j) == 0) return true;

        return false;
    }

    public boolean getFriendly() {
        return friendly;
    }

    public void setFriendly(Boolean friendly) {
        this.friendly = friendly;
    }

    public Integer getBribeAmount() {
        return bribeAmount;
    }

    public Boolean getMindControlled() {
        return mindControlled;
    }

    public void setMindControlled(Boolean mindControlled) {
        this.mindControlled = mindControlled;
    }

    public Boolean bribe(Player player) {
        // we dont bribe ally
        if (this.friendly == true) {
            return true;
        } 

        // check the player in within specified bribing radius
        if (withinBribeRadius(player.getPosition(), player.getInventory())) {
            // check the player has enough gold/ sceptre
            if (hasEnoughItemsToBribe(player.getInventory())) {
                if (player.getInventory().getItemCountByType("sceptre") >= 1) {
                    this.mindControlled = true;
                } else {
                    player.getInventory().deleteItemByTypeAndQuantity("treasure", this.bribeAmount);
                }
                setIsInteractable(false);
                this.friendly = true;
                return true;
            } 
        }

        this.friendly = false;
        return false;
    }

    public Boolean withinBribeRadius(Position playerPosition, Inventory inventory) {
        if (inventory.getItemCountByType("sceptre") >= 1) return true;
        
        Integer mercenaryX = this.getPosition().getX();
        Integer mercenaryY = this.getPosition().getY();
        Integer playerX = playerPosition.getX();
        Integer playerY = playerPosition.getY();

        if ((mercenaryX - Mercenary.bribeRadius) <= playerX && playerX <= (mercenaryX + Mercenary.bribeRadius)) {
            if ((mercenaryY - Mercenary.bribeRadius) <= playerY && playerY <= (mercenaryY + Mercenary.bribeRadius)) {
                return true;
            }
        }

        return false;
    }

    public Boolean hasEnoughItemsToBribe(Inventory inventory) {
        if (inventory.getItemCountByType("sceptre") >= 1) {
            return true;
        }
        
        if (inventory.getItemCountByType("treasure") >= this.bribeAmount) {
            return true;
        } 

        return false;

    }
}
