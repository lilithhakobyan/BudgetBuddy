package com.example.budgetbuddy;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class CurrencyUtils {

    // Method to fetch currency data from ExchangeRate-API
// Method to fetch currency data from ExchangeRate-API
    public static void fetchCurrencies(Context context, CurrencyFetchListener listener) {
        OkHttpClient client = new OkHttpClient();

        // Replace "YOUR_API_KEY" with your actual ExchangeRate-API key
        String apiKey = "3ac52919a6-43e23b931c-sc73tb";

        Request request = new Request.Builder()
                .url("https://api.fastforex.io/currencies?api_key=" + apiKey)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                if (context != null) {
                    Toast.makeText(context, "Failed to fetch currencies", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    String responseData = response.body().string();
                    List<String> currencies = parseCurrencyResponse(responseData);

                    // Update the spinner adapter on the main/UI thread
                    Handler handler = new Handler(Looper.getMainLooper());
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            listener.onCurrencyFetchSuccess(currencies);
                        }
                    });
                } else {
                    if (context != null) {
                        Toast.makeText(context, "Failed to fetch currencies: " + response.message(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }


    // Method to parse currency data from the API response
    private static List<String> parseCurrencyResponse(String responseData) {
        List<String> currencies = new ArrayList<>();
        try {
            JSONObject jsonObject = new JSONObject(responseData);
            JSONArray currenciesArray = jsonObject.getJSONArray("results");
            for (int i = 0; i < currenciesArray.length(); i++) {
                JSONObject currencyObject = currenciesArray.getJSONObject(i);
                String currency = currencyObject.getString("id");
                currencies.add(currency);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return currencies;
    }

    public interface CurrencyFetchListener {
        void onCurrencyFetchSuccess(List<String> currencies);
    }
}
