/*
 * Copyright (C) 2007-2013 Crafter Software Corporation.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.craftercms.security.authentication.impl;

import org.craftercms.commons.http.RequestContext;
import org.craftercms.security.authentication.Authentication;
import org.craftercms.security.authentication.LoginSuccessHandler;
import org.craftercms.security.exception.SecurityProviderException;
import org.craftercms.security.utils.handlers.HandlerBase;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.web.savedrequest.HttpSessionRequestCache;
import org.springframework.security.web.savedrequest.RequestCache;
import org.springframework.security.web.savedrequest.SavedRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Default implementation of {@link LoginSuccessHandler}:
 * <p/>
 * <ol>
 * <li>Deletes any authentication exception saved in the session.</li>
 * <li>Adds the ticket ID and profile last modified cookies to response.</li>
 * <li>Uses the Spring {@link RequestCache} to obtain the previous request before login and redirect to it.</li>
 * </ol>
 *
 * @author Alfonso Vásquez
 */
public class LoginSuccessHandlerImpl extends HandlerBase implements LoginSuccessHandler {

    protected RequestCache requestCache;
    protected String defaultTargetUrl;

    public LoginSuccessHandlerImpl() {
        super();
        requestCache = new HttpSessionRequestCache();
    }

    public void setRequestCache(RequestCache requestCache) {
        this.requestCache = requestCache;
    }

    @Required
    public void setDefaultTargetUrl(String defaultTargetUrl) {
        this.defaultTargetUrl = defaultTargetUrl;
    }

    @Override
    public void handle(RequestContext context, Authentication authentication) throws SecurityProviderException,
            IOException {
        redirectToSavedRequest(context.getRequest(), context.getResponse());
    }

    protected void redirectToSavedRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
        SavedRequest savedRequest = requestCache.getRequest(request, response);
        if (savedRequest != null) {
            redirectToUrl(request, response, savedRequest.getRedirectUrl());
        } else {
            redirectToUrl(request, response, defaultTargetUrl);
        }
    }

}
