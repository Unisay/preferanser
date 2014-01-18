package com.preferanser.client.restygwt;

import com.google.common.collect.Maps;
import com.google.gwt.core.ext.TreeLogger;
import com.google.gwt.core.ext.UnableToCompleteException;
import com.google.gwt.core.ext.typeinfo.JClassType;
import com.google.gwt.core.ext.typeinfo.JMethod;
import org.fusesource.restygwt.rebind.AnnotationResolver;

import java.util.HashMap;
import java.util.Map;

public class PreferanserAnnotationResolver implements AnnotationResolver {

    public static final String REQUEST_ID_KEY = "requestId";

    @Override
    public Map<String, String[]> resolveAnnotation(TreeLogger logger, JClassType source, JMethod method, String restMethod) throws UnableToCompleteException {
        com.preferanser.client.restygwt.RequestId requestIdAnnotation = method.getAnnotation(com.preferanser.client.restygwt.RequestId.class);
        if (requestIdAnnotation != null) {
            RequestIdValue requestId = requestIdAnnotation.value();
            HashMap<String, String[]> result = Maps.newHashMap();
            result.put(REQUEST_ID_KEY, new String[]{requestId.name()});
            return result;
        }
        return null;
    }

}
