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

import com.google.inject.Singleton;
import com.gwtplatform.mvp.client.gin.AbstractPresenterModule;
import com.preferanser.client.application.mvp.dialog.input.InputDialogPresenter;
import com.preferanser.client.application.mvp.dialog.input.InputDialogView;
import com.preferanser.client.application.mvp.editor.dialog.contract.ContractDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.contract.ContractDialogView;
import com.preferanser.client.application.mvp.editor.dialog.open.OpenDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.open.OpenDialogView;
import com.preferanser.client.application.mvp.editor.dialog.validation.ValidationDialogPresenter;
import com.preferanser.client.application.mvp.editor.dialog.validation.ValidationDialogView;

/**
 * Gin module for the mvp builder page
 */
public class EditorModule extends AbstractPresenterModule {

    @Override
    protected void configure() {
        bind(HasHandContracts.class).to(EditorPresenter.class).in(Singleton.class);
        bindSingletonPresenterWidget(OpenDialogPresenter.class, OpenDialogPresenter.TheView.class, OpenDialogView.class);
        bindSingletonPresenterWidget(ContractDialogPresenter.class, ContractDialogPresenter.TheView.class, ContractDialogView.class);
        bindSingletonPresenterWidget(ValidationDialogPresenter.class, ValidationDialogPresenter.TheView.class, ValidationDialogView.class);
        bindSingletonPresenterWidget(InputDialogPresenter.class, InputDialogPresenter.TheView.class, InputDialogView.class);
        bindPresenter(EditorPresenter.class, EditorPresenter.EditorView.class, EditorView.class, EditorPresenter.Proxy.class);
    }

}
