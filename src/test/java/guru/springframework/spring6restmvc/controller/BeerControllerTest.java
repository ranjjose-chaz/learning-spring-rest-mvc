package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import guru.springframework.spring6restmvc.services.BeerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.math.BigDecimal;
import java.util.Optional;
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
        BeerDTO firstBeerDTO = beerServiceImpl.listBeers().get(0);
        //given(beerService.getBeerById(any(UUID.class))).willReturn(firstBeer);
        given(beerService.getBeerById(firstBeerDTO.getId())).willReturn(Optional.of(firstBeerDTO));

        mockMvc.perform(get(BEER_PATH_ID, firstBeerDTO.getId())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(firstBeerDTO.getId().toString())))
                .andExpect(jsonPath("$.beerName", is(firstBeerDTO.getBeerName())))
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

        BeerDTO beerDTO = beerServiceImpl.listBeers().get(0);
        beerDTO.setVersion(null);
        beerDTO.setId(null);

        given(beerService.saveBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers().get(1));

        mockMvc.perform(post(BEER_PATH)
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isCreated())

                .andExpect(header().exists("Location"));
    }

    @Test
    void testCrateNewBeerWithNull_Name_BeerStyle_upc_price() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder().build();
        //given(beerService.saveBeer(any(BeerDTO.class))).willReturn(beerServiceImpl.listBeers().get(1));
        MvcResult mvcResult = mockMvc.perform(post(BEER_PATH)
                .accept(MediaType.APPLICATION_JSON)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(5)))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void testCreateNewBeerWithNullBeerStyle() throws Exception {
        BeerDTO beerDTO = BeerDTO.builder()
                .beerName("Test Beer")
                //.beerStyle(BeerStyle.B)   //To failing the validation check
                .upc("123")
                .price(BigDecimal.valueOf(100.12))
                .build();

        //given(beerService.)
        MvcResult mvcResult = mockMvc.perform(post(BEER_PATH)
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.length()", is(1)))
                .andExpect(jsonPath("$[0].beerStyle", is("must not be null")))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());

    }

    @Test
    void testUpdateBeerById_NullBeerStyle() throws Exception {
        BeerDTO beerDTO = beerServiceImpl.listBeers().get(0);
        beerDTO.setBeerStyle(null);
        given(beerService.updateBeerById(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(beerDTO));
        MvcResult mvcResult = mockMvc.perform(put(BEER_PATH_ID, beerDTO.getId().toString())
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(beerDTO)))
                    .andExpect(status().isBadRequest())
                    .andExpect(jsonPath("$.length()", is(1)))
                .andReturn();
        System.out.println(mvcResult.getResponse().getContentAsString());
    }

    @Test
    void updateBeerById() throws Exception {
        System.out.println("BeerControllerTest.updateBeerById");
        BeerDTO beerDTO = beerServiceImpl.listBeers().get(0);

        given(beerService.updateBeerById(any(UUID.class), any(BeerDTO.class))).willReturn(Optional.of(beerDTO));

        mockMvc.perform(put(BEER_PATH_ID, beerDTO.getId().toString())
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(beerDTO)))
                .andExpect(status().isNoContent());

        verify(beerService).updateBeerById(eq(beerDTO.getId()), any(BeerDTO.class));

    }

    @Test
    void getBeerByIdNotFound() throws Exception {

        given(beerService.getBeerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(BEER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());
    }

    @Test
    void deleteBeerById() throws Exception {
        BeerDTO beerDTOToBeDeleted = beerServiceImpl.listBeers().get(0);
        given(beerService.deleteById(any(UUID.class))).willReturn(Boolean.TRUE);
        mockMvc.perform(delete(BEER_PATH_ID, beerDTOToBeDeleted.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //verify(beerService).deleteById(eq(beerToBeDeleted.getId()));
        ArgumentCaptor<UUID> beerIdCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(beerService).deleteById(beerIdCaptor.capture());
        assertThat(beerDTOToBeDeleted.getId()).isEqualTo(beerIdCaptor.getValue());

    }

    @Test
    void deleteBeerByIdNotFound() throws Exception {
        given(beerService.deleteById(any(UUID.class))).willReturn(Boolean.FALSE);
        mockMvc.perform(delete(BEER_PATH_ID, UUID.randomUUID()))
                .andExpect(status().isNotFound());

    }
}