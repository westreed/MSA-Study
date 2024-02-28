package study.msa.userservice.global.provider;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;

@Component
@FeignClient(name="llm-service")
public interface LLMFeignClient extends LLMFeignProvider {
}
