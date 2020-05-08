package com.olejnik.nick.backend.service;

import com.olejnik.nick.backend.data.Day;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@FeignClient(name = "coronaapi", url = "${api.url}")
public interface WebService {

    @RequestMapping(value = "/dayone/country/UA")
    List<Day> timeline();

}
