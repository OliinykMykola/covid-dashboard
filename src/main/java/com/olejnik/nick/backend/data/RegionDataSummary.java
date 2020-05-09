package com.olejnik.nick.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class RegionDataSummary {

    @JsonProperty("ukraine")
    List<RegionData> ukraine;

    @JsonProperty("world")
    List<RegionData> world;
}
