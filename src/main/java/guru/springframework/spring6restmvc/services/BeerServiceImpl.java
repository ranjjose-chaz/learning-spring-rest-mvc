package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private final Map<UUID, Beer> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();
        Beer beer1 = Beer.builder()
                        .id(UUID.randomUUID())
                        .version(1)
                        .beerName("Galaxy Cat")
                        .beerStyle(BeerStyle.A)
                        .upc("123456")
                        .price(new BigDecimal("12.45"))
                        .quantityOnHand(122)
                        .createdDate(LocalDateTime.now())
                        .updateDate(LocalDateTime.now())
                        .build();

        Beer beer2 = Beer.builder()
                        .id(UUID.randomUUID())
                        .version(1)
                        .beerName("Crank")
                        .beerStyle(BeerStyle.B)
                        .upc("12434")
                        .price(new BigDecimal("13.95"))
                        .quantityOnHand(120)
                        .createdDate(LocalDateTime.now())
                        .updateDate(LocalDateTime.now())
                        .build();

        Beer beer3 = Beer.builder()
                    .id(UUID.randomUUID())
                    .version(1)
                    .beerName("Sunshine City")
                    .beerStyle(BeerStyle.C)
                    .upc("22478")
                    .price(new BigDecimal("15.25"))
                    .quantityOnHand(200)
                    .createdDate(LocalDateTime.now())
                    .updateDate(LocalDateTime.now())
                    .build();

        beerMap.put(beer1.getId(), beer1);
        beerMap.put(beer2.getId(), beer2);
        beerMap.put(beer3.getId(), beer3);

    }

    @Override
    public List<Beer> listBeers() {
        return new ArrayList<>(beerMap.values());
    }


    @Override
    public Optional<Beer> getBeerById(UUID beerId) {
        log.debug("Inside BeerServiceImpl.getBeerById");
        return Optional.of(beerMap.get(beerId));
    }

    @Override
    public Beer saveBeer(Beer beer) {
        System.out.println("** BeerServiceImpl.saveBeer **");
        Beer savedBeer =  Beer.builder()
                                .id(UUID.randomUUID())
                                .createdDate(LocalDateTime.now())
                                .updateDate(LocalDateTime.now())
                                .beerName(beer.getBeerName())
                                .beerStyle(beer.getBeerStyle())
                                .version(beer.getVersion())
                                .quantityOnHand(beer.getQuantityOnHand())
                                .price(beer.getPrice())
                                .upc(beer.getUpc())
                                .build();

        beerMap.put(savedBeer.getId(), savedBeer);

        return savedBeer;
    }

    @Override
    public void updateBeerById(UUID beerId, Beer beer) {
        Beer existingBeer = beerMap.get(beerId);
        existingBeer.setBeerName(beer.getBeerName());
        existingBeer.setBeerStyle(beer.getBeerStyle());
        existingBeer.setPrice(beer.getPrice());
        existingBeer.setUpc(beer.getUpc());
        existingBeer.setQuantityOnHand(beer.getQuantityOnHand());
        existingBeer.setUpdateDate(LocalDateTime.now());


    }

    @Override
    public void deleteById(UUID beerId) {
        Beer deletedBeer = beerMap.remove(beerId);
        System.out.println("Deleted Beed: " + deletedBeer);
    }
}
