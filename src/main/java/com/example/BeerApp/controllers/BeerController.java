package com.example.BeerApp.controllers;

import com.example.BeerApp.exceptions.NotFoundException;
import com.example.BeerApp.model.BeerDTO;
import com.example.BeerApp.model.BeerListResponse;
import com.example.BeerApp.services.BeerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@Slf4j
public class BeerController {

    public static final String BEER_PATH = "/api/v1/beer/";
    public static final String BEER_PATH_ID = BEER_PATH + "{beerId}";
    private final BeerService beerService;

    @GetMapping(BEER_PATH_ID)
    public ResponseEntity<BeerDTO> getBeerById(@PathVariable("beerId") UUID beerId) {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, BEER_PATH + beerId);
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT, "application/json");
        return new ResponseEntity<>(beerService.getBeerById(beerId).get(), headers, HttpStatus.OK);
    }

    @GetMapping(BEER_PATH)
    public BeerListResponse listBeers() {
        return beerService.listBeers();
    }

    @PostMapping(BEER_PATH)
    public ResponseEntity saveBeer(@Valid @RequestBody BeerDTO beer) {

        BeerDTO savedBeer = beerService.saveBeer(beer);

        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.LOCATION, BEER_PATH + savedBeer.getId());
        headers.add(HttpHeaders.CONTENT_TYPE, "application/json");
        headers.add(HttpHeaders.ACCEPT, "application/json");
        headers.add("Test", "Test");

        return new ResponseEntity(savedBeer, headers, HttpStatus.CREATED);
    }

    @PutMapping(BEER_PATH_ID)
    public ResponseEntity<?> updateBeerById(@PathVariable UUID beerId, @Valid @RequestBody BeerDTO beer) {
        if (!isValidForPut(beer)) {
            return ResponseEntity.badRequest().body("Invalid beer data");
        }
        return beerService.updateBeerById(beerId, beer)
                .map(updatedBeer -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Beer updated successfully");
                    response.put("beer", updatedBeer);
                    return ResponseEntity.ok(response);
                })
                .orElseThrow(() -> {

                    throw new NotFoundException("Beer not found");
                });
    }

    @PatchMapping(BEER_PATH_ID)
    public ResponseEntity<Map<String, Object>> patchBeerById(@PathVariable UUID beerId, @Valid @RequestBody BeerDTO beer) {
        return beerService.patchBeerById(beerId, beer)
                .map(updatedBeer -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "success");
                    response.put("message", "Beer updated successfully");
                    response.put("beer", updatedBeer);
                    return ResponseEntity.ok(response);
                })
                .orElseGet(() -> {
                    Map<String, Object> response = new HashMap<>();
                    response.put("status", "error");
                    response.put("message", "Beer not found");
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
                });
    }

    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity deleteBeerById(@PathVariable UUID beerId) {
        beerService.deleteBeerById(beerId);
        return new ResponseEntity(HttpStatus.NO_CONTENT);
    }

    private boolean isValidForPut(BeerDTO beer) {
        return beer.getBeerName() != null && !beer.getBeerName().isEmpty()
                && beer.getUpc() != null && !beer.getUpc().isEmpty();
    }
}
