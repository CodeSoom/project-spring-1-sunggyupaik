package com.example.bookclub.application.interview;

import com.example.bookclub.domain.study.BookType;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * 책 검색, URL 조회를 한다.
 */
@Service
public class BookService {
    @Value("${interpark.apikey}")
    private String apikey;

    /**
     * 주어진 책 타입과 검색어로 책 리스트를 반환한다.
     *
     * @param bookType 책 타입 식별자
     * @param keyword 책 검색 식별자
     * @return 조회한 책 리스트 정보
     */
    public JSONArray getBookLists(BookType bookType, String keyword) {
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
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * 주어진 책 타입과 검색어로 URL을 반환한다.
     * 인터파크의 API를 이용해서 검색한다.
     *
     * @param bookType 책 타입 식별자
     * @param keyword 책 검색 식별자
     * @return 검색하려는 URL 주소
     */
    public URL getBooksUrl(BookType bookType, String keyword) {
        String address = "";

        switch (bookType) {
            case BESTSELLER:
                address = "https://book.interpark.com/api/bestSeller.api?key=" +
                        apikey + "&categoryId=122&output=json";
                break;
            case RECOMMEND:
                address = "https://book.interpark.com/api/recommend.api?key=" +
                        apikey + "&categoryId=122&output=json";
                break;
            case NEW:
                address = "https://book.interpark.com/api/newBook.api?key=" +
                        apikey + "&categoryId=122&output=json";
                break;
            case SEARCH:
                keyword = URLEncoder.encode(keyword, StandardCharsets.UTF_8);
                address = "https://book.interpark.com/api/search.api?key=" +
                        apikey + "&query=" + keyword + "&output=json";
                break;
        }

        URL url = null;
        try {
            url = new URL(address);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
