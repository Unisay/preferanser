package com.preferanser.server.service;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.dao.DrawingDao;
import com.preferanser.server.dao.UserDao;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.DrawingEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.server.exception.NotFoundException;
import com.preferanser.server.exception.UnauthorizedException;
import com.preferanser.shared.util.Clock;

import java.util.List;

public class DrawingService {

    private final DrawingDao drawingDao;
    private final DealDao dealDao;
    private final UserDao userDao;
    private final Provider<AuthenticationService> authenticationServiceProvider;

    @Inject
    public DrawingService(DrawingDao drawingDao, DealDao dealDao, UserDao userDao, Provider<AuthenticationService> authenticationServiceProvider) {
        this.drawingDao = drawingDao;
        this.dealDao = dealDao;
        this.userDao = userDao;
        this.authenticationServiceProvider = authenticationServiceProvider;
    }

    public DrawingEntity get(long dealId, long drawingId) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();
        Optional<DrawingEntity> drawingEntityOptional = drawingDao.find(getUserDealById(currentUser, dealId), drawingId);
        if (!drawingEntityOptional.isPresent()) {
            throw new NotFoundException(DrawingEntity.class, drawingId);
        }
        return drawingEntityOptional.get();
    }

    public DrawingEntity get(long userId, long dealId, long drawingId) {
        Optional<DrawingEntity> drawingEntityOptional = drawingDao.find(getUserDealById(userDao.getById(userId), dealId), drawingId);
        if (!drawingEntityOptional.isPresent()) {
            throw new NotFoundException(DrawingEntity.class, drawingId);
        }
        return drawingEntityOptional.get();
    }

    public List<DrawingEntity> getAll(long dealId) {
        return drawingDao.getAllDescDateCreated(getCurrentUserDealById(dealId));
    }

    public DrawingEntity save(DrawingEntity drawingEntity) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();
        Long currentUserId = currentUser.getId();

        Optional<DealEntity> dealEntityOptional = dealDao.find(drawingEntity.getDeal());
        if (!dealEntityOptional.isPresent()) {
            throw new NotFoundException(DealEntity.class, drawingEntity.getDeal().getId());
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

    public void delete(Long dealId, Long drawingId) {
        Optional<DrawingEntity> drawing = drawingDao.find(getCurrentUserDealById(dealId), drawingId);
        if (!drawing.isPresent()) {
            throw new NotFoundException(DrawingEntity.class, drawingId);
        }
        drawingDao.deleteAsync(drawing.get());
    }

    private DealEntity getCurrentUserDealById(long dealId) {
        return getUserDealById(authenticationServiceProvider.get().getCurrentUserOrThrow(), dealId);
    }

    private DealEntity getUserDealById(UserEntity userEntity, long dealId) {
        Optional<DealEntity> dealEntityOptional = dealDao.find(userEntity, dealId);
        if (dealEntityOptional.isPresent()) {
            return dealEntityOptional.get();
        } else {
            throw new NotFoundException(DealEntity.class, dealId);
        }
    }

}
