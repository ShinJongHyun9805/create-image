package com.mcp.image.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class CrowedKeywordEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long keywordId;

    @Column(nullable = false, length = 36)
    private String crawlingId;                       // UUID (크롤링 묶음 키)

    @Column(nullable = false)
    private String keyword;

    @Column(nullable = false)
    private int rank;

    @Column(nullable = false)
    private LocalDate collectedAt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "batch_id", nullable = false)
//    private BatchHistory batchHistory;
}
