package com.preferanser.server.dao;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.shared.domain.Players;

import java.util.Date;

abstract class DaoTestHelper {

    static DealEntity buildDealEntity(String name, Date created, Number ownerId) {
        return buildDealEntity(name, created, buildUser(ownerId, true));
    }

    static DealEntity buildDealEntity(String name, Date created, UserEntity userEntity) {
        DealEntity deal = new DealEntity();
        deal.setName(name);
        deal.setDescription(name + "_desc");
        deal.setOwner(userEntity);
        deal.setPlayers(Players.THREE);
        deal.setCreated(created);
        return deal;
    }

    static UserEntity buildUser(Number id, boolean admin) {
        UserEntity user = new UserEntity();
        user.setId(id.longValue());
        user.setGoogleId("google_" + id);
        user.setAdmin(admin);
        return user;
    }
}
