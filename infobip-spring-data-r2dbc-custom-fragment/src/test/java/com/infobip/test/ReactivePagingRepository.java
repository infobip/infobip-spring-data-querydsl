package com.infobip.test;

import reactor.core.publisher.Mono;

public interface ReactivePagingRepository<T> {

    Mono<T> simplePaging(String string);
}
