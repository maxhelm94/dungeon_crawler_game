package dungeonmania.Entities.MovingEntities;

import dungeonmania.Entities.Entity;
import dungeonmania.util.Position;
import dungeonmania.util.Direction;
import dungeonmania.GameMap.GameMap;

public class Spider extends MovingEntity {

    private int positionMode;
    private static double attack;
    private static double health;

    public Spider(String id, Position position, double newAttack, double newHealth) {
        super(id, "spider", position, newAttack, newHealth);
        // 0 as spider starts at position 0
        //      8  1  2
        //      7  0  3
        //      6  5  4
        this.positionMode = 0;
    }

    public Spider(String id, Position position) {

        super(id, "spider", position, attack, health);
        this.positionMode = 0;
    }

    @Override
    // needs to be activated if the hero moves
    public void nextStep(GameMap gameMap) {
        
        boolean turn = false;
        
        
        // move up from these positions
        if (positionMode == 6 || positionMode == 7 || positionMode == 0 || positionMode == -4 || positionMode == -3) {
            // check if on next tile is a boulder
            for (Entity entity : gameMap.getEntityListByCoordinate(this.getPosition().getX(), this.getPosition().getY() - 1)) {
                if (entity.getType().equals("boulder")) {
                    turn = true;
                }
            }
            
            if (turn) {
                if (positionMode == 7 || positionMode == -3 || positionMode == 0) {
                    if (getSurroundingBoulder(Direction.DOWN, gameMap)) return;
                    else moveInDirection(gameMap, Direction.DOWN);
                }
                if (positionMode == 6) {
                    if (getSurroundingBoulder(Direction.RIGHT, gameMap)) return;
                    else moveInDirection(gameMap, Direction.RIGHT);
                }
                if (positionMode == -4) {
                    if (getSurroundingBoulder(Direction.LEFT, gameMap)) return;
                    else moveInDirection(gameMap, Direction.LEFT);
                }
            } else {
                moveInDirection(gameMap, Direction.UP);
            }
        }

        // move down from these positions
        if (positionMode == -8 || positionMode == -7 || positionMode == 2 || positionMode == 3) {

            for (Entity entity : gameMap.getEntityListByCoordinate(this.getPosition().getX(), this.getPosition().getY() + 1)) {
                if (entity.getType().equals("boulder")) turn = true;
            }

            
            
            if (turn) {
                if (positionMode == -7 || positionMode == 3) {
                    if (getSurroundingBoulder(Direction.UP, gameMap)) return;
                    else moveInDirection(gameMap, Direction.UP);
                }
                if (positionMode == -8) {
                    if (getSurroundingBoulder(Direction.RIGHT, gameMap)) return;
                    else moveInDirection(gameMap, Direction.RIGHT);
                }
                if (positionMode == 2) {
                    if (getSurroundingBoulder(Direction.LEFT, gameMap)) return;
                    else moveInDirection(gameMap, Direction.LEFT);
                }
            } else {
                moveInDirection(gameMap, Direction.DOWN);
            }
        }

        // move left from these positions
        if (positionMode == -1 || positionMode == -2 || positionMode == 4 || positionMode == 5) {

            for (Entity entity : gameMap.getEntityListByCoordinate(this.getPosition().getX() - 1, this.getPosition().getY())) {
                if (entity.getType().equals("boulder")) turn = true;
            }
            
            if (turn) {
                if (positionMode == -1 || positionMode == 5) {
                    if (getSurroundingBoulder(Direction.RIGHT, gameMap)) return;
                    else moveInDirection(gameMap, Direction.RIGHT);
                }
                if (positionMode == -2) {
                    if (getSurroundingBoulder(Direction.DOWN, gameMap)) return;
                    else moveInDirection(gameMap, Direction.DOWN);
                }
                if (positionMode == 4) {
                    if (getSurroundingBoulder(Direction.UP, gameMap)) return;
                    else moveInDirection(gameMap, Direction.UP);
                }
            } else {
                moveInDirection(gameMap, Direction.LEFT);
            }
        }

        // move right from these positions
        if (positionMode == 8 || positionMode == 1 || positionMode == -5 || positionMode == -6) {

            for (Entity entity : gameMap.getEntityListByCoordinate(this.getPosition().getX() + 1, this.getPosition().getY())) {
                if (entity.getType().equals("boulder")) turn = true;
            }
            
            if (turn) {
                if (positionMode == 1 || positionMode == -5) {
                    if (getSurroundingBoulder(Direction.LEFT, gameMap)) return;
                    else moveInDirection(gameMap, Direction.LEFT);
                }
                if (positionMode == 8) {
                    if (getSurroundingBoulder(Direction.DOWN, gameMap)) return;
                    else moveInDirection(gameMap, Direction.DOWN);
                }
                if (positionMode == -6) {
                    if (getSurroundingBoulder(Direction.UP, gameMap)) return;
                    else moveInDirection(gameMap, Direction.UP);
                }
            } else {
                moveInDirection(gameMap, Direction.RIGHT);
            }
        }

        // change rotation if we turn, so multiply the positionMode by -1 before incrementing
        if (!turn) {
            positionMode += 1;
        } else if (turn && positionMode == 0) {
            positionMode = 5;
        } else if (turn) {
            positionMode *= -1;
            positionMode += 1; 
        } 

        // if positionMode is out of range, change it
        if (positionMode == 0 || positionMode == -8) {
            positionMode = -8;
        } else if (positionMode == 9) {
            positionMode = 1;
        }
    }

    public static void setSpiderHealth(double newHealth) {
        health = newHealth;
    }

    public static void setSpiderAttack(double newAttack) {
        attack = newAttack;
    }

    // enclosed in boulders
    public boolean getSurroundingBoulder(Direction there, GameMap gameMap) {
        if (there == Direction.UP) {
            for (Entity entity : gameMap.getEntityListByCoordinate(this.getPosition().getX(), this.getPosition().getY() - 1)) {
                if (entity.getType().equals("boulder")) return true;
            }
        }

        if (there == Direction.DOWN) {
            for (Entity entity : gameMap.getEntityListByCoordinate(this.getPosition().getX(), this.getPosition().getY() + 1)) {
                if (entity.getType().equals("boulder")) return true;
            }
        }

        if (there == Direction.LEFT) {
            for (Entity entity : gameMap.getEntityListByCoordinate(this.getPosition().getX() - 1, this.getPosition().getY())) {
                if (entity.getType().equals("boulder")) return true;
            }
        }

        if (there == Direction.RIGHT) {
            for (Entity entity : gameMap.getEntityListByCoordinate(this.getPosition().getX() + 1, this.getPosition().getY())) {
                if (entity.getType().equals("boulder")) return true;
            }
        }

        return false;
    }
}
