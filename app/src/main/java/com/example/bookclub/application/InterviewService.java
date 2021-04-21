package com.example.bookclub.application;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class InterviewService {
    public void crawlAllInterviews() {
        List<Map<String, String>> list = new ArrayList<>();
        try {
            int page = 1;
            while(true) {
                String url = "https://www.kyobobook.co.kr/author/info/AuthorInterViewMore.laf?perPage=20&targetPage=" + page;
                Connection conn = Jsoup.connect(url);
                Document doc = conn.get();

                Elements interviewsLiElements = doc.select(".list_author_interview > li");
                for(Element interview : interviewsLiElements) {
                    Element photoChildElement = interview.getElementsByClass("photo").first().child(0);
                    String boardId = photoChildElement.attr("href").split(",")[1].trim();;
                    String interviewUrl = "http://news.kyobobook.co.kr/people/interviewView.ink?orderclick=&sntn_id=" + boardId.substring(1, boardId.length()-1);
                    String imgUrl = interview.select(".photo a img").attr("src").trim();
                    String author = interview.select(".author a").text().trim();
                    String title = interview.select(".title a").text().trim();
                    String date = interview.select(".info").text().split("\\|")[0].trim();
                    String content = interview.select(".detail").text().split("더보기")[0];

                    Map<String, String> map = new HashMap<>();
                    map.put("interviewUrl", interviewUrl);
                    map.put("imgUrl", imgUrl);
                    map.put("author", author);
                    map.put("title", title);
                    map.put("date", date);
                    map.put("content", content);
                    list.add(map);
                }

                if(interviewsLiElements.size() != 20) {
                    break;
                }
                page++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
