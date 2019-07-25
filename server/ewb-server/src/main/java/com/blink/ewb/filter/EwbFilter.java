package com.blink.ewb.filter;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

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
import com.blink.ewb.response.EwbResponse;
import com.google.gson.Gson;

@WebFilter(filterName = "ewbFilter", urlPatterns = "/ewb/*")
public class EwbFilter implements Filter {

	/** 不过滤的url */
	private static Set<String> allowedPaths = new HashSet<String>();

	static {
		/** 创建room */
		allowedPaths.add("/ewb/room/create");
		/** 销毁room */
		allowedPaths.add("/ewb/room/destroy");
		/** 清理数据 */
		allowedPaths.add("/ewb/data/clean");
	}

	@Autowired
	private JwtClient jwtClient;

	/**
	 * 判断是否是不过滤的url
	 * 
	 * @param path
	 * @return
	 */
	private boolean isAllowedPath(String path) {
		return allowedPaths.contains(path);
	}

	@Override
	public void init(FilterConfig config) throws ServletException {

	}

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		// 设置跨域
		this.addAllowResHeader(response);
		if (!Config.token_enable) {
			String roomKey = request.getParameter("roomKey");
			if (roomKey != null) {
				request.setAttribute("roomKey", roomKey);
			}
			chain.doFilter(request, response);
			return;
		}
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		// 判断是否过滤
		if (this.isAllowedPath(httpRequest.getRequestURI())) { // 不过滤
			chain.doFilter(request, response);
			return;
		}
		// 预检options时，return
		if ("options".equalsIgnoreCase(httpRequest.getMethod())) {
			return;
		}
		// 校验token
		Map<String, Object> claims = this.jwtClient.checkToken(httpRequest, Config.token_secret);
		if (claims != null) {
			request.setAttribute("roomKey", claims.get("roomKey"));
			chain.doFilter(request, response);
			return;
		}
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		httpResponse.setCharacterEncoding("UTF-8");
		httpResponse.setContentType("application/json; charset=utf-8");
		httpResponse.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		EwbResponse result = new EwbResponse(401, "token校验失败", "");
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
