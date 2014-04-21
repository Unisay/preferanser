package com.preferanser.shared.domain;


import com.preferanser.shared.util.EnumRotator;

/**
 * Rotates turns on raspass
 */
public class RaspassTurnRotator implements TurnRotator {

    private final TurnRotator turnRotator;
    private int globalTurnIndex = 0;

    public RaspassTurnRotator(TurnRotator turnRotator) {
        this.turnRotator = turnRotator;
    }

    private RaspassTurnRotator(TurnRotator turnRotator, int globalTurnIndex) {
        this.turnRotator = turnRotator;
        this.globalTurnIndex = globalTurnIndex;
    }

    @Override public Hand current() {
        return globalTurnIndex == 0 || globalTurnIndex == 4
            ? Hand.WIDOW
            : turnRotator.current();
    }

    @Override public void next() {
        if (globalTurnIndex != 0 && globalTurnIndex != 4) {
            turnRotator.next();
        }
        globalTurnIndex++;
    }

    @Override public void prev() {
        if (globalTurnIndex != 1 && globalTurnIndex != 5) {
            turnRotator.prev();
        }
        globalTurnIndex--;
    }

    @Override public TurnRotator produceNextRotator(Hand winner) {
        Hand firstHand;
        if (globalTurnIndex <= 8 || winner == Hand.WIDOW) {
            firstHand = turnRotator.current();
        } else {
            firstHand = winner;
        }
        EnumRotator<Hand> enumRotator = new EnumRotator<Hand>(Hand.values(), firstHand, Hand.WIDOW);
        EnumTurnRotator enumTurnRotator = new EnumTurnRotator(enumRotator);
        return new RaspassTurnRotator(enumTurnRotator, globalTurnIndex);
    }

}
