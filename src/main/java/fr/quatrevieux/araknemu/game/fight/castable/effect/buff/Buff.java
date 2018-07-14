package fr.quatrevieux.araknemu.game.fight.castable.effect.buff;

import fr.quatrevieux.araknemu.game.fight.castable.Castable;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;

/**
 * Persistent effect
 *
 * The duration will be taken from the effect.
 * For overload the buff effect value (or duration), you must create a new effect instance
 */
final public class Buff {
    final private SpellEffect effect;
    final private Castable action;
    final private Fighter caster;
    final private Fighter target;
    final private BuffHook hook;

    private int remainingTurns;

    public Buff(SpellEffect effect, Castable action, Fighter caster, Fighter target, BuffHook hook) {
        this.effect = effect;
        this.action = action;
        this.caster = caster;
        this.target = target;
        this.hook = hook;

        this.remainingTurns = effect.duration();
    }

    /**
     * Get the buff effect
     */
    public SpellEffect effect() {
        return effect;
    }

    /**
     * Get the action which generates this buff
     */
    public Castable action() {
        return action;
    }

    /**
     * Get the buff caster
     */
    public Fighter caster() {
        return caster;
    }

    /**
     * Get the buff target
     */
    public Fighter target() {
        return target;
    }

    /**
     * Remaining turns for the buff effect
     *
     * When this value reached 0, the buff should be removed
     */
    public int remainingTurns() {
        return remainingTurns;
    }

    /**
     * Decrements the remaining turns
     *
     * You should call {@link Buff#valid()} for check if the buff is still valid or not
     */
    public void decrementRemainingTurns() {
        --remainingTurns;
    }

    /**
     * Increment remaining turns
     * Use this method when a self-buff is added
     */
    void incrementRemainingTurns() {
        ++remainingTurns;
    }

    /**
     * Get the related hook for the buff
     */
    public BuffHook hook() {
        return hook;
    }

    /**
     * Check if the buff is still valid
     */
    public boolean valid() {
        return remainingTurns > 0;
    }
}