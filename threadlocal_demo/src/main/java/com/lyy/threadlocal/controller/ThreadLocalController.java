package com.lyy.threadlocal.controller;

import com.lyy.threadlocal.config.RequestHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
@RequestMapping("/thredLocal")
public class ThreadLocalController {

    @RequestMapping("test")
    @ResponseBody
    public Long test(){
        return RequestHolder.getId();
    }

}
