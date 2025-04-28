package com.agent.agentforce.controller;

import com.agent.agentforce.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.Map;


@Controller
@RequestMapping(value="/home/*")
public class HomeController {

    @Autowired
    private HomeService homeService;

    @GetMapping("/home")
    public ModelAndView home(Model model) {
        ModelAndView mav = new ModelAndView();
        mav.addObject("title", "Welcome to Home Page");
        mav.addObject("description", "This is the description of the home page.");
        mav.setViewName("home/home");
        return mav;
    }

    @PostMapping("/agentInit")
    @ResponseBody
    public String agentInit() {
        return homeService.agentInit();
    }
    
    @PostMapping("/sentMessage")
	@ResponseBody
	public String sentMessage(@RequestBody Map<String, Object> payload) {
	    String message = (String) payload.get("message");
	    // → Salesforce Agentforce에 REST 요청
	    String agentResponse = homeService.sendMessage(message);
	    
	    return agentResponse;
	}
}
