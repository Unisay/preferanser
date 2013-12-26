package com.preferanser.server.service.impl;

import com.googlecode.objectify.ObjectifyService;
import com.preferanser.server.business.Deal;
import com.preferanser.server.service.DealService;
import com.preferanser.server.service.PreferanserUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.googlecode.objectify.ObjectifyService.ofy;

@Service("dealService")
public class ObjectifyDealService implements DealService {

    @Autowired
    private PreferanserUserService userService;

    static {
        ObjectifyService.register(Deal.class);
    }

    @Override public void persist(Deal deal) {
        deal.setUserId(userService.getCurrentUserId());
        ofy().save().entity(deal).now();
    }

}
