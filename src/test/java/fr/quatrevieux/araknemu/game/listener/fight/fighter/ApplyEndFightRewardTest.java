package fr.quatrevieux.araknemu.game.listener.fight.fighter;

import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.ending.reward.DropReward;
import fr.quatrevieux.araknemu.game.fight.ending.reward.RewardType;
import fr.quatrevieux.araknemu.game.fight.event.FightFinished;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ApplyEndFightRewardTest extends FightBaseCase {
    private Fight fight;
    private ApplyEndFightReward listener;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();
        listener = new ApplyEndFightReward(player.fighter());
    }

    @Test
    void onFightFinishedWithoutReward() {
        listener.on(new FightFinished(new DropReward(RewardType.WINNER, player.fighter())));

        assertFalse(player.isFighting());
    }

    @Test
    void onFightFinishedWithXpReward() {
        long lastXp = player.experience().current();

        listener.on(new FightFinished(new DropReward(RewardType.WINNER, player.fighter(), 1000, 0)));

        assertFalse(player.isFighting());
        assertEquals(lastXp + 1000, player.experience().current());
    }
}
