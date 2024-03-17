package com.example.BeerApp.services;

import com.example.BeerApp.model.BeerDTO;
import com.example.BeerApp.model.BeerListResponse;
import com.example.BeerApp.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Slf4j
public class BeerServiceImpl implements BeerService {

    private final HashMap<UUID, BeerDTO> beerMap = new HashMap<>();

    public BeerServiceImpl() {
        BeerDTO beer = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(1)
                .beerName("Galaxy Cat")
                .beerStyle(BeerStyle.PALE_ALE)
                .upc("12356")
                .price(new BigDecimal("12.99"))
                .quantityOnHand(122)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        BeerDTO beer2 = BeerDTO.builder()
                .id(UUID.randomUUID())
                .version(2)
                .beerName("Hohoho Cat")
                .beerStyle(BeerStyle.LAGER)
                .upc("12356")
                .price(new BigDecimal("11.99"))
                .quantityOnHand(155)
                .createdDate(LocalDateTime.now())
                .updateDate(LocalDateTime.now())
                .build();

        beerMap.put(beer.getId(), beer);
        beerMap.put(beer2.getId(), beer2);
    }

    @Override
    public BeerListResponse listBeers() {
        List<BeerDTO> beerList = new ArrayList<>(beerMap.values());
        return new BeerListResponse(beerList, beerList.size());
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        log.debug("[1] - Get Beer by Id - in service. Id: " + id.toString());

        return Optional.of(beerMap.get(id));
    }

    @Override
    public BeerDTO saveBeer(BeerDTO beer) {
        beerMap.put(beer.getId(), beer);
        return beerMap.get(beer.getId());
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID id, BeerDTO beer) {

        AtomicReference<BeerDTO> updatedBeerRef = new AtomicReference<>();
        beerMap.computeIfPresent(id, (key, existingBeer) -> {
            existingBeer.setBeerName(beer.getBeerName());
            existingBeer.setBeerStyle(beer.getBeerStyle());
            existingBeer.setUpc(beer.getUpc());
            existingBeer.setPrice(beer.getPrice());
            existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
            existingBeer.setUpdateDate(LocalDateTime.now());
            updatedBeerRef.set(existingBeer);

            return existingBeer;
        });
        return Optional.ofNullable(updatedBeerRef.get()); // Return an Optional of the updated customer

    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID id, BeerDTO beer) {
        AtomicReference<BeerDTO> updatedBeerRef = new AtomicReference<>();

        beerMap.computeIfPresent(id, (key, existingBeer) -> {
            existingBeer.setBeerName(beer.getBeerName());
            existingBeer.setBeerStyle(beer.getBeerStyle());
            existingBeer.setUpc(beer.getUpc());
            existingBeer.setPrice(beer.getPrice());
            existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
            existingBeer.setUpdateDate(LocalDateTime.now());
            updatedBeerRef.set(existingBeer);

            return existingBeer;
        });
        return Optional.ofNullable(updatedBeerRef.get());
    }

    @Override
    public Boolean deleteBeerById(UUID id) {
        beerMap.remove(id);
        return true;
    }
}
