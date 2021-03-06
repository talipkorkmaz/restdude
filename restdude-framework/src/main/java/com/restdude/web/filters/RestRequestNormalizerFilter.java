/**
 *
 * Restdude
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.restdude.web.filters;

import com.restdude.util.Constants;
import com.restdude.util.CookieUtil;
import com.restdude.util.HttpUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

//@Component("restRequestNormalizerFilter")
public class RestRequestNormalizerFilter extends OncePerRequestFilter {
    private static final Logger LOGGER = LoggerFactory.getLogger(RestRequestNormalizerFilter.class);
    private static final String X_HTTP_METHOD_OVERRIDE = "X-HTTP-Method-Override";

    /** Default method parameter: {@code _method} */
    public static final String DEFAULT_METHOD_PARAM = "_method";

    private String methodParam = DEFAULT_METHOD_PARAM;

    /**
     * Set the parameter name to look for HTTP methods.
     *
     * @see #DEFAULT_METHOD_PARAM
     */
    public void setMethodParam(String methodParam) {
        Assert.hasText(methodParam, "'methodParam' must not be empty");
        this.methodParam = methodParam;
    }


    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        // set base URL
        HttpUtil.setBaseUrl(request);
        LOGGER.debug("doFilterInternal, request attribute Constants.BASE_URL_KEY: {}", request.getAttribute(Constants.BASE_URL_KEY));

        String requestMethodOverride = getMethodOverride(request);
        String authorizationHeader = request.getHeader(Constants.HEADER_AUTHORIZATION);
        // TODO: check if basic auth is enabled
        String cookieToken =
                org.apache.commons.lang3.StringUtils.isNotBlank(authorizationHeader)
                        ? null : CookieUtil.getCookieValue(request, Constants.BASIC_AUTHENTICATION_TOKEN_COOKIE_NAME);

        LOGGER.debug("doFilterInternal, requestMethodOverride: " + requestMethodOverride +
                ", authorizationHeader: " + authorizationHeader + ", cookieToken: " + cookieToken);
        //response.setContentType(JSON_UTF8);
        if (LOGGER.isDebugEnabled()) {
            String method = requestMethodOverride != null ? requestMethodOverride : request.getMethod();
            LOGGER.debug("doFilterInternal, method: " + method + ", pathFragment: " + request.getRequestURL() + ", contextPath: " + request.getContextPath() + ", method override: " + requestMethodOverride + ", authToken: " + cookieToken);
        }
        if (!StringUtils.isEmpty(requestMethodOverride) || !StringUtils.isEmpty(cookieToken)) {
            HttpServletRequest wrapper = new RestRequestNormalizerRequestWrapper(request, requestMethodOverride, cookieToken);
            filterChain.doFilter(wrapper, response);
        } else {
            filterChain.doFilter(request, response);
        }

    }


    protected String getMethodOverride(HttpServletRequest httpRequest) {
        String requestMethodOverride = httpRequest.getParameter(this.methodParam);
        if (StringUtils.isEmpty(requestMethodOverride)) {
            requestMethodOverride = httpRequest.getHeader(X_HTTP_METHOD_OVERRIDE);
            LOGGER.debug("HTTP Method Override from header: " + requestMethodOverride);
        } else {
            LOGGER.debug("HTTP Method Override from param: " + requestMethodOverride);
        }
        return requestMethodOverride;
    }

}
