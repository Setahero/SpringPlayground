package com.example.springexperiments;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;


import java.net.URI;
import java.security.Principal;
import java.util.Optional;


@RestController
@RequestMapping("/cashcards")
public class CashCardController {

    private CashCardRepository cashCardRepository;

    public CashCardController(CashCardRepository cashCardRepository) {
        this.cashCardRepository = cashCardRepository;
    }

    @GetMapping("/{requestedId}")
    public ResponseEntity<CashCard> findById(@PathVariable Long requestedId, Principal principal) {
        CashCard cashCard = findCashCard(requestedId, principal);
        if (cashCard!=null) {
            return ResponseEntity.ok(cashCard);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private CashCard findCashCard(Long requestedId, Principal principal) {
        return cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
    }

    @PostMapping
    public ResponseEntity<Void> createCashCard(@RequestBody CashCard newCashCardRequest, UriComponentsBuilder ucb, Principal principal ){
        CashCard cashCardWithOwner = new CashCard(null, newCashCardRequest.amount(), principal.getName());
        CashCard savedCashCard = cashCardRepository.save(cashCardWithOwner);
        URI location = ucb.path("cashcards/{id}")
                .buildAndExpand(savedCashCard.id())
                .toUri();
    //    Optional<CashCard> cashCardOptional = cashCardRepository.findById(requestedId);
        return ResponseEntity.created(location).build();
    }


    @GetMapping
    public ResponseEntity<Iterable<CashCard>> findAll(Pageable pageable, Principal principal) {
        Page<CashCard> page = cashCardRepository.findByOwner(
                principal.getName(),
                PageRequest.of(
                        pageable.getPageNumber(),
                        pageable.getPageSize(),
                        pageable.getSortOr(Sort.by(Sort.Direction.ASC, "amount"))
                ));
        return ResponseEntity.ok(page.getContent());
    }

    @PutMapping("/{requestedId}")
    public ResponseEntity<Void> putCashCard(@RequestBody CashCard request, @PathVariable Long requestedId, UriComponentsBuilder ucb,  Principal principal) {
        CashCard response =findCashCard(requestedId, principal);
        if(response == null) {
            return ResponseEntity.notFound().build();
        } else {
            CashCard newCashCard = new CashCard(requestedId, request.amount(), principal.getName());
            cashCardRepository.save(newCashCard);
            return ResponseEntity.noContent().build();
        }
    }

    @DeleteMapping("/{requestedId}")
    public ResponseEntity<Void> deleteCashCard(@PathVariable Long requestedId, Principal principal) {
        CashCard response  = findCashCard(requestedId, principal);
        if(response == null) {
            return ResponseEntity.notFound().build();
        } else {
            cashCardRepository.delete(response);
            return ResponseEntity.noContent().build();
        }
    }


}
