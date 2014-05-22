package com.preferanser.server.dao;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.googlecode.objectify.Key;
import com.preferanser.server.dao.objectify.OfyFactory;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.DrawingEntity;

import javax.validation.Validator;
import java.util.List;

public class DrawingDao extends BaseDao<DrawingEntity> {

    @Inject
    public DrawingDao(OfyFactory ofyFactory, Validator validator) {
        super(DrawingEntity.class, ofyFactory, validator);
    }

    public Optional<DrawingEntity> get(DealEntity dealEntity, Long drawingId) {
        return get(Key.create(dealEntity.getKey(), DrawingEntity.class, drawingId));
    }

    public List<DrawingEntity> getAll(DealEntity dealEntity) {
        return ofy().load().type(DrawingEntity.class).ancestor(dealEntity).list();
    }

}
