package com.preferanser.server.transformer;

import com.preferanser.server.entity.DealEntity;
import com.preferanser.shared.domain.DealInfo;

public class DealInfoTransformer extends BaseTransformer<DealInfo, DealEntity> {

    @Override public DealEntity toEntity(DealInfo type) {
        throw new UnsupportedOperationException("Cannot transform DealInfo into DealEntity");
    }

    @Override public DealInfo fromEntity(DealEntity entity) {
        return new DealInfo(
            entity.getId(),
            entity.getName(),
            entity.getDescription(),
            entity.getOwner().getId(),
            entity.getCreated()
        );
    }

}
