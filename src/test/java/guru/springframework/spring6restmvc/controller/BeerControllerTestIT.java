package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.mappers.BeerMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class BeerControllerTestIT {

    @Autowired
    BeerController beerController;

    @Autowired
    BeerRepository beerRepository;

    @Autowired
    BeerMapper beerMapper;

    @Autowired
    WebApplicationContext wac;

    MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    void listBeers() {
        List<BeerDTO> beerDTOList = beerController.listBeers(null, null);
        assertThat(beerDTOList.size()).isEqualTo(2413);

    }

    @Transactional
    @Rollback
    @Test
    void listBeersEmptyCase() {
        beerRepository.deleteAll();
        List<BeerDTO> beerDTOList = beerController.listBeers(null, null);
        assertThat(beerDTOList.size()).isEqualTo(0);
    }

    @Test
    void getBeerById() {
        Beer beer = beerRepository.findAll().get(0);
        assertThat(beerController.getBeerById(beer.getId())).isNotNull();
    }

    @Test
    void getBeerByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.getBeerById(UUID.randomUUID());
        });
    }

    @Transactional
    @Rollback
    @Test
    void saveBeer() {
        BeerDTO beerDTO = BeerDTO.builder()
                .beerName("New Beer")
                .beerStyle(BeerStyle.C)
                .upc("123")
                .price(BigDecimal.valueOf(12.0))
                .build();

        ResponseEntity responseEntity = beerController.saveBeer(beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] slices = responseEntity.getHeaders().getLocation().getPath().split("/");
        System.out.println(Arrays.toString(slices));
        UUID savedUuid = UUID.fromString(slices[4]);
        assertThat(beerRepository.findById(savedUuid).get()).isNotNull();

    }

    @Test
    void updateBeerNotFound() {
        assertThrows(NotFoundException.class, () ->{
            beerController.updateBeerById(UUID.randomUUID(), BeerDTO.builder().build());
        });
    }

    @Transactional
    @Rollback
    @Test
    void updateBeer() {

        Beer beer = beerRepository.findAll().get(0);
        BeerDTO beerDTO = beerMapper.beerToBeerDto(beer);
        beerDTO.setId(null);
        beerDTO.setVersion(null);
        beerDTO.setBeerName(beerDTO.getBeerName() + " Updated");

        ResponseEntity responseEntity = beerController.updateBeerById(beer.getId(), beerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);

        Beer updatedBeer = beerRepository.findById(beer.getId()).get();
        assertEquals(updatedBeer.getBeerName(), beerDTO.getBeerName());
    }
    @Test
    void testListBeersByStyle() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                        .queryParam("beerStyle", BeerStyle.ALE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(400)));
    }

    @Test
    void testListBeersByName() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                .queryParam("beerName", "IPA"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(336)));
    }

    @Test
    void testListBeersByBeerNameAndStyle() throws Exception {
        mockMvc.perform(get(BEER_PATH)
                .queryParam("beerName", "IPA")
                .queryParam("beerStyle", BeerStyle.ALE.name()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()", is(11)));

    }

    @Transactional
    @Rollback
    @Test
    void deleteById() {
        Beer aBeer = beerRepository.findAll().get(0);
        ResponseEntity responseEntity = beerController.deleteById(aBeer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(beerRepository.findById(aBeer.getId())).isEmpty();
    }

    @Test
    void deleteByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
            beerController.deleteById(UUID.randomUUID());
        });
    }


}