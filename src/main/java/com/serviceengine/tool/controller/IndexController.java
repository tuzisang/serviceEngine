package com.serviceengine.tool.controller;

import com.serviceengine.tool.moduel.Param;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

/**
 * @author jianwei
 * @create 2022-11-14 22:25
 */
@Controller
public class IndexController {
    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index";
    }

    @PostMapping("/test")
    @ResponseBody
    public String test(@RequestBody Param param){

        return param.getName();
    }
}
