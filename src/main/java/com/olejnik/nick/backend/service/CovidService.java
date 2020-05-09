package com.olejnik.nick.backend.service;

import com.olejnik.nick.backend.data.Day;
import com.olejnik.nick.backend.data.RegionDataSummary;
import com.olejnik.nick.backend.data.TimeLine;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class CovidService {

    private final WebService webService;

    public CovidService(WebService webService) {
        this.webService = webService;
    }

    public RegionDataSummary getRegionDataSummary() {
        return webService.getSummary(LocalDate.now().toString());
    }

    public List<Day> getLatestUkraineData() {
        List<Day> result = new ArrayList<>();
        TimeLine ukraineTimeline = webService.getTimeLineForCountry("ukraine");
        List<Integer> confirmed = ukraineTimeline.getConfirmed();
        for (int i = 0; i < confirmed.size(); i++) {
            Integer confirmedCases = confirmed.get(i);
            if (confirmedCases > 99) {
                Day day = new Day(
                        LocalDate.parse(ukraineTimeline.getDates().get(i)), ukraineTimeline.getConfirmed().get(i),
                        ukraineTimeline.getDeaths().get(i),
                        ukraineTimeline.getRecovered().get(i),
                        ukraineTimeline.getExisting().get(i));
                result.add(day);
            }
        }
        return result;
    }

}
