package com.mewebstudio.javaspringbootboilerplate.service;

import com.mewebstudio.javaspringbootboilerplate.security.Authorize;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Profile;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.core.MethodParameter;
import org.springframework.lang.NonNull;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
@Profile("!mvcIT")
public class InterceptorService implements HandlerInterceptor {
    private final AuthenticationService authenticationService;

    /**
     * Interception point before the execution of a handler.
     *
     * @param request  -- Request information for HTTP servlets.
     * @param response -- It is where the servlet can write information about the data it will send back.
     * @param handler  -- Class Object is the root of the class hierarchy.
     * @return -- true or false or AccessDeniedException
     */
    @Override
    public boolean preHandle(@NonNull final HttpServletRequest request,
                             @NonNull final HttpServletResponse response,
                             @NonNull final Object handler) {
        HandlerMethod handlerMethod;
        try {
            handlerMethod = (HandlerMethod) handler;
        } catch (ClassCastException e) {
            return true;
        }

        validateQueryParams(request, handlerMethod);
        Authorize authorizeAnnotation = getAuthorizeAnnotation(handlerMethod);
        if (authorizeAnnotation != null && !hasAnnotationRole(authorizeAnnotation)) {
            log.error("Throwing AccessDeniedException because role is not valid for api");
            throw new AccessDeniedException("You are not allowed to perform this operation");
        }

        return true;
    }

    /**
     * Validation of the request params to check unhandled ones.
     *
     * @param request -- Request information for HTTP servlets.
     * @param handler -- Encapsulates information about a handler method consisting of a method
     */
    private void validateQueryParams(final HttpServletRequest request, final HandlerMethod handler) {
        List<String> queryParams = Collections.list(request.getParameterNames());
        MethodParameter[] methodParameters = handler.getMethodParameters();
        List<String> expectedParams = new ArrayList<>(methodParameters.length);

        boolean hasModelAttribute = Arrays.stream(methodParameters)
            .anyMatch(methodParameter -> methodParameter.getParameterAnnotation(ModelAttribute.class) != null);

        for (MethodParameter methodParameter : methodParameters) {
            RequestParam requestParam = methodParameter.getParameterAnnotation(RequestParam.class);
            if (requestParam != null) {
                if (StringUtils.hasText(requestParam.name())) {
                    expectedParams.add(requestParam.name());
                } else {
                    methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
                    expectedParams.add(methodParameter.getParameterName());
                }
            }

            /* TODO: ModelAttribute params check
            ModelAttribute modelAttribute = methodParameter.getParameterAnnotation(ModelAttribute.class);
            if (modelAttribute != null) {
                methodParameter.initParameterNameDiscovery(new DefaultParameterNameDiscoverer());
                log.info("modelAttribute: {}", methodParameters[1].getParameter().getAnnotatedType().getType());
            }
            */
        }

        queryParams.removeAll(expectedParams);
        if (!queryParams.isEmpty() && !hasModelAttribute) {
            log.error("Unexpected parameters: {}", queryParams);
            throw new InvalidParameterException("unexpected parameter: " + queryParams);
        }
    }

    /**
     * Get infos for Authorize annotation that defined for class or method.
     *
     * @param handlerMethod -- RequestMapping method that reached to server
     * @return -- Authorize annotation or null
     */
    private Authorize getAuthorizeAnnotation(final HandlerMethod handlerMethod) {
        if (handlerMethod.getMethod().getDeclaringClass().isAnnotationPresent(Authorize.class)) {
            return handlerMethod.getMethod().getDeclaringClass().getAnnotation(Authorize.class);
        } else if (handlerMethod.getMethod().isAnnotationPresent(Authorize.class)) {
            return handlerMethod.getMethod().getAnnotation(Authorize.class);
        } else if (handlerMethod.getMethod().getClass().isAnnotationPresent(Authorize.class)) {
            return handlerMethod.getMethod().getClass().getAnnotation(Authorize.class);
        }

        return null;
    }

    /**
     * Checks the roles of user for defined Authorize annotation.
     *
     * @param authorize - parameter that has roles
     * @return -- false if not authorized
     * @throws BadCredentialsException -- throws BadCredentialsException
     * @throws AccessDeniedException   -- throws AccessDeniedException
     */
    private boolean hasAnnotationRole(final Authorize authorize) throws BadCredentialsException, AccessDeniedException {
        if (authenticationService.getPrincipal() == null) {
            log.error("You have to be authenticated to perform this operation");
            throw new BadCredentialsException("You have to be authenticated to perform this operation");
        }

        try {
            if (!authenticationService.isAuthorized(authorize.roles())) {
                log.error("Authorization is failed.");
                return false;
            }
        } catch (Exception ex) {
            log.trace("Exception occurred while authorizing. Ex: {}", ExceptionUtils.getStackTrace(ex));
            return false;
        }

        return true;
    }
}
