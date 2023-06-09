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
        Optional<CashCard> cashCardOptional = Optional.ofNullable(cashCardRepository.findByIdAndOwner(requestedId, principal.getName()));
        if (cashCardOptional.isPresent()) {
            return ResponseEntity.ok(cashCardOptional.get());
        } else {
            return ResponseEntity.notFound().build();
        }
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
        CashCard response = cashCardRepository.findByIdAndOwner(requestedId, principal.getName());
        if(response == null) {
            CashCard newCashCard = new CashCard(null , request.amount(), principal.getName());
            cashCardRepository.save(newCashCard);
            URI location = ucb.path("cashcards/{id}")
                    .buildAndExpand(newCashCard.id())
                    .toUri();
            return ResponseEntity.created(location).build();
        } else {
            CashCard newCashCard = new CashCard(requestedId, request.amount(), principal.getName());
            cashCardRepository.save(newCashCard);
            return ResponseEntity.noContent().build();
        }
    }


}
