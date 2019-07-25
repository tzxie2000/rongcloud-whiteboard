package com.blink.ewb.filter;

import java.io.IOException;
import java.util.Map;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;

import com.blink.ewb.Config;
import com.blink.ewb.jwt.JwtClient;
import com.blink.ewb.response.FileResponse;
import com.google.gson.Gson;

@WebFilter(filterName = "ewbFileFilter", urlPatterns = "/ewbfile/upload")
public class EwbFileFilter implements Filter {

	@Autowired
	private JwtClient jwtClient;

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 设置跨域
		this.addAllowResHeader(response);
		if (!Config.token_enable) {
			chain.doFilter(request, response);
			return;
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		// 预检options时，return
		if ("options".equalsIgnoreCase(httpRequest.getMethod())) {
			return;
		}
		// 校验token
		Map<String, Object> claims = this.jwtClient.checkToken(httpRequest, Config.token_secret);
		if (claims != null) {
			chain.doFilter(request, response);
			return;
		}
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json; charset=utf-8");
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		FileResponse result = new FileResponse(401, "token校验失败", "");
		Gson gson = new Gson();
		httpResponse.getWriter().write(gson.toJson(result));
	}

	@Override
	public void destroy() {

	}

	/**
	 * 设置header头支持跨域访问
	 * 
	 * @param response
	 */
	private void addAllowResHeader(ServletResponse response) {
		((HttpServletResponse) response).addHeader("Access-Control-Allow-Origin", "*");
		((HttpServletResponse) response).addHeader("Access-Control-Allow-Methods", "*");
		((HttpServletResponse) response).addHeader("Access-Control-Allow-Headers",
				"Authentication, Origin, X-Requested-With, Content-Type, Accept, Authorization");
	}

}
