package guru.springframework.spring6restmvc.controller;

import guru.springframework.spring6restmvc.model.BeerDTO;
import guru.springframework.spring6restmvc.model.BeerStyle;
import guru.springframework.spring6restmvc.services.BeerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@RestController
//@RequestMapping("/api/v1/beer")
public class BeerController {

    public static final String BEER_PATH = "/api/v1/beer";
    public static final String BEER_PATH_ID = BEER_PATH + "/{beerId}";


    private final BeerService beerService;

    @GetMapping(BEER_PATH)
    public List<BeerDTO> listBeers(
            @RequestParam(required = false) String beerName,
            @RequestParam(required = false) BeerStyle beerStyle) {
        return beerService.listBeers(beerName, beerStyle);
    }

    @GetMapping(BEER_PATH_ID)
    public BeerDTO getBeerById(@PathVariable("beerId") UUID beerId){
        log.debug("Inside BeerController.getBeerById");
        return beerService.getBeerById(beerId).orElseThrow(NotFoundException::new);
    }

    @PostMapping(BEER_PATH)
    public ResponseEntity saveBeer(@Validated @RequestBody BeerDTO beerDTO) {
        System.out.println(" ** BeerController::saveBeer ** ");
        BeerDTO savedBeerDTO = beerService.saveBeer(beerDTO);
        HttpHeaders headers = new HttpHeaders();
        headers.add("Location",BEER_PATH + "/" +savedBeerDTO.getId().toString());
        return new ResponseEntity(headers, HttpStatus.CREATED);
    }

    @PutMapping(BEER_PATH_ID)
    public ResponseEntity<Void> updateBeerById(@PathVariable("beerId") UUID beerId, @Validated @RequestBody BeerDTO beerDTO) {

        if(beerService.updateBeerById(beerId, beerDTO).isEmpty()){
            throw new NotFoundException();
        }

        return new ResponseEntity<>(HttpStatus.NO_CONTENT);

    }

    @DeleteMapping(BEER_PATH_ID)
    public ResponseEntity deleteById(@PathVariable("beerId") UUID beerId){
        if(beerService.deleteById(beerId)) {
            return new ResponseEntity(HttpStatus.NO_CONTENT);
        }
        throw new NotFoundException();
    }
}
