package com.blink.ewb.controller;

import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.blink.ewb.Config;
import com.blink.ewb.entity.Room;
import com.blink.ewb.jwt.JwtClient;
import com.blink.ewb.jwt.JwtClient.TokenInfo;
import com.blink.ewb.response.EwbResponse;
import com.blink.ewb.service.RoomService;

@Controller
@RequestMapping("/ewb/room")
public class RoomController {

	private static final Logger logger = LoggerFactory.getLogger(RoomController.class);

	@Autowired
	private RoomService roomService;

	@Autowired
	private JwtClient jwtClient;

	/**
	 * 创建room
	 * 
	 * @param appId
	 * @param roomNr
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/create", method = RequestMethod.POST)
	public EwbResponse createRoom(@RequestParam(name = "appId", required = true) String appId,
			@RequestParam(name = "roomNr", required = true) String roomNr, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			Room room = this.roomService.getRoom(appId, roomNr);
			if (room == null) {
				room = this.roomService.createRoom(appId, roomNr);
			}
			String roomKey = room.getRoomKey();
			String url = Config.web_url + "?roomKey=" + URLEncoder.encode(roomKey, "utf-8");
			if (Config.token_enable) {
				Map<String, Object> claims = new HashMap<String, Object>();
				claims.put("roomKey", roomKey);
				TokenInfo tokenInfo = this.jwtClient.createToken(claims, Config.token_secret,
						Config.token_expiration * 60);
				url += "&token=" + tokenInfo.getToken();
			}
			return new EwbResponse(200, "OK", url);
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

	/**
	 * 销毁room
	 * 
	 * @param appId
	 * @param roomNr
	 * @param request
	 * @param response
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value = "/destroy", method = RequestMethod.POST)
	public EwbResponse destroyRoom(@RequestParam(name = "appId", required = true) String appId,
			@RequestParam(name = "roomNr", required = true) String roomNr, HttpServletRequest request,
			HttpServletResponse response) {
		try {
			this.roomService.destroyRoom(appId, roomNr);

			return new EwbResponse(200, "OK", "");
		} catch (Exception e) {
			logger.error("error:", e);
			return new EwbResponse(500, "Server Error", "");
		}
	}

}
