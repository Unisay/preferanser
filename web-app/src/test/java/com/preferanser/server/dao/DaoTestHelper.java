package com.preferanser.server.dao;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.DrawingEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.domain.Players;
import com.preferanser.shared.util.Clock;

import java.util.Date;

import static org.testng.collections.Lists.newArrayList;

abstract class DaoTestHelper {

    static DealEntity buildDealEntity() {
        return buildDealEntity("name", Clock.getNow(), 1L);
    }

    static DealEntity buildDealEntity(Long id) {
        DealEntity dealEntity = buildDealEntity("name", Clock.getNow(), 1L);
        dealEntity.setId(id);
        return dealEntity;
    }

    static DealEntity buildDealEntity(String name, Date created, Number ownerId) {
        return buildDealEntity(name, created, buildUser(ownerId, true));
    }

    static DealEntity buildDealEntity(String name, Date created, UserEntity userEntity) {
        DealEntity dealEntity = new DealEntity();
        dealEntity.setName(name);
        dealEntity.setDescription(name + "_desc");
        dealEntity.setOwner(userEntity);
        dealEntity.setPlayers(Players.THREE);
        dealEntity.setCreated(created);
        return dealEntity;
    }

    static UserEntity buildUser(Number id, boolean admin) {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(id.longValue());
        userEntity.setGoogleId("google_" + id);
        userEntity.setAdmin(admin);
        return userEntity;
    }

    static DrawingEntity buildDrawingEntity(DealEntity dealEntity) {
        DrawingEntity drawingEntity = new DrawingEntity();
        drawingEntity.setId(null);
        drawingEntity.setName("name");
        drawingEntity.setDescription("desc");
        drawingEntity.setDeal(dealEntity);
        drawingEntity.setCreated(Clock.getNow());
        drawingEntity.setTurns(newArrayList(Card.values()));
        return drawingEntity;
    }

}
