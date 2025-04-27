package com.mcp.image.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Entity
@Data
public class CrawledImageEntity extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long imageId;

    @Column(nullable = false, length = 36)
    private String crawlingId;                       // UUID (크롤링 묶음 키)

    @Column(length = 1000, nullable = false)
    private String imageUrl;

    private String siteName;

    @Column(nullable = false)
    private int rank;

    @Column(nullable = false)
    private LocalDate collectedAt;

//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "batch_id", nullable = false)
//    private BatchHistory batchHistory;
}
