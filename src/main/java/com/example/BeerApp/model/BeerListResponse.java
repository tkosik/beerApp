package com.example.BeerApp.model;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class BeerListResponse {

    private List<BeerDTO> beers;
    private Integer size;

}
