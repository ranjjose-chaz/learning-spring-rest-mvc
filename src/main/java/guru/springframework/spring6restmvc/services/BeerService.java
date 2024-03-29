package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
    List<BeerDTO> listBeers(String beerName, BeerStyle beerStyle);

    public Optional<BeerDTO> getBeerById(UUID beerId);

    BeerDTO saveBeer(BeerDTO beerDTO);

    Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO);

    Boolean deleteById(UUID beerId);
}
