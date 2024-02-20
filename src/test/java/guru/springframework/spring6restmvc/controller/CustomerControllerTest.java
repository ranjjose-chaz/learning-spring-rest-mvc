package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.services.CustomerService;
import guru.springframework.spring6restmvc.services.CustomerServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Optional;
import java.util.UUID;

import static guru.springframework.spring6restmvc.controller.CustomerController.CUSTOMER_PATH;
import static guru.springframework.spring6restmvc.controller.CustomerController.CUSTOMER_PATH_ID;


import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CustomerController.class)
class CustomerControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    CustomerService customerService;

    CustomerServiceImpl customerServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;


    @BeforeEach
    void setUp() {
        System.out.println("CustomerControllerTest::setUp");
        customerServiceImpl = new CustomerServiceImpl();
    }

    @Test
    public void customerList() throws Exception {
        System.out.println("CustomerControllerTest::customerList");
        given(customerService.getCustomerList()).willReturn(customerServiceImpl.getCustomerList());
        mockMvc.perform(get(CUSTOMER_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", is(3)));

    }


    @Test
    void getCustomerList() throws Exception {
        given(customerService.getCustomerList()).willReturn(customerServiceImpl.getCustomerList());
        mockMvc.perform(get(CUSTOMER_PATH).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", is(3)));
    }

    @Test
    void getCustomerById() throws Exception {
        CustomerDTO firstCustomerDTO = customerServiceImpl.getCustomerList().get(0);
        given(customerService.getCustomerById(firstCustomerDTO.getId()))
                .willReturn(Optional.of(firstCustomerDTO));

        mockMvc.perform(get(CUSTOMER_PATH_ID, firstCustomerDTO.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(firstCustomerDTO.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(firstCustomerDTO.getName())));



    }

    @Test
    void saveCustomer() throws Exception {
        System.out.println("CustomerControllerTest::saveCustomer");
        CustomerDTO aCustomerDTO = customerServiceImpl.getCustomerList().get(0);
        aCustomerDTO.setVersion(null);
        aCustomerDTO.setId(null);
        given(customerService.saveCustomer(any(CustomerDTO.class))).willReturn(customerServiceImpl.getCustomerList().get(1));

        mockMvc.perform(post(CUSTOMER_PATH)
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(aCustomerDTO)))
                .andExpect(status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));



    }

    @Test
    void updateCustomerById() throws Exception {
        CustomerDTO aCustomerDTO = customerServiceImpl.getCustomerList().get(0);
        given(customerService.updateCustomerById(
                any(UUID.class),
                any(CustomerDTO.class))).willReturn(Optional.of(aCustomerDTO));
        mockMvc.perform(put(CUSTOMER_PATH_ID, aCustomerDTO.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aCustomerDTO)))
                .andExpect(status().isNoContent());

        verify(customerService).updateCustomerById(eq(aCustomerDTO.getId()), any(CustomerDTO.class));

    }

    @Test
    void deleteCustomerById() throws Exception {
        CustomerDTO aCustomerDTO = customerServiceImpl.getCustomerList().get(0);
        given(customerService.deleteCustomerById(any(UUID.class))).willReturn(Boolean.TRUE);
        mockMvc.perform(delete(CUSTOMER_PATH_ID, aCustomerDTO.getId().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        //verify(customerService).deleteCustomerById(eq(customerToBeDeleted.getId()));
        ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(customerService).deleteCustomerById(argumentCaptor.capture());
        assertThat(aCustomerDTO.getId()).isEqualTo(argumentCaptor.getValue());
    }

    @Test
    void getCustomerByIdNotFound() throws Exception {
        given(customerService.getCustomerById(any(UUID.class))).willReturn(Optional.empty());

        mockMvc.perform(get(CUSTOMER_PATH_ID, UUID.randomUUID())).andExpect(status().isNotFound());
    }
}