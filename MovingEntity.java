package dungeonmania.Entities.MovingEntities;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import dungeonmania.GameMap.GameMap;
import dungeonmania.Helper;
import dungeonmania.Entities.Entity;
import dungeonmania.Entities.StaticEntities.SwampTile;
import dungeonmania.Entities.StaticEntities.ZombieToastSpawner;
import dungeonmania.util.Direction;
import dungeonmania.util.Position;

public abstract class MovingEntity extends Entity implements Movement {

    private double attack;
    private double currentHealth;
    private Integer swampTurn = -1;
    private Integer maxSwampTurn = -1;

    public MovingEntity(String id, String type, Position position, double newAttack, double newHealth) {
        super(id, type, position);
        this.attack = newAttack;
        this.currentHealth = newHealth;
    }

    // ---------------------------------------------------------------------- \\
    // Functions
    // ---------------------------------------------------------------------- \\

    /**
     * Moves the MovingEntity towards the input Direction.
     */
    public void moveInDirection(GameMap game, Direction direction) {
        
        if (direction == null) return;
        // Get existing entity at original position
        Position temp = this.getPosition();
        // Entity existingEntity = game.getDungeonMap().get(this.getPosition());
        // System.out.print(existingEntity.getPosition());
        // Get new position in that direction
        Position newPosition = this.getPosition().translateBy(direction);
        // Get new layer integer
        int layer = game.getEntityListByCoordinate(newPosition.getX(), newPosition.getY()).size();
        //
        newPosition = this.getPosition().translateBy(direction).asLayer(layer);
        // Set the Position variable INSIDE the Entity
        this.setPosition(newPosition);
        // Remove the old Position (key)
        game.getDungeonMap().remove(temp);
        // Add the new (key, value) pair back into dungeonMap
        game.getDungeonMap().put(newPosition, this);
    }

    // ---------------------------------------------------------------------- \\
    // Setters & Getters
    // ---------------------------------------------------------------------- \\
    
    public void setAttack(double input) {
        this.attack = input;
    }

    public void setCurrentHealth(double input) {
        this.currentHealth = input;
    }

    public double getAttack() {
        return attack;
    }

    public double getCurrentHealth() {
        return currentHealth;
    }

    public int getSwampTurn() {
        return swampTurn;
    }

    public int getMaxSwampTurn() {
        return maxSwampTurn;
    }

    public List<Integer> assessSurrounding(GameMap gameMap) {
        Position position = this.getPosition();
        List<Integer> x = new ArrayList<>();
        x.add(0); x.add(1); x.add(2); x.add(3);
        
        for (Entity entity : gameMap.getEntityListByCoordinate(position.getX(), position.getY() - 1)) {
            if (entity.getType().equals("boulder") || entity.getType().equals("wall") || entity.getType().equals("door") || entity instanceof ZombieToastSpawner) x.remove(Integer.valueOf(0));
        }

        for (Entity entity : gameMap.getEntityListByCoordinate(position.getX() + 1, position.getY())) {
            if (entity.getType().equals("boulder") || entity.getType().equals("wall") || entity.getType().equals("door") || entity instanceof ZombieToastSpawner) x.remove(Integer.valueOf(1));
        }

        for (Entity entity : gameMap.getEntityListByCoordinate(position.getX(), position.getY() + 1)) {
            if (entity.getType().equals("boulder") || entity.getType().equals("wall") || entity.getType().equals("door") || entity instanceof ZombieToastSpawner) x.remove(Integer.valueOf(2));
        }

        for (Entity entity : gameMap.getEntityListByCoordinate(position.getX() - 1, position.getY())) {
            if (entity.getType().equals("boulder") || entity.getType().equals("wall") || entity.getType().equals("door") || entity instanceof ZombieToastSpawner) x.remove(Integer.valueOf(3));
        }

        return x;
    }

    public void moveRandom(GameMap gameMap) {
        List<Integer> x = assessSurrounding(gameMap);
        if (x.size() == 0) return;
        if (x.size() == 1) {
            if (x.contains(0)) {moveInDirection(gameMap, Direction.UP); return;}
            else if (x.contains(1)) {moveInDirection(gameMap, Direction.RIGHT); return;}
            else if (x.contains(2)) {moveInDirection(gameMap, Direction.DOWN); return;}
            else {moveInDirection(gameMap, Direction.LEFT); return;}
        }
        Random rand = new Random();
        int int_random = rand.nextInt(4);
        
        while (!x.contains(int_random)) {
            int_random = rand.nextInt(4);
        }

        if (int_random == 0) moveInDirection(gameMap, Direction.UP);
        else if (int_random == 1) moveInDirection(gameMap, Direction.RIGHT);
        else if (int_random == 2) moveInDirection(gameMap, Direction.DOWN);
        else if (int_random == 3) moveInDirection(gameMap, Direction.LEFT);

    }

