package net.nieqing.logbackdemo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import lombok.extern.slf4j.Slf4j;
import net.nieqing.logbackdemo.vo.ResponseVo;

@Slf4j
@RestController
@CrossOrigin
public class IndexController {

	@RequestMapping(value = "/index")
	public ResponseVo index() {
		log.info("[info] visit index");
		log.debug("[debug] visit index");
		log.error("[error] visit index");
		log.warn("[warn] visit index");
		return ResponseVo.success();
	}

}
