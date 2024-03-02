package guru.springframework.spring6restmvc.repositories;

import guru.springframework.spring6restmvc.bootstrap.BootStrapData;
import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerCsvServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({BootStrapData.class, BeerCsvServiceImpl.class})
class BeerRepositoryTest {

    @Autowired
    BeerRepository beerRepository;

    @Test
    public void testSaveBeer() {
        Beer beer = beerRepository.save(Beer.builder()
                        .beerName("My Beer")
                        .beerStyle(BeerStyle.C)
                        .upc("123")
                        .price(BigDecimal.valueOf(100.55))
                .build());
        beerRepository.flush();
        assertThat(beer).isNotNull();
        assertThat(beer.getId()).isNotNull();
    }

    @Test
    public void testSaveBeerNameTooLong() {
        assertThrows(ConstraintViolationException.class, () ->{
            Beer beer = beerRepository.save(Beer.builder()
                    .beerName("My Beer 32483298403284032840932084320480392840328409328409328409328408324098324098320483209483209432094832094832094832094832094")
                    .beerStyle(BeerStyle.C)
                    .upc("123")
                    .price(BigDecimal.valueOf(100.55))
                    .build());

            beerRepository.flush();

        });
    }

    @Test
    void findAllByBeerNameIsLikeIgnoreCase() {
        List<Beer> list = beerRepository.findAllByBeerNameIsLikeIgnoreCase("%IPA%");
        assertThat(list.size()).isEqualTo(336);
    }

    @Test
    void findAllByBeerStyle() {
        List<Beer> list = beerRepository.findAllByBeerStyle(BeerStyle.ALE);
        assertThat(list.size()).isEqualTo(400);
    }

    @Test
    void findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle() {
        List<Beer> list = beerRepository.findAllByBeerNameIsLikeIgnoreCaseAndBeerStyle("%IPA%", BeerStyle.ALE);
        assertThat(list.size()).isEqualTo(11);
    }
}