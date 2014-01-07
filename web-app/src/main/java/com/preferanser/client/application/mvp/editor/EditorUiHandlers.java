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

package com.preferanser.client.application.mvp.editor;

import com.preferanser.client.application.mvp.TableUiHandlers;
import com.preferanser.shared.domain.Hand;

/**
 * Game builder UI handlers
 */
public interface EditorUiHandlers extends TableUiHandlers {

    /**
     * User chose contract for hand
     *
     * @param hand for which contract is chosen
     */
    void chooseContract(Hand hand);

    /**
     * User chose first turn
     *
     * @param hand who turns first
     */
    void chooseTurn(Hand hand);

    /**
     * Switch to player page
     */
    void switchToPlayer();

    /**
     * Save deal
     */
    void onDealSaveClicked();

    /**
     * Open deal
     */
    void onDealOpenClicked();
}
