package com.example.movieapp.rest;

import com.example.movieapp.model.request.UpsertCountryRequest;
import com.example.movieapp.service.CountryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/admin/countries")
@RequiredArgsConstructor
public class CountryResource {
    private final CountryService countryService;

    @GetMapping
    public ResponseEntity<?> getAllCountrys() {
        return ResponseEntity.ok(countryService.getAllCountries());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCountryById(@PathVariable Integer id) {
        return ResponseEntity.ok(countryService.getCountryById(id));
    }

    @PostMapping
    public ResponseEntity<?> createCountry(@Valid @RequestBody UpsertCountryRequest request) {
        return new ResponseEntity<>(countryService.saveCountry(request), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateCountry(@PathVariable Integer id, @Valid @RequestBody UpsertCountryRequest request) {
        return ResponseEntity.ok(countryService.updateCountry(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCountry(@PathVariable Integer id) {
        countryService.deleteCountry(id);
        return ResponseEntity.noContent().build();
    }
}
