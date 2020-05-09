package com.olejnik.nick.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegionData {

    @JsonProperty("id")
    private Integer id;

    @JsonProperty("country")
    private String country;

    @JsonProperty("confirmed")
    private Integer confirmed;

    @JsonProperty("deaths")
    private Integer deaths;

    @JsonProperty("recovered")
    private Integer recovered;

    @JsonProperty("existing")
    private Integer existing;

    @JsonProperty("suspicion")
    private Integer suspicion;

    @JsonProperty("delta_confirmed")
    private Integer delta_confirmed;

    @JsonProperty("delta_deaths")
    private Integer delta_deaths;

    @JsonProperty("delta_recovered")
    private Integer delta_recovered;

    @JsonProperty("delta_existing")
    private Integer delta_existing;


    @JsonProperty("delta_suspicion")
    private Integer delta_suspicion;

}
