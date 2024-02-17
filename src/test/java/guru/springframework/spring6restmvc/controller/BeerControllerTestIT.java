package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class BeerControllerTestIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    /*@BeforeEach
    void setUp() {
    }*/

    @Test
    void listBeers() {
        List<BeerDTO> beerDTOList = beerController.listBeers();
        assertThat(beerDTOList.size()).isEqualTo(3);

    }

    @Transactional
    @Rollback
    @Test
    void listBeersEmptyCase() {
        beerRepository.deleteAll();
        List<BeerDTO> beerDTOList = beerController.listBeers();
        assertThat(beerDTOList.size()).isEqualTo(0);
    }

    /*@Test
    void getBeerById() {
    }*/
}