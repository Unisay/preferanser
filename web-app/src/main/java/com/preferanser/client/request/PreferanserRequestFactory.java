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

package com.preferanser.client.request;

import com.google.web.bindery.requestfactory.shared.*;
import com.preferanser.client.request.proxy.AuthInfoProxy;
import com.preferanser.client.request.proxy.DealProxy;
import com.preferanser.server.service.DealService;
import com.preferanser.server.service.impl.AppengineUserService;
import com.preferanser.server.util.SpringServiceLocator;

public interface PreferanserRequestFactory extends RequestFactory {

    @Service(value = AppengineUserService.class, locator = SpringServiceLocator.class)
    interface UserServiceRequest extends RequestContext {
        Request<AuthInfoProxy> getAuthInfo();
    }

    @Service(value = DealService.class, locator = SpringServiceLocator.class)
    interface DealServiceRequest extends RequestContext {
        Request<Void> persist(DealProxy dealProxy);
    }

    UserServiceRequest userService();
    DealServiceRequest dealService();

}