    abstract public void nextStep(GameMap gameMap);

    public Boolean notStuckInSwampTile(List<Entity> entityList) {
        
        // Boolean onSwampTile = false;

        for (Entity entity : entityList) {
            if (entity instanceof SwampTile) {
                if (this.swampTurn == -1) {
                    this.maxSwampTurn = ((SwampTile) entity).getMovementFactor();
                    this.swampTurn = ((SwampTile) entity).getMovementFactor();
                } else {
                    this.swampTurn--;
                }
                // onSwampTile = true;
            } 
        }

        // if (!onSwampTile) this.swampTurn = -1;

        if (this.swampTurn > 0) {
            return false;
        } else if (this.swampTurn == -1) {
            return true;
        } else {
            this.swampTurn = -1;
            return true;
        }
    }

    public Direction advancedDirectionToPlayer(GameMap gameMap) {
        Position monsters = this.getPosition();
        List<Integer> surroundings = assessSurrounding(gameMap);
        int up = 101; int down = 101; int left = 101; int right = 101;
        int visited[][] = new int[200][200];
        for (int i = 0; i < 200; i++) {
            for (int j = 0; j < 200; j++) {
                visited[i][j] = 100;
            }
        }



        if (surroundings.contains(0)) {
            up = recursiveDirectionUp(gameMap, monsters.getX(), monsters.getY() - 1, 0, visited);
        } 
        if (surroundings.contains(1)) {
            right = recursiveDirectionRight(gameMap, monsters.getX() + 1, monsters.getY(), 0, visited);
        } 
        if (surroundings.contains(2)) {
            down = recursiveDirectionDown(gameMap, monsters.getX(), monsters.getY() + 1, 0, visited);
        } 
        if (surroundings.contains(3)) {
            left = recursiveDirectionLeft(gameMap, monsters.getX() - 1, monsters.getY(), 0, visited);
        } 

        if (left == 101 && right == 101 && up == 101 && down == 101) return null;
        if (up <= down && up <= left && up <= right) return Direction.UP;
        if (left <= down && left <= up && left <= right) return Direction.LEFT;
        if (down <= left && down <= up && down <= right) return Direction.DOWN;
        if (right <= down && right <= up && right <= left) return Direction.RIGHT;
        return null;
    }

    public Integer recursiveDirectionUp(GameMap gameMap, int x, int y, int z, int[][] visited) {
        List<Entity> entityList = gameMap.getEntityListByCoordinate(x, y);
        List<Integer> surroundings = gameMap.assessSurroundingsMap(x, y);
        boolean playerFound = false;
        
        z++;
        
        for (Entity entity : entityList) {
            if (entity.getType() == "player") playerFound = true;
        }

        if (visited[x + 100][y + 100] <= z) return 101;
        else visited[x + 100][y + 100] = z;

        int up = 101; int left = 101; int right = 101; 
        
        if (playerFound || z == 30) {
            return z;
        } else {
            if (surroundings.contains(1)) {
                right = recursiveDirectionRight(gameMap, x + 1, y, z, visited);
            } 
            if (surroundings.contains(0)) {
                up = recursiveDirectionUp(gameMap, x, y - 1, z, visited);
            } 
            if (surroundings.contains(3)) {
                left = recursiveDirectionLeft(gameMap, x - 1, y, z, visited);
            } 
        }
        return Helper.minimum(right, Helper.minimum(up, left));
    }

