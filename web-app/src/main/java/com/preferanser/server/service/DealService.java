package com.preferanser.server.service;

import com.google.common.base.Optional;
import com.google.inject.Inject;
import com.google.inject.Provider;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.exception.NoAuthenticatedUserException;
import com.preferanser.server.exception.NotAuthorizedUserException;
import com.preferanser.shared.domain.entity.Deal;
import com.preferanser.shared.domain.entity.User;
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

    public void importSharedDeals(User user) {
        Set<Deal> importedDeals = newHashSet();
        for (Deal sharedDeal : dealDao.getSharedDeals()) {
            if (!user.getId().equals(sharedDeal.getOwner().getName())) {
                importedDeals.add(importDeal(sharedDeal, user));
            }
        }
        dealDao.save(importedDeals);
    }

    private Deal importDeal(Deal sharedDeal, User user) {
        Deal deal = new Deal(sharedDeal);
        deal.setId(null);
        deal.setShared(false);
        deal.setOwner(user.key());
        return deal;
    }

    public List<Deal> getCurrentUserOrSharedDeals() {
        Optional<User> userOptional = authenticationServiceProvider.get().getCurrentUser();
        if (userOptional.isPresent()) {
            return dealDao.getUserDeals(userOptional.get());
        } else {
            return dealDao.getSharedDeals();
        }
    }

    public Deal get(Long dealId) {
        return dealDao.get(dealId);
    }

    public Deal save(Deal deal) {
        Optional<User> currentUserOptional = authenticationServiceProvider.get().getCurrentUser();

        if (!currentUserOptional.isPresent())
            throw new NoAuthenticatedUserException();

        if (deal.isShared() && !currentUserOptional.get().getAdmin())
            throw new NotAuthorizedUserException("Only admins can create shared deals");

        deal.setId(null);
        deal.setOwner(currentUserOptional.get());
        deal.setCreated(Clock.getNow());
        Deal savedDeal = dealDao.save(deal);

        assert savedDeal != null : "dealDao.save(" + deal + ") returned null";
        return savedDeal;
    }

    public void update(Deal deal) {
        Optional<User> currentUserOptional = authenticationServiceProvider.get().getCurrentUser();

        if (!currentUserOptional.isPresent())
            throw new NoAuthenticatedUserException();

        User currentUser = currentUserOptional.get();

        Deal existingDeal = dealDao.get(deal.getId());
        if (!existingDeal.getOwner().getName().equals(currentUser.getId()))
            throw new NotAuthorizedUserException();

        deal.setOwner(currentUser);
        deal.setCreated(Clock.getNow());
        dealDao.save(deal);
    }

    public void delete(Long dealId) {
        Optional<User> currentUserOptional = authenticationServiceProvider.get().getCurrentUser();

        if (!currentUserOptional.isPresent())
            throw new NoAuthenticatedUserException(); // TODO force HTTP status

        User currentUser = currentUserOptional.get();
        Deal deal = dealDao.get(dealId);
        if (!deal.getOwner().getName().equals(currentUser.getId()))
            throw new NotAuthorizedUserException();

        dealDao.delete(deal);
    }

}
