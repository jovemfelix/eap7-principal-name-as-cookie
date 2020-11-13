package com.example.eap.handler;

import io.undertow.security.api.SecurityContext;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.server.handlers.CookieImpl;
import io.undertow.servlet.handlers.ServletRequestContext;
import io.undertow.servlet.spec.HttpServletRequestImpl;
import org.jboss.logging.Logger;

import javax.servlet.http.HttpSession;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.Principal;

/**
 * Create a Cookie with the information of the logged user
 */
public class LogUserHandler implements HttpHandler {
    private static final Logger logger = Logger.getLogger(LogUserHandler.class);

    private final HttpHandler next;
    final String COOKIE_NAME = "Username";
    final String CUSTOM_CONECTION_WITH_EXTERNAL_LIB = "MySpecialAtribute";

    public LogUserHandler(HttpHandler next) {
        this.next = next;
    }

    @Override
    public void handleRequest(final HttpServerExchange serverExchange) throws Exception {
        serverExchange.addResponseCommitListener(Exchange -> {

            String username = getPrincipalName(serverExchange.getSecurityContext());

            if (username == null) {
                username = extractUsernameFromSessionAtribute(serverExchange);
            }

            logger.debug("[handleRequest][username] = " + username);

            if (username != null) {
                serverExchange.setResponseCookie(new CookieImpl(COOKIE_NAME, username));
            }
        });

        next.handleRequest(serverExchange);
    }

    private String getPrincipalName(SecurityContext securityContext) {
        logger.trace("[handleRequest][securityContext] = " + securityContext);
        String username = null;
        if (securityContext != null) {
            boolean isAutenticated = securityContext.getAuthenticatedAccount() != null;
            logger.trace("[handleRequest][isAutenticated] = " + isAutenticated);
            if (isAutenticated) {
                Principal principal = securityContext.getAuthenticatedAccount().getPrincipal();
                username = principal == null ? "-" : principal.getName();
            }
        }

        return username;
    }

    private String extractUsernameFromSessionAtribute(HttpServerExchange serverExchange) {
        ServletRequestContext src = serverExchange.getAttachment(ServletRequestContext.ATTACHMENT_KEY);
        String username = null;

        if (src != null) {
            HttpServletRequestImpl request = src.getOriginalRequest();
            if (request != null) {
                HttpSession session = request.getSession(false);

                logger.trace("[handleRequest][extractUsernameFromSessionAtribute] session " + session);

                if (session != null) {
                    Object sessionContexaoMP = session.getAttribute(CUSTOM_CONECTION_WITH_EXTERNAL_LIB);
                    logger.trace("[handleRequest][CONEXAO_MP] sessionAttribute " + sessionContexaoMP);
                    if (sessionContexaoMP != null) {
                        try {
                            Method getUser = sessionContexaoMP.getClass().getMethod("getUser");
                            username = (String) getUser.invoke(sessionContexaoMP);
                        } catch (SecurityException e) {
                        } catch (NoSuchMethodException e) {
                        } catch (IllegalAccessException e) {
                        } catch (InvocationTargetException e) {
                        }
                    }
                }
            }
        }
        return username;
    }

}

