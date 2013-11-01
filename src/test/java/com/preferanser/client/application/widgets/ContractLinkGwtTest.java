/*
 * Preferanser is a program to simulate and calculate Russian Preferans Card game deals.
 *
 *     Copyright (C) 2013  Yuriy Lazarev <Yuriy.Lazarev@gmail.com>
 *
 *     This program is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     This program is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU General Public License for more details.
 *
 *     You should have received a copy of the GNU General Public License
 *     along with this program.  If not, see [http://www.gnu.org/licenses/].
 */

package com.preferanser.client.application.widgets;

import com.googlecode.gwt.test.GwtModule;
import com.googlecode.gwt.test.GwtTestWithMockito;
import com.googlecode.gwt.test.Mock;
import com.preferanser.client.application.i18n.PreferanserConstants;
import com.preferanser.client.application.table.TableStyle;
import com.preferanser.shared.Contract;
import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

/**
 * Gwt Unit test for the ContractLink
 */
@GwtModule("com.preferanser.Preferanser")
public class ContractLinkGwtTest extends GwtTestWithMockito {

    private ContractLink contractLink;

    @Mock
    private PreferanserConstants constants;

    @Mock
    private TableStyle tableStyle;

    private String expectedContract;

    @Before
    public void setUp() throws Exception {
        expectedContract = "6 Club";

        when(constants.noTrump()).thenReturn("nt");
        when(constants.whist()).thenReturn("w");
        when(constants.pass()).thenReturn("p");
        when(constants.miser()).thenReturn("m");
        when(constants.chooseContract()).thenReturn(expectedContract);

        when(tableStyle.contractLabel()).thenReturn("contract-label");
        when(tableStyle.contractLink()).thenReturn("contract-link");
        when(tableStyle.noTrump()).thenReturn("n");
        when(tableStyle.contractSuit()).thenReturn("s");
        when(tableStyle.contractTricks()).thenReturn("t");

        contractLink = new ContractLink(constants, tableStyle);
    }

    @Test
    public void testSetContract_SuitAndRank() throws Exception {
        when(constants.getString("CLUB_char")).thenReturn("Club");

        contractLink.setContract(Contract.EIGHT_CLUB);
        assertThat(contractLink.getHTML(), equalTo("<span class=\"t\">8</span> <span class=\"s club\">Club</span>"));
    }

    @Test
    public void testSetContract_RankNoSuit() throws Exception {
        contractLink.setContract(Contract.SIX_NO_TRUMP);
        assertThat(contractLink.getHTML(), equalTo("<span class=\"t\">6</span> <span class=\"n\">nt</span>"));
    }

    @Test
    public void testSetContract_Pass() throws Exception {
        contractLink.setContract(Contract.PASS);
        assertThat(contractLink.getHTML(), equalTo("p"));
    }

    @Test
    public void testSetContract_Whist() throws Exception {
        contractLink.setContract(Contract.WHIST);
        assertThat(contractLink.getHTML(), equalTo("w"));
    }

    @Test
    public void testSetContract_Miser() throws Exception {
        contractLink.setContract(Contract.MISER);
        assertThat(contractLink.getHTML(), equalTo("m"));
    }

    @Test
    public void testSetContract_Null() throws Exception {
        contractLink.setContract(null);
        assertThat(contractLink.getHTML(), equalTo(expectedContract));
    }
}
