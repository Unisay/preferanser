package com.preferanser.webtest;

import com.preferanser.webtest.requirements.Application;
import net.thucydides.core.annotations.Story;
import org.junit.Test;

import static com.preferanser.domain.Cardinal.*;

@Story(Application.Table.TurnPointer.Editing.class)
public class TurnPointerTest extends TableTest {

    @Test
    public void turnPointerEditing() throws Exception {
        endUser.onTheTablePage()
                .editsNewDeal()
                .withTurnPointer()
                .canSeeNoActiveTurnPointer()
                .activatesTurnPointer(NORTH).canSeeOnlyTurnPointerActive(NORTH)
                .activatesTurnPointer(EAST).canSeeOnlyTurnPointerActive(EAST)
                .activatesTurnPointer(SOUTH).canSeeOnlyTurnPointerActive(SOUTH)
                .activatesTurnPointer(WEST).canSeeOnlyTurnPointerActive(WEST)
                .withTable()
                .resetsEditedDeal()
                .withTurnPointer()
                .canSeeNoActiveTurnPointer();
    }

    @Test
    public void turnPointerSwitchToPlayMode() throws Exception {
            endUser.onTheTablePage()
                    .editsNewDeal()
                    .withTurnPointer()
                    .activatesTurnPointer(NORTH)
                    .withTable()
                    .switchesToPlayMode()
                    .withTurnPointer()
                    .canSeeOnlyTurnPointer(NORTH);
    }

}
