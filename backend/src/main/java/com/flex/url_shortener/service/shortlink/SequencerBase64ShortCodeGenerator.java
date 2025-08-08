package com.flex.url_shortener.service.shortlink;

import com.flex.url_shortener.common.Base58Encoder;
import org.springframework.stereotype.Service;

@Service
public class SequencerBase64ShortCodeGenerator implements ShortCodeGenerator {

    @Override
    public String generateShortCode(long id) {
        if (id < 1_000_000_000) {
            throw new IllegalArgumentException(
                    "Id must be at least 1,000,000,000 to ensure uniqueness and 6 symbols length in Base58 encoding.");
        }

        return Base58Encoder.encodeBase58(id);
    }
}
