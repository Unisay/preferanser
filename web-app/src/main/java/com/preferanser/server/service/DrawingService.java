package com.preferanser.server.service;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.dao.DrawingDao;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.DrawingEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.server.exception.NotFoundException;
import com.preferanser.server.exception.UnauthorizedException;
import com.preferanser.shared.util.Clock;

public class DrawingService {

    private final DrawingDao drawingDao;
    private final DealDao dealDao;
    private final Provider<AuthenticationService> authenticationServiceProvider;

    @Inject
    public DrawingService(DrawingDao drawingDao, DealDao dealDao, Provider<AuthenticationService> authenticationServiceProvider) {
        this.drawingDao = drawingDao;
        this.dealDao = dealDao;
        this.authenticationServiceProvider = authenticationServiceProvider;
    }

    public DrawingEntity get(long dealId, long drawingId) {
        Optional<DealEntity> dealEntityOptional = dealDao.get(dealId);
        if (!dealEntityOptional.isPresent()) {
            throw new NotFoundException(DealEntity.class, dealId);
        }

        Optional<DrawingEntity> drawingEntityOptional = drawingDao.get(dealEntityOptional.get(), drawingId);
        if (!dealEntityOptional.isPresent()) {
            throw new NotFoundException(DrawingEntity.class, drawingId);
        }

        return drawingEntityOptional.get();
    }

    public DrawingEntity save(DrawingEntity drawingEntity) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();
        Long currentUserId = currentUser.getId();

        Optional<DealEntity> dealEntityOptional = dealDao.get(drawingEntity.getDeal());
        if (!dealEntityOptional.isPresent()) {
            throw new IllegalArgumentException("Deal is not present");
        }

        DealEntity dealEntity = dealEntityOptional.get();
        long dealOwnerId = dealEntity.getOwner().getId();
        if (dealOwnerId != currentUserId) {
            throw new UnauthorizedException("Its not allowed to save drawings for deals you don't own");
        }

        // TODO: validate turns (e.g. unique cards)

        drawingEntity.setId(null);
        drawingEntity.setCreated(Clock.getNow());
        DrawingEntity savedDrawing = drawingDao.save(drawingEntity);

        assert savedDrawing != null : "drawingDao.save(" + drawingEntity + ") returned null";
        return savedDrawing;
    }

}
