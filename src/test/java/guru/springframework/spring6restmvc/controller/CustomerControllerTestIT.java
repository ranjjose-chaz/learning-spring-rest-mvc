package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.mappers.CustomerMapper;
import guru.springframework.spring6restmvc.model.CustomerDTO;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerControllerTestIT {

    @Autowired
    CustomerController customerController;

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper customerMapper;



    @Test
    void getCustomerList() {
        assertThat(customerController.getCustomerList().size()).isEqualTo(3);
    }

    @Transactional
    @Rollback
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

    @Test
    void saveCustomer() {
        CustomerDTO customerDTO = CustomerDTO.builder()
                .name("New Customer")
                .build();

        ResponseEntity responseEntity = customerController.saveCustomer(customerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(responseEntity.getHeaders().getLocation()).isNotNull();
        String[] parts = responseEntity.getHeaders().getLocation().getPath().split("/");
        UUID uuid = UUID.fromString(parts[4]);
        assertThat(customerRepository.findById(uuid)).isNotNull();
        assertThat(customerRepository.findById(uuid).get().getId()).isEqualTo(uuid);
    }

    @Transactional
    @Rollback
    @Test
    void updateCustomer() {
        CustomerDTO aCustomerDTO = customerMapper.customerToCustomerDto(customerRepository.findAll().get(0));
        UUID uuid = aCustomerDTO.getId();
        aCustomerDTO.setVersion(null);
        aCustomerDTO.setId(null);
        aCustomerDTO.setName(aCustomerDTO.getName() + " Updated!");
        ResponseEntity responseEntity = customerController.updateCustomerById(uuid, aCustomerDTO);
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        CustomerDTO customerDTO = customerMapper.customerToCustomerDto(customerRepository.findById(uuid).get());
        assertThat(customerDTO.getName()).isEqualTo(aCustomerDTO.getName());

    }

    @Test
    void updateCustomerNotFound() {
        assertThrows(
                NotFoundException.class,
                () -> customerController.updateCustomerById(
                                        UUID.randomUUID(),
                                        CustomerDTO.builder().build()));
    }

    @Transactional
    @Rollback
    @Test
    void deleteById() {
        Customer aCustomer = customerRepository.findAll().get(0);
        ResponseEntity responseEntity = customerController.deleteCustomerById(aCustomer.getId());
        assertThat(responseEntity.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(customerRepository.findById(aCustomer.getId())).isEmpty();
    }

    @Test
    void deleteByIdNotFound() {
        assertThrows(NotFoundException.class,
                () -> customerController.deleteCustomerById(UUID.randomUUID()));
    }



}