package com.yamada.utils;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import com.yamada.models.Currency;
import java.util.List;
import java.util.Scanner;

public class ApiConnector {

    private final static String KEY = "f87358f945dd54a6227a002a";
    private final static String API = "https://v6.exchangerate-api.com/v6/";

    private static ApiConnector connector;

    private ApiConnector() { }

    public static ApiConnector getInstance() {
        if(connector == null) {
            connector = new ApiConnector();
        }
        return connector;
    }

    public List<Currency> getCodes() throws IOException {

        URL url = new URL(API + KEY + "/codes");

        JsonObject jsonObject = getJsonElement(url).getAsJsonObject();

        JsonArray array = jsonObject.get("supported_codes").getAsJsonArray();

        List<Currency> currencies = new ArrayList<>();
        array.forEach(element -> {
            JsonArray currencyElements = element.getAsJsonArray();

            currencies.add(new Currency(currencyElements.get(0).getAsString(),
                    currencyElements.get(1).getAsString()));

        });

        return currencies;
    }

    public double getConversionResult(String from, String to, String amount) throws IOException {
        URL url = new URL(API + KEY + "/pair/" + from + "/" + to + "/" + amount);

        JsonObject jsonObject = getJsonElement(url).getAsJsonObject();

        return jsonObject.get("conversion_result").getAsDouble();
    }

    public JsonElement getJsonElement(URL url) throws IOException {
        HttpURLConnection request = (HttpURLConnection) url.openConnection();
        request.connect();

        Scanner scanner = new Scanner((InputStream) request.getContent());
        StringBuilder content = new StringBuilder();
        while(scanner.hasNext()) {
            content.append(scanner.nextLine());
        }

        return JsonParser.parseString(content.toString());
    }
}
