package com.preferanser.server.service;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.server.exception.EntityNotFoundException;
import com.preferanser.server.exception.NotAuthorizedUserException;
import com.preferanser.shared.util.Clock;

import java.util.List;
import java.util.Set;

import static com.google.common.collect.Sets.newHashSet;

public class DealService {

    private final DealDao dealDao;
    private final Provider<AuthenticationService> authenticationServiceProvider;

    @Inject
    public DealService(DealDao dealDao, Provider<AuthenticationService> authenticationServiceProvider) {
        this.dealDao = dealDao;
        this.authenticationServiceProvider = authenticationServiceProvider;
    }

    public void importSharedDeals(UserEntity user) {
        Set<DealEntity> importedDeals = newHashSet();
        for (DealEntity sharedDeal : dealDao.getSharedDeals()) {
            if (!user.getId().equals(sharedDeal.getOwner().getName())) {
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

        Optional<DealEntity> dealEntityOptional = dealDao.get(currentUser, dealId);
        if (!dealEntityOptional.isPresent())
            throw new EntityNotFoundException();
        return dealEntityOptional.get();
    }

    public DealEntity save(DealEntity deal) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();

        if (deal.isShared() && !currentUser.getAdmin())
            throw new NotAuthorizedUserException("Only admins can create shared deals");

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
            throw new EntityNotFoundException();

        if (!maybeDeal.get().getOwner().getName().equals(currentUser.getId()))
            throw new NotAuthorizedUserException();

        deal.setOwner(currentUser);
        deal.setCreated(Clock.getNow());
        dealDao.save(deal);
    }

    public void delete(Long dealId) {
        UserEntity currentUser = authenticationServiceProvider.get().getCurrentUserOrThrow();

        Optional<DealEntity> maybeDeal = dealDao.get(currentUser, dealId);
        if (!maybeDeal.isPresent())
            throw new EntityNotFoundException();

        if (!maybeDeal.get().getOwner().getName().equals(currentUser.getId()))
            throw new NotAuthorizedUserException();

        dealDao.deleteAsync(maybeDeal.get());
    }

}
