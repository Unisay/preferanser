package com.preferanser.server.service;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.dao.UserDao;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.server.exception.NotFoundException;
import com.preferanser.server.exception.UnauthorizedException;
import com.preferanser.shared.util.Clock;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class DealService {

    private final DealDao dealDao;
    private final UserDao userDao;
    private final Provider<AuthenticationService> authenticationServiceProvider;

    @Inject
    public DealService(DealDao dealDao, UserDao userDao, Provider<AuthenticationService> authenticationServiceProvider) {
        this.dealDao = dealDao;
        this.userDao = userDao;
        this.authenticationServiceProvider = authenticationServiceProvider;
    }

    public void importSharedDeals(UserEntity user) {
        Set<DealEntity> importedDeals = newHashSet();
        for (DealEntity sharedDeal : dealDao.getSharedDeals()) {
            if (!user.getId().equals(sharedDeal.getOwner().getId())) {
                importedDeals.add(importDeal(sharedDeal, user));
            }
        }
        dealDao.save(importedDeals);
    }

    private DealEntity importDeal(DealEntity sharedDeal, UserEntity user) {
        DealEntity deal = new DealEntity(sharedDeal);
        deal.setId(null);
        deal.setShared(false);
        deal.setOwner(user);
        return deal;
    }

    public List<DealEntity> getCurrentUserOrSharedDeals() {
        Optional<UserEntity> userOptional = authenticationServiceProvider.get().getCurrentUser();
        if (userOptional.isPresent()) {
            return dealDao.getUserDeals(userOptional.get());
        } else {
            return dealDao.getSharedDeals();
        }
    }

    public DealEntity get(Long dealId) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();
        return get(currentUser, dealId);
    }

    public DealEntity get(Long userId, Long dealId) {
        Optional<UserEntity> userEntityOptional = userDao.get(userId);
        if (userEntityOptional.isPresent()) {
            return get(userEntityOptional.get(), dealId);
        } else {
            throw new NotFoundException(DealEntity.class, dealId);
        }
    }

    private DealEntity get(UserEntity owner, Long dealId) {
        Optional<DealEntity> dealEntityOptional = dealDao.get(owner, dealId);
        if (!dealEntityOptional.isPresent())
            throw new NotFoundException(DealEntity.class, dealId);
        return dealEntityOptional.get();
    }

    public DealEntity save(DealEntity deal) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();

        if (deal.isShared() && !currentUser.getAdmin())
            throw new UnauthorizedException("Only admins can create shared deals");

        deal.setId(null);
        deal.setOwner(currentUser);
        deal.setCreated(Clock.getNow());
        DealEntity savedDeal = dealDao.save(deal);

        assert savedDeal != null : "dealDao.save(" + deal + ") returned null";
        return savedDeal;
    }

    public void update(DealEntity deal) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();

        Optional<DealEntity> maybeDeal = dealDao.get(currentUser, deal.getId());
        if (!maybeDeal.isPresent())
            throw new NotFoundException(DealEntity.class, deal.getId());

        if (!currentUser.getId().equals(maybeDeal.get().getOwner().getId()))
            throw new UnauthorizedException("Can't update deal of other user");

        deal.setOwner(currentUser);
        deal.setCreated(Clock.getNow());
        dealDao.save(deal);
    }

    public void delete(Long dealId) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();

        Optional<DealEntity> maybeDeal = dealDao.get(currentUser, dealId);
        if (!maybeDeal.isPresent())
            throw new NotFoundException(DealEntity.class, dealId);

        if (!currentUser.getId().equals(maybeDeal.get().getOwner().getId()))
            throw new UnauthorizedException("Can't delete deal of other user");

        dealDao.deleteAsync(maybeDeal.get());
    }
}
