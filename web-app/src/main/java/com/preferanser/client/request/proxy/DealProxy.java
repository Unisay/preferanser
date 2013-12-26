package com.preferanser.client.request.proxy;

import com.google.web.bindery.requestfactory.shared.EntityProxy;
import com.google.web.bindery.requestfactory.shared.ProxyFor;
import com.preferanser.domain.Card;
import com.preferanser.domain.Cardinal;
import com.preferanser.domain.Contract;
import com.preferanser.server.business.Deal;
import com.preferanser.server.service.ObjectifyLocator;

import java.util.Date;
import java.util.List;

@ProxyFor(value = Deal.class, locator = ObjectifyLocator.class)
public interface DealProxy extends EntityProxy {

    public String getName();

    public Date getCreated();

    public Contract getNorthContract();

    public Contract getEastContract();

    public Contract getSouthContract();

    public Contract getWestContract();

    public List<Card> getNorthCards();

    public List<Card> getEastCards();

    public List<Card> getSouthCards();

    public List<Card> getWestCards();

    public Card getCenterNorthCard();

    public Card getCenterEastCard();

    public Card getCenterSouthCard();

    public Card getCenterWestCard();

    public Cardinal getFirstTurn();

    public void setName(String name);

    public void setCreated(Date created);

    public void setNorthContract(Contract northContract);

    public void setEastContract(Contract eastContract);

    public void setSouthContract(Contract southContract);

    public void setWestContract(Contract westContract);

    public void setNorthCards(List<Card> northCards);

    public void setEastCards(List<Card> eastCards);

    public void setSouthCards(List<Card> southCards);

    public void setWestCards(List<Card> westCards);

    public void setCenterNorthCard(Card centerNorthCard);

    public void setCenterEastCard(Card centerEastCard);

    public void setCenterSouthCard(Card centerSouthCard);

    public void setCenterWestCard(Card centerWestCard);

    public void setFirstTurn(Cardinal firstTurn);

}
