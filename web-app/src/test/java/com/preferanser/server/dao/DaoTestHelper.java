package com.preferanser.server.dao;

import com.preferanser.shared.domain.Players;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.entity.User;

import java.util.Date;

abstract class DaoTestHelper {

    static Deal buildDeal(String name, Date created, String ownerId) {
        Deal deal = new Deal();
        deal.setName(name);
        deal.setDescription(name + "_desc");
        deal.setOwner(User.key(ownerId));
        deal.setPlayers(Players.THREE);
        deal.setCreated(created);
        return deal;
    }

    static User buildUser(String googleId, boolean admin) {
        User user = new User();
        user.setAdmin(admin);
        user.setId(googleId);
        return user;
    }
}
