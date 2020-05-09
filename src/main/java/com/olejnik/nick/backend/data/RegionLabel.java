package com.olejnik.nick.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegionLabel {

    @JsonProperty("uk")
    private String uk;

    @JsonProperty("en")
    private String en;



}