    public Integer recursiveDirectionDown(GameMap gameMap, int x, int y, int z, int[][] visited) {
        List<Entity> entityList = gameMap.getEntityListByCoordinate(x, y);
        List<Integer> surroundings = gameMap.assessSurroundingsMap(x, y);
        boolean playerFound = false;
        
        z++;
        
        for (Entity entity : entityList) {
            if (entity.getType() == "player") playerFound = true;
        }

        if (visited[x + 100][y + 100] <= z) return 101;
        else visited[x + 100][y + 100] = z;

        int down = 101; int left = 101; int right = 101; 
        
        if (playerFound || z == 30) {
            return z;
        } else {
            if (surroundings.contains(2)) {
                down = recursiveDirectionDown(gameMap, x, y + 1, z, visited);
            } 
            if (surroundings.contains(1)) {
                right = recursiveDirectionRight(gameMap, x + 1, y, z, visited);
            } 
            if (surroundings.contains(3)) {
                left = recursiveDirectionLeft(gameMap, x - 1, y, z, visited);
            } 
        }
        return Helper.minimum(Helper.minimum(down, right), left);
    }

    public Integer recursiveDirectionLeft(GameMap gameMap, int x, int y, int z, int[][] visited) {
        List<Entity> entityList = gameMap.getEntityListByCoordinate(x, y);
        List<Integer> surroundings = gameMap.assessSurroundingsMap(x, y);
        boolean playerFound = false;
        
        z++;
        
        for (Entity entity : entityList) {
            if (entity.getType() == "player") playerFound = true;
        }

        if (visited[x + 100][y + 100] <= z) return 101;
        else visited[x + 100][y + 100] = z;

        int up = 101; int down = 101; int left = 101; 
        
        if (playerFound || z == 30) {
            return z;
        } else {
            if (surroundings.contains(0)) {
                up = recursiveDirectionUp(gameMap, x, y - 1, z, visited);
            } 
            if (surroundings.contains(3)) {
                left = recursiveDirectionLeft(gameMap, x - 1, y, z, visited);
            } 
            if (surroundings.contains(2)) {
                down = recursiveDirectionDown(gameMap, x, y + 1, z, visited);
            } 
        }
        return Helper.minimum(Helper.minimum(up, left), down);
    }

    public Integer recursiveDirectionRight(GameMap gameMap, int x, int y, int z, int[][] visited) {
        List<Entity> entityList = gameMap.getEntityListByCoordinate(x, y);
        List<Integer> surroundings = gameMap.assessSurroundingsMap(x, y);
        boolean playerFound = false;
        
        z++;
        
        for (Entity entity : entityList) {
            if (entity.getType() == "player") playerFound = true;
        }

        if (visited[x + 100][y + 100] <= z) return 101;
        else visited[x + 100][y + 100] = z;

        int up = 101; int down = 101; int right = 101;
        
        if (playerFound || z == 30) {
            return z;
        } else {
            if (surroundings.contains(0)) {
                up = recursiveDirectionUp(gameMap, x, y - 1, z, visited);
            }
            if (surroundings.contains(2)) {
                down = recursiveDirectionDown(gameMap, x, y + 1, z, visited);
            } 
            if (surroundings.contains(1)) {
                right = recursiveDirectionRight(gameMap, x + 1, y, z, visited);
            } 
        }
        return Helper.minimum(up, Helper.minimum(down, right));
    }

    public Direction runAway(GameMap gameMap) {
        List<Integer> surroundings = assessSurrounding(gameMap);

        if (advancedDirectionToPlayer(gameMap) == Direction.UP && surroundings.contains(2)) return Direction.DOWN;
        if (advancedDirectionToPlayer(gameMap) == Direction.DOWN && surroundings.contains(0)) return Direction.UP;
        if (advancedDirectionToPlayer(gameMap) == Direction.LEFT && surroundings.contains(1)) return Direction.RIGHT;
        if (advancedDirectionToPlayer(gameMap) == Direction.RIGHT && surroundings.contains(3)) return Direction.LEFT;

        if (advancedDirectionToPlayer(gameMap) == Direction.UP || advancedDirectionToPlayer(gameMap) == Direction.DOWN) {
            if (gameMap.getPlayer().getPosition().getX() < this.getPosition().getX() && surroundings.contains(1)) return Direction.RIGHT;
            else if (gameMap.getPlayer().getPosition().getX() > this.getPosition().getX() && surroundings.contains(3)) return Direction.LEFT;
        }

        if (advancedDirectionToPlayer(gameMap) == Direction.LEFT || advancedDirectionToPlayer(gameMap) == Direction.RIGHT) {
            if (gameMap.getPlayer().getPosition().getY() < this.getPosition().getY() && surroundings.contains(2)) return Direction.DOWN;
            else if (gameMap.getPlayer().getPosition().getY() > this.getPosition().getY() && surroundings.contains(0)) return Direction.UP;
        }

        return null;
    }
}
