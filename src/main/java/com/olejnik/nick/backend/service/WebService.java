package com.olejnik.nick.backend.service;

import com.olejnik.nick.backend.data.RegionDataSummary;
import com.olejnik.nick.backend.data.TimeLine;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;


@FeignClient(name = "coronaapi", url = "${api.url}")
public interface WebService {

    @RequestMapping(value = "/data?to={date}")
    RegionDataSummary getSummary(@PathVariable("date") String date);

    @RequestMapping(value = "/charts/main-data?mode={mode}")
    TimeLine getTimeLineForCountry(@PathVariable("mode") String mode);

}
