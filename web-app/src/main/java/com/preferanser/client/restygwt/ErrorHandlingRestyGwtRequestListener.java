package com.preferanser.client.restygwt;

import com.google.gwt.http.client.Request;
import com.google.gwt.http.client.Response;
import com.google.gwt.user.client.Window;
import com.google.inject.Inject;
import com.gwtplatform.mvp.client.proxy.PlaceManager;
import org.fusesource.restygwt.client.Method;

import java.util.logging.Logger;

public class ErrorHandlingRestyGwtRequestListener extends RestyGwtRequestListenerAdapter {

    private static final Logger log = Logger.getLogger("ErrorHandlingRestyGwtRequestListener");

    private PlaceManager placeManager;

    @Inject
    public ErrorHandlingRestyGwtRequestListener(PlaceManager placeManager) {
        this.placeManager = placeManager;
    }

    @Override public void afterClientErrorHandled(Method method, Request request, Response response) {
        String errorMessage;
        switch (response.getStatusCode()) {
            case 0:
                return; // Ignore
            case 401: // Unauthorized
                placeManager.revealDefaultPlace();
                errorMessage = "Для просмотра данной страницы необходимо войти в систему.";
                break;
            case 404: // Not found
                errorMessage = "Объект не найден.";
                break;
            default:
                errorMessage = "HTTP response error: " + response.getText();
                Window.alert("Обработка запроса завершилась ошибкой. " +
                    "Подробная информация об ошибке доступна в консоли браузера. " +
                    "Пожалуйста, обновите текущую страницу или начните с главной.");
        }

        log.severe(errorMessage);
    }

}
