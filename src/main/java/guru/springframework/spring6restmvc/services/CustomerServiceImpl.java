package guru.springframework.spring6restmvc.services;

import guru.springframework.spring6restmvc.model.CustomerDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

    private Map<UUID, CustomerDTO> customerMap;

    public CustomerServiceImpl() {
        customerMap = new HashMap<>();

        CustomerDTO cust1 = CustomerDTO
                .builder()
                .id(UUID.randomUUID())
                .name("Kingfisher")
                .version(1)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        CustomerDTO cust2 = CustomerDTO
                .builder()
                .id(UUID.randomUUID())
                .name("Jawan")
                .version(1)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        CustomerDTO cust3 = CustomerDTO
                .builder()
                .id(UUID.randomUUID())
                .name("Ceaser")
                .version(1)
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();

        customerMap.put(cust1.getId(), cust1);
        customerMap.put(cust2.getId(), cust2);
        customerMap.put(cust3.getId(), cust3);

    }

    @Override
    public List<CustomerDTO> getCustomerList() {
        return new ArrayList<>(customerMap.values());
    }

    @Override
    public Optional<CustomerDTO> getCustomerById(UUID id) {
        return Optional.of(customerMap.get(id));
    }

    @Override
    public CustomerDTO saveCustomer(CustomerDTO customerDTO) {
        System.out.println("CustomerServiceImpl::saveCustomer");
        CustomerDTO savedCustomerDTO = CustomerDTO.builder()
                .id(UUID.randomUUID())
                .name(customerDTO.getName())
                .version(customerDTO.getVersion())
                .createdDate(LocalDateTime.now())
                .lastModifiedDate(LocalDateTime.now())
                .build();
        customerMap.put(savedCustomerDTO.getId(), savedCustomerDTO);
        return savedCustomerDTO;
    }

    @Override
    public void updateCustomerById(UUID customerId, CustomerDTO customerDTO) {
        CustomerDTO existingCustomerDTO = customerMap.get(customerId);
        existingCustomerDTO.setName(customerDTO.getName());
        existingCustomerDTO.setVersion(customerDTO.getVersion());
        existingCustomerDTO.setLastModifiedDate(LocalDateTime.now());

    }

    @Override
    public void deleteCustomerById(UUID customerId) {
        CustomerDTO existingCustomerDTO = customerMap.remove(customerId);
        if(existingCustomerDTO == null) {
            log.warn("No customer found with id: "+customerId);
        } else {
            log.debug("Removed customer -> " + existingCustomerDTO);
        }

    }
}
