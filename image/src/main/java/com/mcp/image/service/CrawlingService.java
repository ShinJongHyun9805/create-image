package com.mcp.image.service;

import com.mcp.image.dto.res.CrawlingDataResponse;
import com.mcp.image.manager.CrawlingManger;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CrawlingService {

    private final CrawlingManger crawlingManger;

    public void insertCrawlingData() {

        try {
            List<CrawlingDataResponse> crawledResult = crawlingManger.crawling();
            System.out.println("crawledResult = " + crawledResult);


        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }
}


