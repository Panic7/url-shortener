package com.flex.url_shortener.service.shortlink;

import com.flex.url_shortener.repository.ShortLinkRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ShortLinkMetricService {
    private final ShortLinkRepository shortLinkRepository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void incrementClickCount(String shortCode) {
        shortLinkRepository.incrementClickCount(shortCode);
    }

}
