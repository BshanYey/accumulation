package com.bshanyey.controller;

import com.bshanyey.annotations.Autowired;
import com.bshanyey.annotations.Controller;
import com.bshanyey.annotations.Qualifier;
import com.bshanyey.annotations.RequestMapping;
import com.bshanyey.service.MVCService;

@Controller
@RequestMapping(value = "/mvc")
public class MVCController {

    @Autowired
    @Qualifier(value = "mvcService")
    private MVCService service;


    @RequestMapping("/go")
    public String mvcController(){
        service.service();
        return "index";
    }
}
