package com.example.demo.Controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class ForwardController {

    @GetMapping(value = {"/", "/login", "/register"})
    public String index() {
        return "forward:/index.html";
    }

    // Osigurava da se rute koje ne sadrže ekstenzije i nisu statičke datoteke preusmjeravaju na `index.html`
    @RequestMapping(value = "/{path:[^\\.]*}")
    public String redirect(@PathVariable String path) {
        return "forward:/index.html";
    }
}


