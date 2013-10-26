package com.preferanser.shared;

import static com.preferanser.shared.Suit.*;

public enum Contract {

    SIX_SPADE(6, SPADE), 
    SIX_CLUB(6, CLUB),
    SIX_DIAMOND(6, DIAMOND),
    SIX_HEART(6, HEART),
    
    SEVEN_SPADE(7, SPADE),
    SEVEN_CLUB(7, CLUB),
    SEVEN_DIAMOND(7, DIAMOND),
    SEVEN_HEART(7, HEART),
    
    EIGHT_SPADE(8, SPADE),
    EIGHT_CLUB(8, CLUB),
    EIGHT_DIAMOND(8, DIAMOND),
    EIGHT_HEART(8, HEART),
    
    NINE_SPADE(9, SPADE),
    NINE_CLUB(9, CLUB),
    NINE_DIAMOND(9, DIAMOND),
    NINE_HEART(9, HEART),
    
    TEN_SPADE(10, SPADE),
    TEN_CLUB(10, CLUB),
    TEN_DIAMOND(10, DIAMOND),
    TEN_HEART(10, HEART),
    
    MISER(0, null),
    PASS(null, null),
    WHIST(null, null);

    private Contract(Integer tricksNumber, Suit trumpSuit) {
        this.tricksNumber = tricksNumber;
        this.trump = trumpSuit;
    }

    private Suit trump;
    private final Integer tricksNumber;

    public Integer getTricksNumber() {
        return tricksNumber;
    }

    public Suit getTrump() {
        return trump;
    }
}
