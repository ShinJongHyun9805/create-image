package com.mcp.image.dto.res;

import lombok.Data;

import java.util.List;

@Data
public class CrawlingDataResponse {

    private int rank;   // 순위

    private List<String> keywords;      // 키워드 목록

    private List<String> imageUrls;     // 이미지 목록
}
