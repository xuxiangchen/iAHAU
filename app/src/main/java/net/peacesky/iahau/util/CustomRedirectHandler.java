package net.peacesky.iahau.util;

import android.util.Log;

import java.net.URI;
import java.net.URISyntaxException;

import cz.msebera.android.httpclient.Header;
import cz.msebera.android.httpclient.HttpHost;
import cz.msebera.android.httpclient.HttpRequest;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.HttpStatus;
import cz.msebera.android.httpclient.ProtocolException;
import cz.msebera.android.httpclient.client.CircularRedirectException;
import cz.msebera.android.httpclient.client.params.ClientPNames;
import cz.msebera.android.httpclient.client.utils.URIUtils;
import cz.msebera.android.httpclient.impl.client.DefaultRedirectHandler;
import cz.msebera.android.httpclient.impl.client.RedirectLocations;
import cz.msebera.android.httpclient.params.HttpParams;
import cz.msebera.android.httpclient.protocol.ExecutionContext;
import cz.msebera.android.httpclient.protocol.HttpContext;

/**
 * Created by SuooL on 15/9/26.
 */
public class CustomRedirectHandler extends DefaultRedirectHandler {

    private static final String REDIRECT_LOCATIONS = "http.protocol.redirect-locations";
    private final boolean enableRedirects;

    public CustomRedirectHandler(final boolean allowRedirects) {
        super();
        this.enableRedirects = allowRedirects;
    }

    @Override
    public boolean isRedirectRequested(
            final HttpResponse response,
            final HttpContext context) {
        if (!enableRedirects) {
            return false;
        }
        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        int statusCode = response.getStatusLine().getStatusCode();
        switch (statusCode) {
            // 重定向
            case HttpStatus.SC_MOVED_TEMPORARILY:
            case HttpStatus.SC_MOVED_PERMANENTLY:
            case HttpStatus.SC_SEE_OTHER:
            case HttpStatus.SC_TEMPORARY_REDIRECT:
                return true;
            default:
                return false;
        } //end of switch
    }

    @Override
    public URI getLocationURI(final HttpResponse response, final HttpContext context) throws ProtocolException {

        if (response == null) {
            throw new IllegalArgumentException("HTTP response may not be null");
        }
        //get the location header to find out where to redirect to
        Header locationHeader = response.getFirstHeader("location");
        Log.d(" Location", locationHeader.toString());
        if (locationHeader == null) {
            // got a redirect response, but no location header
            throw new ProtocolException(
                    "Received redirect response " + response.getStatusLine()
                            + " but no location header"
            );
        }
        //HERE IS THE MODIFIED LINE OF CODE
        String location = locationHeader.getValue().replaceAll(" ", "%20");
        Log.d("Re", location);
        URI uri;
        try {
            uri = new URI(location);
        } catch (URISyntaxException ex) {
            throw new ProtocolException("Invalid redirect URI: " + location, ex);
        }

        HttpParams params = response.getParams();
        // rfc2616 demands the location value be a complete URI
        // Location       = "Location" ":" absoluteURI
        if (!uri.isAbsolute()) {
            if (params.isParameterTrue(ClientPNames.REJECT_RELATIVE_REDIRECT)) {
                throw new ProtocolException("Relative redirect location '"
                        + uri + "' not allowed");
            }
            // Adjust location URI
            HttpHost target = (HttpHost) context.getAttribute(
                    ExecutionContext.HTTP_TARGET_HOST);
            if (target == null) {
                throw new IllegalStateException("Target host not available " +
                        "in the HTTP context");
            }

            HttpRequest request = (HttpRequest) context.getAttribute(
                    ExecutionContext.HTTP_REQUEST);

            try {
                URI requestURI = new URI(request.getRequestLine().getUri());
                URI absoluteRequestURI = URIUtils.rewriteURI(requestURI, target, true);
                uri = URIUtils.resolve(absoluteRequestURI, uri);
            } catch (URISyntaxException ex) {
                throw new ProtocolException(ex.getMessage(), ex);
            }
        }

        if (params.isParameterFalse(ClientPNames.ALLOW_CIRCULAR_REDIRECTS)) {

            RedirectLocations redirectLocations = (RedirectLocations) context.getAttribute(
                    REDIRECT_LOCATIONS);

            if (redirectLocations == null) {
                redirectLocations = new RedirectLocations();
                context.setAttribute(REDIRECT_LOCATIONS, redirectLocations);
            }

            URI redirectURI;
            if (uri.getFragment() != null) {
                try {
                    HttpHost target = new HttpHost(
                            uri.getHost(),
                            uri.getPort(),
                            uri.getScheme());
                    redirectURI = URIUtils.rewriteURI(uri, target, true);
                } catch (URISyntaxException ex) {
                    throw new ProtocolException(ex.getMessage(), ex);
                }
            } else {
                redirectURI = uri;
            }

            if (redirectLocations.contains(redirectURI)) {
                throw new CircularRedirectException("Circular redirect to '" +
                        redirectURI + "'");
            } else {
                redirectLocations.add(redirectURI);
            }
        }

        // HttpUtil.setURL_JWXTRE(uri.toString());
        HttpUtil.setURL_JWXTRE("http://zf.ahau.edu.cn/default_zzjk.aspx");
        Log.d(" I am redirect to:", uri.toString());
        return uri;
    }
}
