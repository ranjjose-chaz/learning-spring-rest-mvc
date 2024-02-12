package guru.springframework.spring6restmvc.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import guru.springframework.spring6restmvc.model.Customer;
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
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

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
        mockMvc.perform(get("/api/v1/customer").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", is(3)));

    }


    @Test
    void getCustomerList() throws Exception {
        given(customerService.getCustomerList()).willReturn(customerServiceImpl.getCustomerList());
        mockMvc.perform(get("/api/v1/customer").accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.length()", is(3)));
    }

    @Test
    void getCustomerById() throws Exception {
        Customer firstCustomer = customerServiceImpl.getCustomerList().get(0);
        given(customerService.getCustomerById(firstCustomer.getId()))
                .willReturn(firstCustomer);

        mockMvc.perform(get("/api/v1/customer/"+firstCustomer.getId().toString())
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(firstCustomer.getId().toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is(firstCustomer.getName())));



    }

    @Test
    void saveCustomer() throws Exception {
        System.out.println("CustomerControllerTest::saveCustomer");
        Customer aCustomer = customerServiceImpl.getCustomerList().get(0);
        aCustomer.setVersion(null);
        aCustomer.setId(null);
        given(customerService.saveCustomer(any(Customer.class))).willReturn(customerServiceImpl.getCustomerList().get(1));

        mockMvc.perform(post("/api/v1/customer")
                            .accept(MediaType.APPLICATION_JSON)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(aCustomer)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.header().exists("Location"));



    }

    @Test
    void updateCustomerById() throws Exception {
        Customer aCustomer = customerServiceImpl.getCustomerList().get(0);
        mockMvc.perform(put("/api/v1/customer/"+aCustomer.getId().toString())
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(aCustomer)))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        verify(customerService).updateCustomerById(eq(aCustomer.getId()), any(Customer.class));

    }

    @Test
    void deleteCustomerById() throws Exception {
        Customer customerToBeDeleted = customerServiceImpl.getCustomerList().get(0);
        mockMvc.perform(delete("/api/v1/customer/"+customerToBeDeleted.getId().toString())
                                .accept(MediaType.APPLICATION_JSON)
                                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isNoContent());

        //verify(customerService).deleteCustomerById(eq(customerToBeDeleted.getId()));
        ArgumentCaptor<UUID> argumentCaptor = ArgumentCaptor.forClass(UUID.class);
        verify(customerService).deleteCustomerById(argumentCaptor.capture());
        assertThat(customerToBeDeleted.getId()).isEqualTo(argumentCaptor.getValue());
    }
}