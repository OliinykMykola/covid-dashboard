package com.olejnik.nick.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class TimeLine {

    @JsonProperty("dates")
    List<String> dates;

    @JsonProperty("confirmed")
    List<Integer> confirmed;

    @JsonProperty("deaths")
    List<Integer> deaths;

    @JsonProperty("recovered")
    List<Integer> recovered;

    @JsonProperty("existing")
    List<Integer> existing;


}
