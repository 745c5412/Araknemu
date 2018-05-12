package fr.quatrevieux.araknemu.game.fight.type;

import fr.quatrevieux.araknemu.game.fight.ending.reward.generator.RewardsGenerator;

import java.time.Duration;

/**
 * Fight type parameters
 */
public interface FightType {
    /**
     * The type id
     */
    public int id();

    /**
     * Can cancel the fight without penalties
     */
    public boolean canCancel();

    /**
     * Does the fight type has a placement time limit ?
     *
     * @see FightType#placementTime() For get the placement time limit
     */
    public boolean hasPlacementTimeLimit();

    /**
     * Get the fight placement time in seconds
     * This value must be used only, and only if hasPlacementTimeLimit is set to true
     *
     * @see FightType#hasPlacementTimeLimit()
     */
    public int placementTime();

    /**
     * Get the maximum duration of a turn
     */
    public Duration turnDuration();

    /**
     * Get the rewards generator
     */
    public RewardsGenerator rewards();
}
