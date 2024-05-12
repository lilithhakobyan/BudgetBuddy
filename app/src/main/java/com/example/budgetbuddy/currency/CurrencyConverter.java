package com.example.budgetbuddy.currency;

import android.app.Dialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.budgetbuddy.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class CurrencyConverter extends AppCompatActivity {

    TextView convert_from_dropdown_menu, convert_to_dropdown_menu, conversion_rate;
    EditText edit_amount_to_convert_value;
    ArrayList<String> arraylist;
    Dialog from_dialog, to_dialog;
    Button conversion, exit;
    String convert_from_value, convert_to_value, conversion_value;
    String[] currency = {"AED", "AFN", "ALL", "AMD", "ANG", "AOA", "ARS", "AUD", "AWG", "AZN",
            "BAM", "BBD", "BDT", "BGN", "BHD", "BIF", "BMD", "BND", "BOB", "BRL", "BSD", "BTC", "BTN", "BWP", "BYN", "BYR", "BZD",
            "CAD", "CDF", "CHF", "CLF", "CLP", "CNY", "COP", "CRC", "CUC", "CUP", "CVE", "CZK",
            "DJF", "DKK", "DOP", "DZD",
            "EGP", "ERN", "ETB", "EUR",
            "FJD", "FKP",
            "GBP", "GEL", "GGP", "GHS", "GIP", "GMD", "GNF", "GTQ", "GYD",
            "HKD", "HNL", "HRK", "HTG", "HUF",
            "IDR", "ILS", "IMP", "INR", "IQD", "IRR", "ISK",
            "JEP", "JMD", "JOD", "JPY",
            "KES", "KGS", "KHR", "KMF", "KPW", "KRW", "KWD", "KYD", "KZT",
            "LAK", "LBP", "LKR", "LRD", "LSL", "LVL", "LYD",
            "MAD", "MDL", "MGA", "MKD", "MMK", "MNT", "MOP", "MRO", "MUR", "MVR", "MWK", "MXN", "MYR", "MZN",
            "NAD", "NGN", "NIO", "NOK", "NPR", "NZD",
            "OMR",
            "PAB", "PEN", "PGK", "PHP", "PKR", "PLN", "PYG",
            "QAR",
            "RON", "RSD", "RUB", "RWF",
            "SAR", "SBD", "SCR", "SDG", "SEK", "SGD", "SHP", "SLL", "SOS", "SRD", "STD", "SVC", "SYP", "SZL",
            "THB", "TJS", "TMT", "TND", "TOP", "TRY", "TTD", "TWD", "TZS",
            "UAH", "UGX", "USD", "UYU", "UZS",
            "VEF", "VND", "VUV",
            "WST",
            "XAF", "XAG", "XCD", "XDR", "XOF", "XPF",
            "YER",
            "ZAR", "ZMK", "ZMW", "ZWL"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_currency_converter);

        convert_from_dropdown_menu = findViewById(R.id.convert_from_dropdown_menu);
        convert_to_dropdown_menu = findViewById(R.id.convert_to_dropdown_menu);
        conversion_rate = findViewById(R.id.conversion_rate);
        conversion = findViewById(R.id.conversion);
        exit = findViewById(R.id.exit);

        edit_amount_to_convert_value = findViewById(R.id.edit_amount_to_convert_value);

        arraylist = new ArrayList<>();
        for (String i : currency) {
            arraylist.add(i);
        }

        convert_from_dropdown_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                from_dialog = new Dialog(CurrencyConverter.this);
                from_dialog.setContentView(R.layout.from_spinner);
                from_dialog.getWindow().setLayout(650, 800);
                from_dialog.show();

                EditText edittext = from_dialog.findViewById(R.id.edit_text);
                ListView listview = from_dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CurrencyConverter.this, android.R.layout.simple_list_item_1, arraylist);
                listview.setAdapter(adapter);

                edittext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convert_from_dropdown_menu.setText(adapter.getItem(position));
                        from_dialog.dismiss();
                        convert_from_value = adapter.getItem(position);
                    }
                });
            }
        });

        convert_to_dropdown_menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                to_dialog = new Dialog(CurrencyConverter.this);
                to_dialog.setContentView(R.layout.to_spinner);
                to_dialog.getWindow().setLayout(650, 800);
                to_dialog.show();

                EditText edittext = to_dialog.findViewById(R.id.edit_text);
                ListView listview = to_dialog.findViewById(R.id.list_view);

                ArrayAdapter<String> adapter = new ArrayAdapter<>(CurrencyConverter.this, android.R.layout.simple_list_item_1, arraylist);
                listview.setAdapter(adapter);

                edittext.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {
                    }
                });
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        convert_to_dropdown_menu.setText(adapter.getItem(position));
                        to_dialog.dismiss();
                        convert_to_value = adapter.getItem(position);
                    }
                });
            }
        });

        conversion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String fromCurrency = convert_from_dropdown_menu.getText().toString();
                    String toCurrency = convert_to_dropdown_menu.getText().toString();
                    Double amountToConvert = Double.valueOf(edit_amount_to_convert_value.getText().toString());
                    getExchangeRates(fromCurrency, toCurrency, amountToConvert);
                } catch (Exception e) {
                    e.printStackTrace();
                    handleError("Invalid input");
                }
            }
        });


        exit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CloseCurrencyConverter();
            }
        });
    }

    public void getExchangeRates(String fromCurrency, String toCurrency, final Double amountToConvert) {
        String apiKey = "N0IHQ8QOBHA9EXVG";
        String apiUrl = "https://www.alphavantage.co/query?function=FX_DAILY&from_symbol=" + fromCurrency + "&to_symbol=" + toCurrency + "&apikey=" + apiKey;

        RequestQueue requestQueue = Volley.newRequestQueue(this);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, apiUrl, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d("CurrencyConverter", "API Response: " + response);

                try {
                    // Parse the JSON response to extract exchange rate data
                    JSONObject jsonResponse = new JSONObject(response);
                    JSONObject timeSeriesData = jsonResponse.getJSONObject("Time Series FX (Daily)");

                    String latestDate = timeSeriesData.keys().next();
                    JSONObject latestRateData = timeSeriesData.getJSONObject(latestDate);
                    double exchangeRate = latestRateData.getDouble("4. close");

                    // Perform conversion based on selected currencies and rates
                    double convertedAmount = amountToConvert * exchangeRate;
                    displayConversionResult(convertedAmount);
                } catch (JSONException e) {
                    e.printStackTrace();
                    handleError("Error parsing API response");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
                handleError("Network error occurred");
            }
        });

        requestQueue.add(stringRequest);
    }


    // Method to display conversion result
    private void displayConversionResult(double convertedAmount) {
        conversion_rate.setText(String.valueOf(convertedAmount));
    }

    // Method to handle errors
    private void handleError(String errorMessage) {
        Toast.makeText(this, errorMessage, Toast.LENGTH_SHORT).show();
    }

    // Method to close the activity
    public void CloseCurrencyConverter() {
        finish();
    }
}