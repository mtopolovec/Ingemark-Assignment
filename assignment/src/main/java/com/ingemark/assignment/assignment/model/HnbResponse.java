package com.ingemark.assignment.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ingemark.assignment.assignment.deserializer.HnbStringToBigDecimalDeserializer;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class HnbResponse {
    @JsonProperty("broj_tecajnice")
    private String rateNumber;

    @JsonProperty("datum_primjene")
    private String applicationDate;

    @JsonProperty("drzava")
    private String country;

    @JsonProperty("drzava_iso")
    private String countryIso;

    @JsonProperty("kupovni_tecaj")
    @JsonDeserialize(using = HnbStringToBigDecimalDeserializer.class)
    private BigDecimal buyingRate;

    @JsonProperty("prodajni_tecaj")
    @JsonDeserialize(using = HnbStringToBigDecimalDeserializer.class)
    private BigDecimal sellingRate;

    @JsonProperty("sifra_valute")
    private String currencyCode;

    @JsonProperty("srednji_tecaj")
    @JsonDeserialize(using = HnbStringToBigDecimalDeserializer.class)
    private BigDecimal middleRate;

    @JsonProperty("valuta")
    private String currency;

}
