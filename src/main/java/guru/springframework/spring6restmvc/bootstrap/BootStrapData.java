package guru.springframework.spring6restmvc.bootstrap;

import guru.springframework.spring6restmvc.entities.Beer;
import guru.springframework.spring6restmvc.entities.Customer;
import guru.springframework.spring6restmvc.model.BeerCSVRecord;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.repositories.BeerRepository;
import guru.springframework.spring6restmvc.repositories.CustomerRepository;
import guru.springframework.spring6restmvc.services.BeerCsvService;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ResourceUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

@Component
@RequiredArgsConstructor
public class BootStrapData implements CommandLineRunner {
    private final BeerRepository beerRepository;
    private final CustomerRepository customerRepository;
    private final BeerCsvService beerCsvService;

    @Transactional
    @Override
    public void run(String... args) throws Exception {

        loadCsvData();

        loadBeersIntoMap();

    }

    private void loadBeersIntoMap() {
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

    private void loadCsvData() throws FileNotFoundException {
        if(beerRepository.count() < 10) {
            File file = ResourceUtils.getFile("classpath:csvdata/beers.csv");

            List<BeerCSVRecord> recs = beerCsvService.convertCSV(file);
            recs.forEach(
                    beerCSVRecord -> {
                        BeerStyle beerStyle = switch (beerCSVRecord.getStyle()) {
                            case "American Pale Lager" -> BeerStyle.LAGER;
                            case "American Pale Ale (APA)", "American Black Ale", "Belgian Dark Ale", "American Blonde Ale" ->
                                    BeerStyle.ALE;
                            case "American IPA", "American Double / Imperial IPA", "Belgian IPA" -> BeerStyle.IPA;
                            case "American Porter" -> BeerStyle.PORTER;
                            case "Oatmeal Stout", "American Stout" -> BeerStyle.STOUT;
                            case "Saison / Farmhouse Ale" -> BeerStyle.SAISON;
                            case "Fruit / Vegetable Beer", "Winter Warmer", "Berliner Weissbier" -> BeerStyle.WHEAT;
                            case "English Pale Ale" -> BeerStyle.PALE_ALE;
                            default -> BeerStyle.PILSNER;
                        };

                        beerRepository.save(Beer.builder()
                                        .beerName(StringUtils.abbreviate(beerCSVRecord.getBeer(), 50))
                                        .beerStyle(beerStyle)
                                        .price(BigDecimal.TEN)
                                        .upc(beerCSVRecord.getRow().toString())
                                        .quantityOnHand(beerCSVRecord.getCount())
                                .build());




                    });

        }
    }


}
