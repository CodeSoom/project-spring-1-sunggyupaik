package com.example.bookclub.application;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

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
}
