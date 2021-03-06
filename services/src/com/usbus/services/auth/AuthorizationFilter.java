package com.usbus.services.auth;

import com.usbus.commons.auxiliaryClasses.Utils;
import com.usbus.commons.enums.Rol;
import com.usbus.commons.exceptions.AuthException;
import org.jose4j.jwt.consumer.JwtConsumer;
import org.jose4j.jwt.consumer.JwtConsumerBuilder;
import org.jose4j.jwt.consumer.JwtContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.annotation.Priority;
import javax.ws.rs.NotAuthorizedException;
import javax.ws.rs.Priorities;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;
import javax.ws.rs.ext.Provider;
import java.io.IOException;
import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jpmartinez on 31/05/16.
 */
@Secured
@Provider
@Priority(Priorities.AUTHORIZATION)
public class AuthorizationFilter implements ContainerRequestFilter {
    static Logger logger = LoggerFactory.getLogger(AuthorizationFilter.class);

    @Context
    private ResourceInfo resourceInfo;

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {

        String authorizationHeader =
                requestContext.getHeaderString(HttpHeaders.AUTHORIZATION);
        // Check if the HTTP Authorization header is present and formatted correctly
        if (authorizationHeader == null || !authorizationHeader.startsWith("Bearer ")) {
            throw new NotAuthorizedException("Authorization header must be provided");
        }

        // Extract the token from the HTTP Authorization header
        String token = authorizationHeader.substring("Bearer".length()).trim();

        // Get the resource class which matches with the requested URL
        // Extract the roles declared by it
        Class<?> resourceClass = resourceInfo.getResourceClass();
        List<Rol> classRoles = extractRoles(resourceClass);

        // Get the resource method which matches with the requested URL
        // Extract the roles declared by it
        Method resourceMethod = resourceInfo.getResourceMethod();
        List<Rol> methodRoles = extractRoles(resourceMethod);

        try {

            // Check if the user is allowed to execute the method
            // The method annotations override the class annotations
            if (methodRoles.isEmpty()) {
                checkPermissions(classRoles, token);
            } else {
                checkPermissions(methodRoles, token);
            }

        } catch (AuthException e) {
            logger.info("El usuario no tiene el rol necesario para acceder a este servicio.");
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        } catch (Exception e) {
            logger.error("Error", e);
            requestContext.abortWith(Response.status(Response.Status.FORBIDDEN).build());
        }
    }

    // Extract the roles from the annotated element
    private List<Rol> extractRoles(AnnotatedElement annotatedElement) {
        if (annotatedElement == null) {
            return new ArrayList<Rol>();
        } else {
            Secured secured;
            secured = annotatedElement.getAnnotation(Secured.class);
            if (secured == null) {
                return new ArrayList<Rol>();
            } else {
                Rol[] allowedRoles = secured.value();
                return Arrays.asList(allowedRoles);
            }
        }
    }

    private void checkPermissions(List<Rol> allowedRoles, String token) throws Exception, AuthException {

        JwtConsumer firstPassJwtConsumer = new JwtConsumerBuilder()
                .setSkipAllValidators()
                .setDisableRequireSignature()
                .setSkipSignatureVerification()
                .build();

        logger.debug("Roles permitidos " + allowedRoles.toString());


        JwtContext jwtContext = firstPassJwtConsumer.process(token);

        List<String> rolesString = jwtContext.getJwtClaims().getStringListClaimValue("roles");

        List<Rol> roles = Utils.enumListFromStringList(Rol.class, rolesString);


        logger.debug("Roles recibidos" + roles.toString());
        logger.debug("Roles recibidos string" + rolesString.toString());

        boolean ok = false;
        for (Rol rol : allowedRoles) {
            if (roles.contains(rol)) {
                ok = true;
            }
        }
        if (!ok) {
            String error = "El usuario no tiene el rol especificado \n[RECIBIDOS] " + rolesString.toString() + "\n[PERMITIDOS] " + allowedRoles.toString();
            throw new AuthException(error);
        }

    }
}