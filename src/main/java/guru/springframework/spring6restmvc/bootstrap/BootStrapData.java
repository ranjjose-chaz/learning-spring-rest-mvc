package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;

@Component
@RequiredArgsConstructor
public class BootStrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    @Override
    public void run(String... args) throws Exception {

        Beer beer1 = Beer.builder()
                        .beerName("Beer 1")
                        .beerStyle(BeerStyle.A)
                        .quantityOnHand(100)
                .upc("123")
                .price(BigDecimal.TEN)
                        .createdDate(LocalDateTime.now())
                .build();

        Beer beer2 = Beer.builder()
                .beerName("Beer 2")
                .beerStyle(BeerStyle.B)
                .quantityOnHand(200)
                .upc("123")
                .price(BigDecimal.TEN)
                .createdDate(LocalDateTime.now())
                .build();

        Beer beer3 = Beer.builder()
                .beerName("Beer 2")
                .beerStyle(BeerStyle.C)
                .quantityOnHand(250)
                .upc("123")
                .price(BigDecimal.TEN)
                .createdDate(LocalDateTime.now())
                .build();



        beerRepository.saveAll(Arrays.asList(beer1, beer2, beer3));

        Customer cust1 = Customer.builder()
                        .name("Customer1")
                .build();

        Customer cust2 = Customer.builder()
                .name("Customer2")
                .build();

        Customer cust3 = Customer.builder()
                .name("Customer3")
                .build();

        customerRepository.saveAll(Arrays.asList(cust1, cust2, cust3));






    }
}
