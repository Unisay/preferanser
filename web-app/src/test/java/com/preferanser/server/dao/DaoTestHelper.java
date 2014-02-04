package com.preferanser.server.dao;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.shared.domain.Players;

import java.util.Date;

abstract class DaoTestHelper {

    static DealEntity buildDealEntity(String name, Date created, String ownerId) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(ownerId);

        DealEntity deal = new DealEntity();
        deal.setName(name);
        deal.setDescription(name + "_desc");
        deal.setOwner(userEntity);
        deal.setPlayers(Players.THREE);
        deal.setCreated(created);
        return deal;
    }

    static UserEntity buildUser(String googleId, boolean admin) {
        UserEntity user = new UserEntity();
        user.setAdmin(admin);
        user.setId(googleId);
        return user;
    }
}
