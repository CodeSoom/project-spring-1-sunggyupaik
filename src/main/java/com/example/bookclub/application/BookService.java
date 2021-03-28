package com.example.bookclub.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

@Service
public class BookService {
    @Value("${interpark.apikey}")
    private String apikey;

    public URL getBestSellerUrl() throws MalformedURLException {
        String address = "http://book.interpark.com/api/bestSeller.api?key=" +
                apikey + "&categoryId=122&output=json";
        return new URL(address);
    }

    public String getBestSellers() throws IOException {
        URL url = getBestSellerUrl();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }
            return sb.toString();
        }
    }
}
