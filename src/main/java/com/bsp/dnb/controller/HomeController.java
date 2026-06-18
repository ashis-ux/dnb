package com.bsp.dnb.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @GetMapping("/")
    public String home() {

        return "home";
    }
    
    @GetMapping("/dnb/create")
    public String createDnb() {

        return "dnb-create";
    }
    
    @GetMapping("/dnb/edit")
    public String editDnb() {

        return "dnb-edit";
    }
    
    @GetMapping("/attendance")
    public String attendance() {
        return "attendance";
    }
    
    @GetMapping("/dnb-adjustment")
    public String dnb_adjustment() {
        return "dnb-adjustment";
    }
    
    @GetMapping("/dnb-master-report")
    public String dnb_master_report() {
        return "dnb-master-report";
    }
    
    @GetMapping("/attendance-report")
    public String dnb_attendance_report() {
        return "attendance-report";
    }
    
    @GetMapping("/adjustment-report")
    public String dnb_adjustment_report() {
        return "adjustment-report";
    }

}