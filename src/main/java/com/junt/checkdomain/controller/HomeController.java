package com.junt.checkdomain.controller;

import com.junt.checkdomain.service.DomainService;
import com.junt.checkdomain.service.ProxyService;
import com.junt.checkdomain.service.StatusService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@RequiredArgsConstructor
public class HomeController {
    private final StatusService statusService;
    private final DomainService domainService;
    private final ProxyService proxyService;
    @GetMapping("/")
    public String index(Model model){
        model.addAttribute("listDomain", domainService.getAllDomains());
        return "index";
    }
    @GetMapping("/status")
    public String status(Model model){
        model.addAttribute("lstStatus",statusService.getAllStatus());
        return "staus";
    }
    @GetMapping("/proxy")
    public String proxy(Model model){
        return "proxy";
    }
}

