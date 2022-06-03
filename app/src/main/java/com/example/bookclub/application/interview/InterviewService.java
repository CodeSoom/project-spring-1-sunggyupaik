package com.example.bookclub.application.interview;

import com.example.bookclub.domain.interview.Interview;
import com.example.bookclub.dto.InterviewDto;
import com.example.bookclub.infrastructure.interview.JpaInterviewRepository;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * 인터뷰 크롤링, 조회, 리스트 조회를 한다.
 */
@Service
@Transactional
public class InterviewService {
    private final JpaInterviewRepository interviewRepository;

    public InterviewService(JpaInterviewRepository interviewRepository) {
        this.interviewRepository = interviewRepository;
    }

    /**
     * 모든 인터뷰를 크롤링해서 반환한다.
     * 교보문고 인터뷰를 크롤링한다.
     *
     * @return 크롤링한 인터뷰 리스트
     */
    public List<Interview> crawlAllInterviews() {
        List<Interview> list = new ArrayList<>();
        boolean isLastPage = false;
        try {
            int page = 1;
            while (true) {
                String url = "https://www.kyobobook.co.kr/author/info/AuthorInterViewMore.laf?perPage=20&targetPage=" + page;
                Connection conn = Jsoup.connect(url);
                Document doc = conn.get();

                Elements interviewsLiElements = doc.select(".list_author_interview > li");
                if (isLastPage && interviewsLiElements.size() == 1) {
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

                    Optional<Interview> savedInterview = interviewRepository.findByTitle(title);
                    if(savedInterview.isPresent()) continue;

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

                if (interviewsLiElements.size() != 20) {
                    isLastPage = true;
                }
                page++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 23시 50분마다 새로운 인터뷰를 크롤링하고 반환한다.
     * 교보문고 인터뷰를 크롤링한다.
     *
     * @return 크롤링한 인터뷰
     */
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

                if (LocalDate.parse(date, DateTimeFormatter.ISO_DATE).isEqual(LocalDate.now())) {
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

    /**
     * 주어진 검색어와 페이징 정보로 인터뷰 정보를 조회하고 반환한다.
     *
     * @param search 검색어 식별자
     * @param pageable 페이징 정보
     * @return 조회된 인터뷰 페이징 리스트 정보
     */
    public Page<InterviewDto.InterviewResultDto> getInterviews(String search, Pageable pageable) {
        return interviewRepository.findAllContainsTileOrContent(search, pageable);
    }

    /**
     * 주어진 페이징 정보로 인터뷰 리스트를 조회하고 반환한다.
     *
     * @param pageable 페이징 정보
     * @return 조회된 인터뷰 페이징 리스트 정보
     */
    public Page<InterviewDto.InterviewResultDto> getAllInterviews(Pageable pageable) {
        return interviewRepository.findAll(pageable);
    }
}
