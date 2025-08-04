package com.flex.url_shortener.event;

import com.flex.url_shortener.service.shortlink.ShortLinkMetricService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionalEventListener;


@Slf4j
@RequiredArgsConstructor
@Component
public class ShortLinkEventListener {
    private final ShortLinkMetricService shortLinkMetricService;

    /**
     * This method is triggered after a successful transaction to increment the click count for a short link.
     * It listens for the {@link OriginalLinkAccessed} event and updates the click count accordingly.
     *
     * @param event The {@link OriginalLinkAccessed} event containing the {@link com.flex.url_shortener.entity.ShortLink}.
     */
    @TransactionalEventListener
    public void captureLinkAccessedMetrics(OriginalLinkAccessed event) {
        log.debug("Transactional Event After Commit triggered, incrementing click count for short link: {}",
                event.shortLink().getShortCode());
        shortLinkMetricService.incrementClickCount(event.shortLink().getShortCode());
    }

}
