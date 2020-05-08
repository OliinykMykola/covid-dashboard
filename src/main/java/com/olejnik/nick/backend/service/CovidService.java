package com.olejnik.nick.backend.service;

import com.olejnik.nick.backend.data.Day;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CovidService {

    private final WebService webService;

    public CovidService(WebService webService) {
        this.webService = webService;
    }

    public List<Day> getLatestUkraineData(){
        return webService.timeline().stream().filter(day -> day.getConfirmed() > 99).collect(Collectors.toList());
    }

}
