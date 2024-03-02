package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Service
@Primary // So that this impl will be preferred by Spring
@RequiredArgsConstructor
public class BeerServiceJPA implements BeerService {

    private final BeerRepository beerRepository;
    private final BeerMapper beerMapper;

    @Override
    public List<BeerDTO> listBeers(String beerName, BeerStyle beerStyle) {
        List<Beer> beerList;

        if(StringUtils.hasText(beerName) && beerStyle != null){
            beerList = beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle(
                    "%" + beerName  + "%",
                    beerStyle);
        } else if(StringUtils.hasText(beerName)){
         beerList = findBeersLikeBeerName(beerName);
        } else if(beerStyle != null){
            beerList = beerRepository.findAllByBeerStyle(beerStyle);
        } else {
            beerList = beerRepository.findAll();
        }
        return beerList.stream().map(beerMapper::beerToBeerDto).toList();
    }

    public List<Beer> findBeersLikeBeerName(String beerName) {
        return beerRepository.findAllByBeerNameIsLikeIgnoreCase("%" + beerName  + "%");

    }

    @Override
    public Optional<BeerDTO> getBeerById(UUID beerId) {
        return Optional.ofNullable(beerMapper.beerToBeerDto(beerRepository.findById(beerId).orElse(null)));
    }

    @Override
    public BeerDTO saveBeer(BeerDTO beerDTO) {
        return beerMapper.beerToBeerDto(
                beerRepository.save(
                        beerMapper.beerDtoToBeer(beerDTO)
                )
        );
    }

    @Override
    public Optional<BeerDTO> updateBeerById(UUID beerId, BeerDTO beerDTO) {

        AtomicReference<Optional<BeerDTO>> atomicRef = new AtomicReference<>();
        beerRepository.findById(beerId)
                .ifPresentOrElse(foundBeer -> {
                    foundBeer.setBeerName(beerDTO.getBeerName());
                    foundBeer.setBeerStyle(beerDTO.getBeerStyle());
                    foundBeer.setUpc(beerDTO.getUpc());
                    foundBeer.setPrice(beerDTO.getPrice());
                    beerRepository.save(foundBeer);
                    atomicRef.set(Optional.of(beerMapper.beerToBeerDto(foundBeer)));
                }, () -> {
                    atomicRef.set(Optional.empty());
                });

        return atomicRef.get();
    }

    @Override
    public Boolean deleteById(UUID beerId) {
        if(beerRepository.existsById(beerId)) {
            beerRepository.deleteById(beerId);
            return true;
        }
        return false;

    }
}
