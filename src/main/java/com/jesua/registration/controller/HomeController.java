package com.jesua.registration.controller;

import com.jesua.registration.service.HomeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class HomeController {

    private final HomeService homeService;

    @GetMapping("/")
    public List<HomeService.Statistic> getStatistics() {

        //get all registered for event
        return homeService.getStatistics();

    }

}
