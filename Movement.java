package dungeonmania.Entities.MovingEntities;

import dungeonmania.GameMap.GameMap;

public interface Movement {
    public void nextStep(GameMap gameMap);
}