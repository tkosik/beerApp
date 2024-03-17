package com.example.BeerApp.services;

import com.example.BeerApp.model.BeerDTO;
import com.example.BeerApp.model.BeerListResponse;

import java.util.Optional;
import java.util.UUID;

public interface BeerService {

    Boolean deleteBeerById(UUID id);

    BeerListResponse listBeers();

    Optional<BeerDTO> getBeerById(UUID id);

    BeerDTO saveBeer(BeerDTO beer);

    Optional<BeerDTO> updateBeerById(UUID id, BeerDTO beer);

    Optional<BeerDTO> patchBeerById(UUID id, BeerDTO beer);

}
