package dungeonmania.Entities.MovingEntities;

import dungeonmania.util.Position;

public class Hydra extends ZombieToast {

    private static double hydraHealthIncreaseAmount;
    private static double hydraHealthIncreaseRate;

    public Hydra(String id, Position position, double newAttack, double newHealth) {
        super(id, "hydra", position, newAttack, newHealth);
    }

    public static void setHydraHealthIncreaseAmount (double increaseAmount) {
        hydraHealthIncreaseAmount = increaseAmount;
    }

    public static void sethydraHealthIncreaseRate (double rate) {
        hydraHealthIncreaseRate = rate;
    }

    public double getHydraHealthIncreaseAmount() {
        return hydraHealthIncreaseAmount;
    }

    public double gethydraHealthIncreaseRate() {
        return hydraHealthIncreaseRate;
    }
}
