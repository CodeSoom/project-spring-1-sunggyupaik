package com.example.bookclub.utils;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class PageUtil {
    private int targetPage;
    private int previous;
    private List<Integer> pageNumberList;
    private int next;
    private int totalPage;
    private int firstPage;
    private int lastPage;

    public PageUtil makePage(int allListsCount,
                             int targetPage,
                             int countList,
                             int countPage) {
        this.targetPage = targetPage;
        calcTotalPage(allListsCount, countList);
        calcFirstPage(targetPage, countPage);
        calcLastPage(countPage);
        calcPrevious();
        calcNext();
        calcPageNumberList();

        return this;
    }

    public void calcTotalPage(int allListsCount, int countList) {
        this.totalPage = allListsCount / countList;
        if(allListsCount % countList > 0) {
            this.totalPage += 1;
        }
    }

    public void calcFirstPage(int targetPage, int countPage) {
        this.firstPage = (((targetPage - 1) - 1) / countPage) * countPage + 1;
    }

    public void calcLastPage(int countPage) {
        this.lastPage = this.firstPage + countPage - 1;
        if(lastPage > totalPage) {
            lastPage = totalPage;
        }
    }

    public void calcPrevious() {
        this.previous = this.firstPage - 1;
        if(this.previous < 1) {
            this.previous = 1;
        }
    }

    public void calcNext() {
        this.next = this.lastPage + 1;
    }

    public void calcPageNumberList() {
        this.pageNumberList = new ArrayList<>();
        for(int i=firstPage; i<=lastPage; i++) {
            this.pageNumberList.add(i);
        }
    }

    @Override
    public String toString() {
        return "PageUtil{" +
                "previous=" + previous +
                ", pageNumberList=" + pageNumberList +
                ", next=" + next +
                ", totalPage=" + totalPage +
                ", firstPage=" + firstPage +
                ", lastPage=" + lastPage +
                '}';
    }
}
