package com.oo.tools.spring.boot.support;


import com.oo.tools.spring.boot.BusinessException;
import com.oo.tools.spring.boot.CheckRequestException;
import com.oo.tools.spring.boot.annotation.CheckRequest;
import jakarta.annotation.Nullable;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @author <a href="mailto:yu.ou@alpha-ess.com">ouyu</a>
 * @date: 2024/02/22 13:39:44
 */
public class CheckRequestHandle {

    public static Object checkRequestHandler(Object result) {
        if (result != null) {
            passTheInterfaceVerificationMethod(result);

            passTheAnnotationVerificationMethod(result);

        }
        return result;
    }

    @Nullable
    private static void passTheAnnotationVerificationMethod(Object result) {
        CheckRequest checkRequest = AnnotationUtils.getAnnotation(result.getClass(), CheckRequest.class);

        if (checkRequest == null) {
            return;
        }
        String checkMethod = checkRequest.checkMethod();
        Method checkRequestMethod;
        try {
            checkRequestMethod = result.getClass().getMethod(checkMethod);
        } catch (NoSuchMethodException | SecurityException e) {
            throw new BusinessException(e);
        }
        try {
            checkRequestMethod.invoke(result);
        } catch (CheckRequestException | IllegalAccessException | IllegalArgumentException |
                 InvocationTargetException e) {
            throw new BusinessException(e.getCause().getMessage());
        }
    }

    private static void passTheInterfaceVerificationMethod(Object result) {
        if (result instanceof com.oo.tools.spring.boot.CheckRequest checkRequest) {
            try {
                checkRequest.checkRequest();
            } catch (CheckRequestException e) {
                throw new BusinessException(e.getMessage());
            }
        }
    }
}