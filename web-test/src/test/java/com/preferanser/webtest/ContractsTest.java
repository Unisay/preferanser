package com.preferanser.webtest;

import com.preferanser.domain.Contract;
import com.preferanser.webtest.requirements.Application;
import net.thucydides.core.annotations.Story;
import org.junit.Test;

import static com.preferanser.domain.Cardinal.*;

@Story(Application.Table.Contracts.class)
public class ContractsTest extends TableTest {

    @Test
    public void user_can_specify_contracts() {
        endUser.onTheTablePage()
                .editsNewDeal()
                .specifiesContract(NORTH, Contract.SIX_SPADE)
                .specifiesContract(EAST, Contract.PASS)
                .specifiesContract(WEST, Contract.WHIST)
                .switchesToPlayMode()
                .canSeeContract(NORTH, "6♠")
                .canSeeContract(EAST, "пас")
                .canSeeContract(WEST, "вист")
                .canSeeNoContract(SOUTH);
    }


}
