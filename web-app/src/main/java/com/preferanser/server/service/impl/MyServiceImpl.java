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

package com.preferanser.server.service.impl;

import com.preferanser.server.business.User;
import com.preferanser.server.repos.MyEntityRepo;
import com.preferanser.server.service.MyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service("myService")
@Transactional
public class MyServiceImpl implements MyService {
    @Autowired
    private MyEntityRepo myEntityRepo;

    @Override
    public void create(User entity) {
        myEntityRepo.save(entity);
    }

    @Override
    public void delete(User entity) {
        myEntityRepo.delete(entity);
    }

    @Override
    @Transactional(readOnly = true)
    public List<User> loadAll(String searchToken) {
        String token = searchToken + "%";
        return myEntityRepo.findByFirstNameLikeOrLastNameLike(token, token);
    }
}
