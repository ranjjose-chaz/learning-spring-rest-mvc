package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Beer;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.sql.SQLOutput;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH;
import static guru.springframework.spring6restmvc.controller.BeerController.BEER_PATH_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@SpringBootTest
@WebMvcTest(BeerController.class)
class BeerControllerTest {

    //@Autowired
    //BeerController beerController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    BeerService beerService;

    BeerServiceImpl beerServiceImpl;

    @BeforeEach
    void setUp() {
        System.out.println("BeerControllerTest::setUp");
        beerServiceImpl = new BeerServiceImpl();
    }

    @Test
    void getBeerById() throws Exception {
        System.out.println("BeerControllerTest::getBeerById");
        Beer firstBeer = beerServiceImpl.listBeers().get(0);
        //given(beerService.getBeerById(any(UUID.class))).willReturn(firstBeer);
        given(beerService.getBeerById(firstBeer.getId())).willReturn(firstBeer);

        mockMvc.perform(get(BEER_PATH_ID, firstBeer.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(firstBeer.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(firstBeer.getBeerName().toString())))
                ;

        //beerController.getBeerById(UUID.randomUUID());

    }

    @Test
    void listBeers() throws Exception  {
        System.out.println("BeerControllerTest::listBeers");
        given(beerService.listBeers()).willReturn(beerServiceImpl.listBeers());

        mockMvc.perform(get(BEER_PATH)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.length()", is(3)));

    }

    @Test
    void testCreateNewBeer() throws Exception {

        System.out.println("BeerControllerTest::testCreateNewBeer");

        Beer beer = beerServiceImpl.listBeers().get(0);
        beer.setVersion(null);
        beer.setId(null);

        given(beerService.saveBeer(any(Beer.class))).willReturn(beerServiceImpl.listBeers().get(1));

        mockMvc.perform(post(BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isCreated())

                .andExpect(header().exists("Location"));

        System.out.println("Beers Count -> "+ beerServiceImpl.listBeers().size());

    }

    @Test
    void updateBeerById() throws Exception {
        System.out.println("BeerControllerTest.updateBeerById");
        Beer beer = beerServiceImpl.listBeers().get(0);

        mockMvc.perform(put(BEER_PATH_ID, beer.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beer)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeerById(eq(beer.getId()), any(Beer.class));

    }

    @Test
    void deleteBeerById() throws Exception {
        Beer beerToBeDeleted = beerServiceImpl.listBeers().get(0);
        mockMvc.perform(delete(BEER_PATH_ID, beerToBeDeleted.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //verify(beerService).deleteById(eq(beerToBeDeleted.getId()));
        ArgumentCaptor<UUID> beerIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(beerService).deleteById(beerIdCaptor.capture());
        assertThat(beerToBeDeleted.getId()).isEqualTo(beerIdCaptor.getValue());


    }
}