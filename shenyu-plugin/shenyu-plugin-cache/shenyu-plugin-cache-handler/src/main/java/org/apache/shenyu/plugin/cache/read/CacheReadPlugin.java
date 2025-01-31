/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.shenyu.plugin.cache.read;

import org.apache.shenyu.common.enums.PluginEnum;
import org.apache.shenyu.plugin.api.ShenyuPlugin;
import org.apache.shenyu.plugin.api.ShenyuPluginChain;
import org.apache.shenyu.plugin.cache.ICache;
import org.apache.shenyu.plugin.cache.utils.CacheUtils;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Objects;

/**
 * CacheReadPlugin.
 */
public class CacheReadPlugin implements ShenyuPlugin {

    @Override
    public Mono<Void> execute(final ServerWebExchange exchange, final ShenyuPluginChain chain) {
        ICache cache = CacheUtils.getCache();
        byte[] bytes;
        if (Objects.nonNull(cache) && Objects.nonNull(bytes = cache.getData(CacheUtils.dataKey(exchange)))) {
            exchange.getResponse().getHeaders().setContentType(cache.getContentType(CacheUtils.contentTypeKey(exchange)));
            return exchange.getResponse().writeWith(Mono.just(exchange.getResponse()
                            .bufferFactory().wrap(bytes))
                    .doOnNext(data -> exchange.getResponse().getHeaders().setContentLength(data.readableByteCount())));
        }
        return chain.execute(exchange);
    }

    @Override
    public int getOrder() {
        return PluginEnum.CACHE_READ.getCode();
    }

    @Override
    public String named() {
        return PluginEnum.CACHE_READ.getName();
    }
}
