package fr.quatrevieux.araknemu.game.fight.castable;

import fr.quatrevieux.araknemu.data.value.EffectArea;
import fr.quatrevieux.araknemu.game.fight.Fight;
import fr.quatrevieux.araknemu.game.fight.FightBaseCase;
import fr.quatrevieux.araknemu.game.fight.fighter.Fighter;
import fr.quatrevieux.araknemu.game.fight.fighter.player.PlayerFighter;
import fr.quatrevieux.araknemu.game.fight.map.FightCell;
import fr.quatrevieux.araknemu.game.spell.Spell;
import fr.quatrevieux.araknemu.game.spell.SpellConstraints;
import fr.quatrevieux.araknemu.game.spell.effect.SpellEffect;
import fr.quatrevieux.araknemu.game.spell.effect.area.CellArea;
import fr.quatrevieux.araknemu.game.spell.effect.area.CircleArea;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.Arrays;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class CastScopeTest extends FightBaseCase {
    private Fight fight;
    private PlayerFighter caster;
    private PlayerFighter target;

    @Override
    @BeforeEach
    public void setUp() throws Exception {
        super.setUp();

        fight = createFight();

        caster = player.fighter();
        target = other.fighter();
    }

    @Test
    void getters() {
        Fighter caster = Mockito.mock(Fighter.class);
        FightCell target = Mockito.mock(FightCell.class);
        Castable action = Mockito.mock(Castable.class);

        CastScope scope = new CastScope(action, caster, target);

        assertSame(caster, scope.caster());
        assertSame(target, scope.target());
        assertSame(action, scope.action());
    }

    @Test
    void withEffectsWillResolveTarget() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CellArea());
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertEquals(effect, scope.effects().get(0).effect());
        assertEquals(Collections.singletonList(target), scope.effects().get(0).targets());
    }

    @Test
    void withEffectsWithFreeCellConstraintWillNotResolveTargets() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(true);

        CastScope scope = new CastScope(spell, caster, fight.map().get(123));

        scope.withEffects(Collections.singletonList(effect));

        assertCount(1, scope.effects());
        assertEquals(effect, scope.effects().get(0).effect());
        assertEquals(Collections.emptyList(), scope.effects().get(0).targets());
    }

    @Test
    void resolveTargetsWithAreaTwoFighters() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 10)));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, fight.map().get(123));

        scope.withEffects(Collections.singletonList(effect));

        assertEquals(Arrays.asList(caster, target), scope.effects().get(0).targets());
    }

    @Test
    void resolveTargetsWithAreaOneFighter() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2)));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, target.cell());

        scope.withEffects(Collections.singletonList(effect));

        assertEquals(Arrays.asList(target), scope.effects().get(0).targets());
    }

    @Test
    void resolveTargetsWithAreaNoTargets() {
        SpellEffect effect = Mockito.mock(SpellEffect.class);
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        Mockito.when(effect.area()).thenReturn(new CircleArea(new EffectArea(EffectArea.Type.CIRCLE, 2)));
        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        CastScope scope = new CastScope(spell, caster, fight.map().get(2));

        scope.withEffects(Collections.singletonList(effect));

        assertEquals(Collections.emptyList(), scope.effects().get(0).targets());
    }

    @Test
    void withRandomEffectsOnlyProbableEffectsHalfProbability() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        SpellEffect effect2 = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        Mockito.when(effect1.area()).thenReturn(new CellArea());
        Mockito.when(effect1.probability()).thenReturn(50);
        Mockito.when(effect2.area()).thenReturn(new CellArea());
        Mockito.when(effect2.probability()).thenReturn(50);

        CastScope scope = new CastScope(spell, caster, target.cell());

        int c1 = 0, c2 = 0;

        for (int i = 0; i < 1000; ++i) {
            scope.withRandomEffects(Arrays.asList(effect1, effect2));

            assertCount(1, scope.effects());

            if (effect1 == scope.effects().get(0).effect()) {
                ++c1;
            } else {
                ++c2;
            }
        }

        assertEquals(1000, c1 + c2);
        assertBetween(400, 600, c1);
        assertBetween(400, 600, c2);
    }

    @Test
    void withRandomEffectsOnlyProbableEffectsNotHalfProbability() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        SpellEffect effect2 = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        Mockito.when(effect1.area()).thenReturn(new CellArea());
        Mockito.when(effect1.probability()).thenReturn(80);
        Mockito.when(effect2.area()).thenReturn(new CellArea());
        Mockito.when(effect2.probability()).thenReturn(20);

        CastScope scope = new CastScope(spell, caster, target.cell());

        int c1 = 0, c2 = 0;

        for (int i = 0; i < 1000; ++i) {
            scope.withRandomEffects(Arrays.asList(effect1, effect2));

            assertCount(1, scope.effects());

            if (effect1 == scope.effects().get(0).effect()) {
                ++c1;
            } else {
                ++c2;
            }
        }

        assertEquals(1000, c1 + c2);
        assertBetween(700, 900, c1);
        assertBetween(100, 300, c2);
    }

    @Test
    void withRandomEffectsWithOnProbableEffectCanBeNotChoose() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        Mockito.when(effect1.area()).thenReturn(new CellArea());
        Mockito.when(effect1.probability()).thenReturn(20);

        CastScope scope = new CastScope(spell, caster, target.cell());

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            scope.withRandomEffects(Collections.singletonList(effect1));

            if (scope.effects().size() == 1) {
                ++count;
            }
        }

        assertBetween(100, 300, count);
    }

    @Test
    void withRandomEffectsWithRandomAndPermanentEffects() {
        Spell spell = Mockito.mock(Spell.class);
        SpellConstraints constraints = Mockito.mock(SpellConstraints.class);

        SpellEffect effect1 = Mockito.mock(SpellEffect.class);
        SpellEffect effect2 = Mockito.mock(SpellEffect.class);

        Mockito.when(spell.constraints()).thenReturn(constraints);
        Mockito.when(constraints.freeCell()).thenReturn(false);

        Mockito.when(effect1.area()).thenReturn(new CellArea());
        Mockito.when(effect1.probability()).thenReturn(0);
        Mockito.when(effect2.area()).thenReturn(new CellArea());
        Mockito.when(effect2.probability()).thenReturn(50);

        CastScope scope = new CastScope(spell, caster, target.cell());

        int count = 0;

        for (int i = 0; i < 1000; ++i) {
            scope.withRandomEffects(Arrays.asList(effect1, effect2));

            if (scope.effects().size() == 1) {
                assertEquals(effect1, scope.effects().get(0).effect());
            } else {
                assertEquals(effect1, scope.effects().get(0).effect());
                assertEquals(effect2, scope.effects().get(1).effect());
                ++count;
            }
        }

        assertBetween(400, 600, count);
    }
}