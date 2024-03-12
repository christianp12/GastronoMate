package it.unipi.lsmd.gastronomate.dto;

import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.awt.print.PageFormat;
import java.awt.print.Pageable;
import java.awt.print.Printable;
import java.util.List;

@Data
@NoArgsConstructor
public class PageDTO<T> {
    @Getter
    private static final int PAGE_SIZE = 12;
    private List<T> entries;
    private int totalCount;
    private int currentPage;
    private int numberOfPages;

    public PageDTO(List<T> entries, int totalCount, int currentPage) {
        this.entries = entries;
        this.totalCount = totalCount;
        this.currentPage = currentPage;
    }

    public int getNumberOfPages() {
        return (int) Math.ceil((double) totalCount / PAGE_SIZE);
    }
}