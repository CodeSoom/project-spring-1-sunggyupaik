package com.example.bookclub.application;

import com.example.bookclub.domain.BookType;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
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

    public URL getBooksUrl(BookType bookType) throws MalformedURLException {
        String address = "";
        switch (bookType) {
            case BESTSELLER -> address = "http://book.interpark.com/api/bestSeller.api?key=" +
                    apikey + "&categoryId=122&output=json";
            case RECOMMEND -> address = "http://book.interpark.com/api/recommend.api?key=" +
                    apikey + "&categoryId=122&output=json";
            case NEW -> address = "http://book.interpark.com/api/newBook.api?key=" +
                    apikey + "&categoryId=122&output=json";
        }
        return new URL(address);
    }

    public JSONArray getBookLists(BookType bookType) throws IOException, ParseException {
        URL url = getBooksUrl(bookType); ;
        String stringValue;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(url.openStream()))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = br.readLine()) != null) {
                sb.append(line);
            }

            stringValue = sb.toString();
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(stringValue);

            return (JSONArray) jsonObject.get("item");
        }
    }
}
