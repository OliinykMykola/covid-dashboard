package com.olejnik.nick.backend.data;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer;
import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Day {

    @JsonDeserialize(using = LocalDateDeserializer.class)
    @JsonSerialize(using = LocalDateSerializer.class)
    private LocalDate Date;

    @JsonProperty("Confirmed")
    private Integer confirmed;

    @JsonProperty("Deaths")
    private Integer deaths;

    @JsonProperty("Recovered")
    private Integer recovered;

    @JsonProperty("Active")
    private Integer active;

    private Integer newConfirmed;

    private Integer newDeaths;

    private Integer newRecovered;

}
