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
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class BookService {
    @Value("${interpark.apikey}")
    private String apikey;

    public URL getBooksUrl(BookType bookType, String keyword)
            throws MalformedURLException, UnsupportedEncodingException {
        String address = "";
        if(!keyword.equals("")) {
            keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
        }

        switch (bookType) {
            case BESTSELLER:
                address = "http://book.interpark.com/api/bestSeller.api?key=" +
                    apikey + "&categoryId=122&output=json";
                break;
            case RECOMMEND:
                address = "http://book.interpark.com/api/recommend.api?key=" +
                    apikey + "&categoryId=122&output=json";
                break;
            case NEW:
                address = "http://book.interpark.com/api/newBook.api?key=" +
                    apikey + "&categoryId=122&output=json";
                break;
            case SEARCH:
                address = "http://book.interpark.com/api/search.api?key=" +
                    apikey + "&query=" + keyword + "&output=json";
                break;
        }
        return new URL(address);
    }

    public JSONArray getBookLists(BookType bookType, String keyword) throws IOException, ParseException {
        URL url = getBooksUrl(bookType, keyword);
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
