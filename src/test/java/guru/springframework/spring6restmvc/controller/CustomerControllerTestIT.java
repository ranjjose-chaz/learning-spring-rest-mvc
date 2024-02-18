package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerControllerTestIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;



    @Test
    void getCustomerList() {
        assertThat(customerController.getCustomerList().size()).isEqualTo(3);
    }

    @Test
    void getCustomerListEmptyCase(){
        customerRepository.deleteAll();
        assertThat(customerController.getCustomerList().size()).isEqualTo(0);
    }

    @Test
    void getCustomerById() {
        Customer customer = customerRepository.findAll().get(0);
        assertThat(customerController.getCustomerById(customer.getId())).isNotNull();
    }

    @Test
    void getCustomerByIdNotFound() {
        assertThrows(NotFoundException.class, () -> {
           customerController.getCustomerById(UUID.randomUUID());
        });
    }
}