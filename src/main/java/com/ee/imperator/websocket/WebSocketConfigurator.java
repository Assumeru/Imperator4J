package com.ee.imperator.websocket;

import java.net.URI;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.websocket.HandshakeResponse;
import javax.websocket.server.HandshakeRequest;
import javax.websocket.server.ServerEndpointConfig;
import javax.websocket.server.ServerEndpointConfig.Configurator;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;

import org.ee.web.request.Request;

import com.ee.imperator.Imperator;

public class WebSocketConfigurator extends Configurator {
	@Override
	public void modifyHandshake(ServerEndpointConfig sec, HandshakeRequest request, HandshakeResponse response) {
		Map<String, Cookie> cookies = getCookies(request.getHeaders());
		sec.getUserProperties().put(Request.class.getName(),
				new Request(Imperator.getContext(),
						new WebSocketServletRequest(request, cookies),
						cookies, getGetParams(request.getParameterMap()),
						null, getPath(request.getRequestURI())));
	}

	private Map<String, Cookie> getCookies(Map<String, List<String>> headers) {
		Map<String, Cookie> cookies = new HashMap<>();
		List<String> header = headers.get("Cookie");
		if(header != null) {
			for(String value : header) {
				Cookie cookie = Cookie.valueOf(value);
				cookies.put(cookie.getName(), cookie);
			}
		}
		return Collections.unmodifiableMap(cookies);
	}

	private MultivaluedMap<String, String> getGetParams(Map<String, List<String>> parameterMap) {
		MultivaluedMap<String, String> out = new MultivaluedHashMap<>();
		out.putAll(parameterMap);
		return out;
	}

	private String getPath(URI requestURI) {
		try {
			return requestURI.toString().substring(Imperator.getContext().getContextPath().length() + 1);
		} catch(StringIndexOutOfBoundsException e) {
			return "";
		}
	}
}
