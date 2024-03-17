package com.example.BeerApp.services;

import com.example.BeerApp.exceptions.NotFoundException;
import com.example.BeerApp.mappers.BeerMapper;
import com.example.BeerApp.model.BeerDTO;
import com.example.BeerApp.model.BeerListResponse;
import com.example.BeerApp.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public Boolean deleteBeerById(UUID id) {
        if (beerRepository.existsById(id)) {
            beerRepository.deleteById(id);
            return true;
        } else {
            throw new NotFoundException("Beer not found with id: " + id);
        }
    }

    @Override
    public BeerListResponse listBeers() {
        List<BeerDTO> beerDto = beerRepository.findAll()
                .stream()
                .map(beerMapper::beerToBeerDto)
                .toList();

        return new BeerListResponse(beerDto, beerDto.size());
    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID id) {
        return Optional.ofNullable(beerRepository.findById(id)
                .map(beerMapper::beerToBeerDto) // Convert if present
                .orElseThrow(() -> new NotFoundException("Beer not found with id: " + id)));
    }

    @Override
    public BeerDTO saveBeer(BeerDTO beer) {
        return beerMapper.beerToBeerDto(beerRepository.save(beerMapper.beerDtoToBeer(beer)));
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID id, BeerDTO beer) {
        AtomicReference<Optional<BeerDTO>> atomicReference = new AtomicReference<>();

        beerRepository.findById(id).ifPresentOrElse(beer1 -> {
                    beer1.setBeerName(beer.getBeerName());
                    beer1.setBeerStyle(beer.getBeerStyle());
                    beer1.setPrice(beer.getPrice());
                    beer1.setUpc(beer.getUpc());
                    atomicReference.set(Optional.of(beerMapper.beerToBeerDto(beerRepository.save(beer1))));
                },
                () -> {
                    atomicReference.set(Optional.empty());
                });
        return atomicReference.get();
    }

    @Override
    public Optional<BeerDTO> patchBeerById(UUID id, BeerDTO beer) {
        return beerRepository.findById(id).map(beer1 -> {
            if (beer.getBeerName() != null) {
                beer1.setBeerName(beer.getBeerName());
            }
            if (beer.getBeerStyle() != null) {
                beer1.setBeerStyle(beer.getBeerStyle());
            }
            if (beer.getPrice() != null) {
                beer1.setPrice(beer.getPrice());
            }
            if (beer.getUpc() != null) {
                beer1.setUpc(beer.getUpc());
            }
            return beerMapper.beerToBeerDto(beerRepository.save(beer1));
        });
    }
}
