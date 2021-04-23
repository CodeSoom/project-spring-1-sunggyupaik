package com.example.bookclub.application;

import com.example.bookclub.domain.Interview;
import com.example.bookclub.domain.InterviewRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class InterviewService {
    private final InterviewRepository interviewRepository;

    public InterviewService(InterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    public List<Interview> crawlAllInterviews() {
        List<Interview> list = new ArrayList<>();
        boolean isLastPage = false;
        try {
            int page = 1;
            while(true) {
                String url = "https://www.kyobobook.co.kr/author/info/AuthorInterViewMore.laf?perPage=20&targetPage=" + page;
                Connection conn = Jsoup.connect(url);
                Document doc = conn.get();

                Elements interviewsLiElements = doc.select(".list_author_interview > li");
                if(isLastPage && interviewsLiElements.size() == 1) {
                    break;
                }

                for(Element interviewElement : interviewsLiElements) {
                    Element photoChildElement = interviewElement.getElementsByClass("photo").first().child(0);
                    String boardId = photoChildElement.attr("href").split(",")[1].trim();;
                    String interviewUrl = "http://news.kyobobook.co.kr/people/interviewView.ink?orderclick=&sntn_id=" + boardId.substring(1, boardId.length()-1);
                    String imgUrl = interviewElement.select(".photo a img").attr("src").trim();
                    String author = interviewElement.select(".author a").text().trim();
                    String title = interviewElement.select(".title a").text().trim();
                    String date = interviewElement.select(".info").text().split("\\|")[0].trim();
                    String content = interviewElement.select(".detail").text().split("더보기")[0];

                    Interview interview = Interview.builder()
                            .interviewUrl(interviewUrl)
                            .imgUrl(imgUrl)
                            .author(author)
                            .title(title)
                            .date(LocalDate.parse(date, DateTimeFormatter.ISO_DATE))
                            .content(content)
                            .build();
                    interviewRepository.save(interview);
                    list.add(interview);
                }

                if(interviewsLiElements.size() != 20) {
                    isLastPage = true;
                }
                page++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    @Scheduled(cron = "0 50 23 * * *")
    public Interview crawlInterview() {
        Interview interview = null;
        try {
            String url = "https://www.kyobobook.co.kr/author/info/AuthorInterViewMore.laf?perPage=20&targetPage=1";
            Connection conn = Jsoup.connect(url);
            Document doc = conn.get();

            Elements interviewsLiElements = doc.select(".list_author_interview > li");

            for(Element interviewElement : interviewsLiElements) {
                Element photoChildElement = interviewElement.getElementsByClass("photo").first().child(0);
                String boardId = photoChildElement.attr("href").split(",")[1].trim();;
                String interviewUrl = "http://news.kyobobook.co.kr/people/interviewView.ink?orderclick=&sntn_id=" + boardId.substring(1, boardId.length()-1);
                String imgUrl = interviewElement.select(".photo a img").attr("src").trim();
                String author = interviewElement.select(".author a").text().trim();
                String title = interviewElement.select(".title a").text().trim();
                String date = interviewElement.select(".info").text().split("\\|")[0].trim();
                String content = interviewElement.select(".detail").text().split("더보기")[0];

                if(LocalDate.parse(date, DateTimeFormatter.ISO_DATE).isEqual(LocalDate.now())) {
                    interview = Interview.builder()
                            .interviewUrl(interviewUrl)
                            .imgUrl(imgUrl)
                            .author(author)
                            .title(title)
                            .date(LocalDate.parse(date, DateTimeFormatter.ISO_DATE))
                            .content(content)
                            .build();
                    interviewRepository.save(interview);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return interview;
    }

    public List<Interview> getInterviews() {
        return interviewRepository.findAll();
    }

    public List<Interview> getNowPageInterviews(int startIndex, int countList) {
        return interviewRepository.findTargetPageInterviews(startIndex, countList);
    }
}
