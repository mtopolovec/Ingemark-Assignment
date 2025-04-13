package com.ingemark.assignment.assignment.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.ingemark.assignment.assignment.deserializer.HnbStringToBigDecimalDeserializer;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
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

    public HnbResponse(String currency, BigDecimal buyingRate, BigDecimal middleRate, BigDecimal sellingRate) {
        this.currency = currency;
        this.buyingRate = buyingRate;
        this.middleRate = middleRate;
        this.sellingRate = sellingRate;
    }
}
