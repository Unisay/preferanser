package com.preferanser.server.transformer;

import com.preferanser.server.entity.DrawingEntity;
import com.preferanser.shared.domain.Drawing;

public class DrawingTransformer extends BaseTransformer<Drawing, DrawingEntity> {

    @Override public DrawingEntity toEntity(Drawing drawing) {
        DrawingEntity entity = new DrawingEntity();
        entity.setId(drawing.getId());
        entity.setDeal(drawing.getUserId(), drawing.getDealId());
        entity.setName(drawing.getName());
        entity.setDescription(drawing.getDescription());
        entity.setTurns(drawing.getTurns());
        entity.setCreated(drawing.getCreatedAt());
        return entity;
    }

    @Override public Drawing fromEntity(DrawingEntity entity) {
        return new Drawing(
            entity.getId(),
            entity.getDeal().getParent().getId(), entity.getDeal().getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getTurns(),
            entity.getCreated());
    }
}
