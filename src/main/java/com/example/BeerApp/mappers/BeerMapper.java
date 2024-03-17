package com.example.BeerApp.mappers;

import com.example.BeerApp.entities.Beer;
import com.example.BeerApp.model.BeerDTO;
import org.mapstruct.Mapper;

@Mapper
public interface BeerMapper {

    Beer beerDtoToBeer(BeerDTO dto);

    BeerDTO beerToBeerDto(Beer beer);
}
