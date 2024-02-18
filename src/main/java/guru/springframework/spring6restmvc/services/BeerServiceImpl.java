package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class BeerServiceImpl implements BeerService {

    private final Map<UUID, BeerDTO> beerMap;

    public BeerServiceImpl() {
        this.beerMap = new HashMap<>();
        BeerDTO beerDTO1 = BeerDTO.builder()
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

        BeerDTO beerDTO2 = BeerDTO.builder()
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

        BeerDTO beerDTO3 = BeerDTO.builder()
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

        beerMap.put(beerDTO1.getId(), beerDTO1);
        beerMap.put(beerDTO2.getId(), beerDTO2);
        beerMap.put(beerDTO3.getId(), beerDTO3);

    }

    @Override
    public List<BeerDTO> listBeers() {
        return new ArrayList<>(beerMap.values());
    }


    @Override
    public Optional<BeerDTO> getBeerById(UUID beerId) {
        log.debug("Inside BeerServiceImpl.getBeerById");
        return Optional.of(beerMap.get(beerId));
    }

    @Override
    public BeerDTO saveBeer(BeerDTO beerDTO) {
        System.out.println("** BeerServiceImpl.saveBeer **");
        BeerDTO savedBeerDTO =  BeerDTO.builder()
                                .id(UUID.randomUUID())
                                .createdDate(LocalDateTime.now())
                                .updateDate(LocalDateTime.now())
                                .beerName(beerDTO.getBeerName())
                                .beerStyle(beerDTO.getBeerStyle())
                                .version(beerDTO.getVersion())
                                .quantityOnHand(beerDTO.getQuantityOnHand())
                                .price(beerDTO.getPrice())
                                .upc(beerDTO.getUpc())
                                .build();

        beerMap.put(savedBeerDTO.getId(), savedBeerDTO);

        return savedBeerDTO;
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO) {
        BeerDTO existingBeerDTO = beerMap.get(beerId);
        existingBeerDTO.setBeerName(beerDTO.getBeerName());
        existingBeerDTO.setBeerStyle(beerDTO.getBeerStyle());
        existingBeerDTO.setPrice(beerDTO.getPrice());
        existingBeerDTO.setUpc(beerDTO.getUpc());
        existingBeerDTO.setQuantityOnHand(beerDTO.getQuantityOnHand());
        existingBeerDTO.setUpdateDate(LocalDateTime.now());

        return Optional.of(existingBeerDTO);


    }

    @Override
    public void deleteById(UUID beerId) {
        BeerDTO deletedBeerDTO = beerMap.remove(beerId);
        System.out.println("Deleted Beed: " + deletedBeerDTO);
    }
}
