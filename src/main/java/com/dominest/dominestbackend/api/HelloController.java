package com.dominest.dominestbackend.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {
    @GetMapping("/")
    public String hello(
            @RequestParam(name = "textToShow", required = false, defaultValue = "Hello Domidomi") String textToShow
    ) {
        return "<!DOCTYPE html>"
                + "<html lang=\"en\">"
                + "<head>"
                + "<meta charset=\"UTF-8\">"
                + "<title>Color Wave Text Effect</title>"
                + "<style>"
                + "@keyframes wave {"
                + "    0% {background-position: 0%;}"
                + "    100% {background-position: 100%;}"
                + "}"
                + ".wave-text {"
                + "    font-size: 36px;"
                + "    background: linear-gradient(90deg, red, orange, yellow, green, blue, indigo, violet);"
                + "    -webkit-background-clip: text;"
                + "    color: transparent;"
                + "    animation: wave 3s infinite linear;"
                + "    background-size: 200% auto;"
                + "    position: fixed;"
                + "    top: 50%;"
                + "    left: 50%;"
                + "    transform: translate(-50%, -50%);"
                +"     font-size: 70px"
                + "}"
                + "</style>"
                + "</head>"
                + "<body>"
                + "<h2 class=\"wave-text\">" + textToShow + "</h2>"
                + "</body>"
                + "</html>";
    }
}
