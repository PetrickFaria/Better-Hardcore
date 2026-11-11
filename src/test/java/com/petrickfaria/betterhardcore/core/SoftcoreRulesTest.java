package com.petrickfaria.betterhardcore.core;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class SoftcoreRulesTest {
    private SoftcoreRules.Progress p(int health, long ticks) { return new SoftcoreRules.Progress(health, ticks, false); }

    @Test void startsAtThreeHeartsAndUsesThirtyMinuteIntervals() {
        assertEquals(6, SoftcoreRules.START_HEALTH);
        assertEquals(6, SoftcoreRules.tick(p(6, SoftcoreRules.TICKS_PER_REWARD - 2), true).maxHealth());
        assertEquals(7, SoftcoreRules.tick(p(6, SoftcoreRules.TICKS_PER_REWARD - 1), true).maxHealth());
    }

    @Test void hardcoreStartsAtSevenHeartsWithoutUsingSoftcoreProgression() {
        assertEquals(14, SoftcoreRules.HARDCORE_START_HEALTH);
        assertEquals(6, SoftcoreRules.START_HEALTH);
    }

    @Test void timeCountsOnlyWhileAlive() {
        var current = p(6, 50);
        assertEquals(current, SoftcoreRules.tick(current, false));
    }

    @Test void clampsAtThirtyHealthAndDoesNotBankTimeAtCap() {
        var result = SoftcoreRules.tick(p(30, 100), true);
        assertEquals(30, result.maxHealth());
        assertEquals(0, result.survivedTicks());
        assertEquals(30, SoftcoreRules.tick(p(29, SoftcoreRules.TICKS_PER_REWARD - 1), true).maxHealth());
    }

    @Test void deathCostsTwoHealthPointsAndKeepsPartialSurvivalTime() {
        var result = SoftcoreRules.die(p(20, 1234));
        assertEquals(18, result.maxHealth());
        assertEquals(1234, result.survivedTicks());
        assertFalse(result.permanentlyDead());
    }

    @Test void twoHeartsMayFallToOneButLowerCapacityIsPermanent() {
        assertEquals(2, SoftcoreRules.die(p(4, 0)).maxHealth());
        assertTrue(SoftcoreRules.die(p(3, 0)).permanentlyDead());
        assertTrue(SoftcoreRules.die(p(2, 0)).permanentlyDead());
    }

    @Test void permanentDeathCannotBeReversedByProgression() {
        var dead = new SoftcoreRules.Progress(2, 0, true);
        assertEquals(dead, SoftcoreRules.tick(dead, true));
        assertEquals(dead, SoftcoreRules.die(dead));
    }

    @Test void recoveryAfterDeathUsesRemainingSurvivalIntervals() {
        var afterDeath = SoftcoreRules.die(p(16, 0));
        assertEquals(14, afterDeath.maxHealth());
        var recovered = SoftcoreRules.tick(p(afterDeath.maxHealth(), SoftcoreRules.TICKS_PER_REWARD - 1), true);
        assertEquals(15, recovered.maxHealth());
    }

    @Test void anHourGrantsTwoHalfHeartRewards() {
        var first = SoftcoreRules.tick(p(6, 2 * SoftcoreRules.TICKS_PER_REWARD - 1), true);
        assertEquals(8, first.maxHealth());
        assertEquals(0, first.survivedTicks());
        var second = SoftcoreRules.tick(first, true);
        assertEquals(8, second.maxHealth());
        assertEquals(1, second.survivedTicks());
    }

    @Test void fractionalHeartsLoseExactlyOneFullHeart() {
        assertEquals(5, SoftcoreRules.die(p(7, 0)).maxHealth());
        assertEquals(3, SoftcoreRules.die(p(5, 0)).maxHealth());
    }

    @Test void highAndInvalidSavedValuesAreClampedByRules() {
        assertEquals(30, SoftcoreRules.tick(p(31, 0), true).maxHealth());
        assertEquals(2, SoftcoreRules.die(p(1, 0)).maxHealth());
    }

    @Test void noRewardIsGrantedWhileDeadOrAfterGameOver() {
        var deadPlayer = SoftcoreRules.tick(p(6, SoftcoreRules.TICKS_PER_REWARD - 1), false);
        assertEquals(6, deadPlayer.maxHealth());
        assertEquals(SoftcoreRules.TICKS_PER_REWARD - 1, deadPlayer.survivedTicks());
        var permanent = new SoftcoreRules.Progress(2, 30, true);
        assertEquals(permanent, SoftcoreRules.tick(permanent, true));
    }

    @Test void simulatedTwelveHoursStopsAtFifteenHearts() {
        var progress = p(SoftcoreRules.START_HEALTH, 0);
        for (int tick = 0; tick < 24 * SoftcoreRules.TICKS_PER_REWARD; tick++) {
            progress = SoftcoreRules.tick(progress, true);
        }
        assertEquals(SoftcoreRules.MAX_HEALTH, progress.maxHealth());
        assertEquals(0, progress.survivedTicks());
    }
}
