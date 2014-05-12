package com.preferanser.server.service;

import com.google.common.base.Optional;
import com.google.inject.Provider;
import com.preferanser.server.dao.DealDao;
import com.preferanser.server.dao.DrawingDao;
import com.preferanser.server.entity.DealEntity;
import com.preferanser.server.entity.DrawingEntity;
import com.preferanser.server.entity.UserEntity;
import com.preferanser.shared.domain.Card;
import com.preferanser.shared.util.Clock;
import com.preferanser.testng.ClockTestNGListener;
import com.preferanser.testng.DatastoreTestNGListener;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;

import static org.mockito.Mockito.*;
import static org.testng.collections.Lists.newArrayList;
import static org.unitils.reflectionassert.ReflectionAssert.assertReflectionEquals;

@Listeners({ClockTestNGListener.class, DatastoreTestNGListener.class})
public class DrawingServiceTest {

    private DrawingService drawingService;

    @Mock
    private DrawingDao drawingDao;

    @Mock
    private DealDao dealDao;

    @Mock
    private AuthenticationService authenticationService;

    private UserEntity user;
    private UserEntity otherUser;

    @BeforeMethod
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        drawingService = new DrawingService(drawingDao, dealDao, new Provider<AuthenticationService>() {
            @Override public AuthenticationService get() {
                return authenticationService;
            }
        });
        user = new UserEntity();
        user.setId(123L);
        otherUser = new UserEntity();
        otherUser.setId(234L);
    }

    @Test
    public void testSave() throws Exception {
        DrawingEntity unsavedDrawing = buildDrawingEntity(123L); // This id must be overridden
        DrawingEntity expectedUnsavedDrawing = buildDrawingEntity(null);
        DrawingEntity expectedSavedDrawing = buildDrawingEntity(1L);

        when(authenticationService.getCurrentUserOrThrow()).thenReturn(user);
        when(dealDao.get(unsavedDrawing.getDeal())).thenReturn(Optional.of(buildDealEntity()));
        when(drawingDao.save(expectedUnsavedDrawing)).thenReturn(expectedSavedDrawing);

        DrawingEntity savedDrawing = drawingService.save(unsavedDrawing);

        verify(authenticationService).getCurrentUserOrThrow();
        verifyNoMoreInteractions(authenticationService);

        verify(dealDao).get(unsavedDrawing.getDeal());
        verifyNoMoreInteractions(dealDao);

        verify(drawingDao).save(expectedUnsavedDrawing);
        verifyNoMoreInteractions(drawingDao);

        assertReflectionEquals(expectedSavedDrawing, savedDrawing);
    }

    private DealEntity buildDealEntity() {
        DealEntity deal = new DealEntity();
        deal.setId(1000L);
        deal.setOwner(user);
        return deal;
    }

    private DrawingEntity buildDrawingEntity(Long id) {
        DrawingEntity entity = new DrawingEntity();
        entity.setId(id);
        entity.setName("name");
        entity.setDescription("desc");
        entity.setCreated(Clock.getNow());
        entity.setDeal(user.getId(), buildDealEntity().getId());
        entity.setTurns(newArrayList(Card.values()));
        return entity;
    }

}