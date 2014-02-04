package com.preferanser.server.transformer;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.shared.domain.Deal;

public class DealTransformer extends BaseTransformer<Deal, DealEntity> {

    @Override public DealEntity toEntity(Deal deal) {
        DealEntity entity = new DealEntity();
        entity.setId(deal.getId());
        entity.setName(deal.getName());
        entity.setDescription(deal.getDescription());
        entity.setWidow(deal.getWidow());
        entity.setPlayers(deal.getPlayers());
        entity.setShared(deal.isShared());
        entity.setFirstTurn(deal.getFirstTurn());
        entity.setTurns(deal.getTurns());
        entity.setEastContract(deal.getEastContract());
        entity.setSouthContract(deal.getSouthContract());
        entity.setWestContract(deal.getWestContract());
        entity.setEastCards(deal.getEastCards());
        entity.setSouthCards(deal.getSouthCards());
        entity.setWestCards(deal.getWestCards());
        entity.setTurns(deal.getTurns());
        entity.setCreated(deal.getCreated());
        entity.setCurrentTrickIndex(deal.getCurrentTrickIndex());
        return entity;
    }

    @Override public Deal fromEntity(DealEntity entity) {
        Deal deal = new Deal();
        deal.setId(entity.getId());
        deal.setName(entity.getName());
        deal.setDescription(entity.getDescription());
        deal.setWidow(entity.getWidow());
        deal.setPlayers(entity.getPlayers());
        deal.setShared(entity.isShared());
        deal.setFirstTurn(entity.getFirstTurn());
        deal.setTurns(entity.getTurns());
        deal.setEastContract(entity.getEastContract());
        deal.setSouthContract(entity.getSouthContract());
        deal.setWestContract(entity.getWestContract());
        deal.setEastCards(entity.getEastCards());
        deal.setSouthCards(entity.getSouthCards());
        deal.setWestCards(entity.getWestCards());
        deal.setTurns(entity.getTurns());
        deal.setCreated(entity.getCreated());
        deal.setCurrentTrickIndex(entity.getCurrentTrickIndex());
        return deal;
    }
}
