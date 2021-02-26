package com.jesua.registration.controller;

import com.jesua.registration.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/")
    public List<Map<String, Object>> getStatistics() {

        //get all registered for event
        return homeService.getStatistics();

    }

}
