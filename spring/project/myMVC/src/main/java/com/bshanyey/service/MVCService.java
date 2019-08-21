package com.bshanyey.service;

import com.bshanyey.annotations.Service;

@Service("mvcService")
public class MVCService {

    public void service(){
        System.out.println("service running");
    }
}
