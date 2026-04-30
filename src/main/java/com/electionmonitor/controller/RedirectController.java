package com.electionmonitor.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class RedirectController {

    @GetMapping("/api/")
    public String redirectSwaggerWithSlash() {
        return "redirect:/api";
    }
}
