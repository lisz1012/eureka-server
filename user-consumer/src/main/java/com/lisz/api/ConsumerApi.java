package com.lisz.api;

import org.springframework.cloud.openfeign.FeignClient;
// 这个类只是提供了一个让Feign介入的机会
@FeignClient(name = "user-provider")
public interface ConsumerApi extends UserApi {
}
