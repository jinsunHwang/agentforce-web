package com.agent.agentforce.controller;

import com.agent.agentforce.service.HomeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;


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
}
