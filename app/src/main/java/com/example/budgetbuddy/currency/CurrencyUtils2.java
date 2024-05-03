package com.example.budgetbuddy.currency;

import android.content.Context;
import android.text.TextUtils;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import org.joda.money.CurrencyUnit;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CurrencyUtils2 {



    private static final String[] PRIORITY_CURRENCIES = {"AMD", "USD", "EUR"};

    public static List<String> fetchCurrencies() {
        List<String> currencies = new ArrayList<>();
        currencies.add("Select currency");

        for (CurrencyUnit currencyUnit : CurrencyUnit.registeredCurrencies()) {
            currencies.add(currencyUnit.getCode());
        }

        Collections.sort(currencies.subList(1, currencies.size()), (currency1, currency2) -> {
            if (isPriorityCurrency(currency1) && !isPriorityCurrency(currency2)) {
                return -1;
            } else if (!isPriorityCurrency(currency1) && isPriorityCurrency(currency2)) {
                return 1;
            } else {
                return currency1.compareTo(currency2);
            }
        });

        return currencies;
    }

    public static void populateCurrencySpinner(Context context, Spinner spinner, String searchQuery) {
        List<String> currencies = fetchCurrencies();
        List<String> filteredCurrencies = new ArrayList<>();

        if (!TextUtils.isEmpty(searchQuery)) {
            for (String currency : currencies) {
                if (currency.toLowerCase().contains(searchQuery.toLowerCase())) {
                    filteredCurrencies.add(currency);
                }
            }
        } else {
            filteredCurrencies.addAll(currencies);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, filteredCurrencies);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);
    }

    private static boolean isPriorityCurrency(String currency) {
        for (String priorityCurrency : PRIORITY_CURRENCIES) {
            if (priorityCurrency.equals(currency)) {
                return true;
            }
        }
        return false;
    }


}
