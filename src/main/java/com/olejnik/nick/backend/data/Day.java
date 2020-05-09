package com.olejnik.nick.backend.data;

import lombok.*;

import java.time.LocalDate;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Day {

    private LocalDate Date;

    private Integer confirmed;

    private Integer deaths;

    private Integer recovered;

    private Integer active;


}
