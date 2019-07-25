package com.blink.ewb.jwt;

import java.io.Serializable;
import java.util.Date;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Service
public class JwtClient {

	public static final String HEADER_STRING = "Authorization";

	public static final String TOKEN_PREFIX = "Bearer ";

	public static final String PARAM_STRING = "token";

	/**
	 * 创建token
	 * 
	 * @param roomKey
	 * @param secret
	 * @param expiration
	 *            单位秒
	 * @return
	 */
	public TokenInfo createToken(Map<String, Object> claims, String secret, long expiration) {
		long expiration_msec = expiration * 1000;
		String token = this.generateJWT(claims, secret, expiration_msec);
		TokenInfo tokenInfo = new TokenInfo();
		tokenInfo.setToken(token);
		tokenInfo.setExpires_in(expiration);
		return tokenInfo;
	}

	/**
	 * 校验token
	 * 
	 * @param token
	 * @param secret
	 * @return
	 */
	public Map<String, Object> checkToken(String token, String secret) {
		return this.parseJWT(token, secret);
	}

	/**
	 * 校验token
	 * 
	 * @param request
	 * @param secret
	 * @return
	 */
	public Map<String, Object> checkToken(HttpServletRequest request, String secret) {
		// 从Header中拿到token
		String token = request.getHeader(HEADER_STRING);
		if (token != null && !"".equals(token)) {
			// 去掉 Bearer
			token = token.replace(TOKEN_PREFIX, "");
		} else {
			token = request.getParameter(PARAM_STRING);
		}
		if (token == null || "".equals(token)) {
			return null;
		}
		return this.checkToken(token, secret);
	}

	/**
	 * 生成token
	 * 
	 * @param claims
	 * @param key
	 * @param expirationMillis
	 * @return
	 */
	public String generateJWT(Map<String, Object> claims, String key, long expirationMillis) {
		return Jwts.builder().setClaims(claims).setExpiration(new Date(System.currentTimeMillis() + expirationMillis))
				.signWith(SignatureAlgorithm.HS512, key).compact();
	}

	/**
	 * 校验token
	 * 
	 * @param jsonWebToken
	 * @param key
	 * @return
	 */
	public Map<String, Object> parseJWT(String jsonWebToken, String key) {
		try {
			return Jwts.parser().setSigningKey(key).parseClaimsJws(jsonWebToken).getBody();
		} catch (Exception e) {
			return null;
		}
	}

	public class TokenInfo implements Serializable {

		private static final long serialVersionUID = 1L;

		private String token;

		/** 过期时间，单位秒 */
		private long expires_in;

		public String getToken() {
			return token;
		}

		public void setToken(String token) {
			this.token = token;
		}

		public long getExpires_in() {
			return expires_in;
		}

		public void setExpires_in(long expires_in) {
			this.expires_in = expires_in;
		}

	}

}
