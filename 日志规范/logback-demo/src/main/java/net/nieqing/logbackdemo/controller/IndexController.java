package net.nieqing.logbackdemo.controller;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import net.nieqing.logbackdemo.vo.ResponseVo;

@RestController
@CrossOrigin
public class IndexController {

	@RequestMapping(value = "/index")
	public ResponseVo index() {
		return ResponseVo.success();
	}

}
