package com.preferanser.webtest;

import com.preferanser.webtest.requirements.Application;
import net.thucydides.core.annotations.Story;
import org.junit.Test;

import static com.preferanser.domain.Cardinal.*;
import static com.preferanser.domain.Contract.*;

@Story(Application.Table.Contracts.class)
public class ContractsTest extends TableTest {

    @Test
    public void userCanSpecifyContracts() {
        endUser.onTheTablePage()
                .editsNewDeal()
                .specifiesContract(NORTH, SIX_SPADE)
                .specifiesContract(EAST, PASS)
                .specifiesContract(WEST, WHIST)
                .switchesToPlayMode()
                .canSeeContract(NORTH, "6♠")
                .canSeeContract(EAST, "пас")
                .canSeeContract(WEST, "вист")
                .canSeeNoContract(SOUTH);
    }


}
