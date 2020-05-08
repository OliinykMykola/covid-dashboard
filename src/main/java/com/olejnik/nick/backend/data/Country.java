package com.olejnik.nick.backend.data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Country {

    @EqualsAndHashCode.Include
    private String isoCode;

    private String countryregion;

    private Long total;

    private Long deaths;

    private Long recovered;

    private List<Day> days;

}
