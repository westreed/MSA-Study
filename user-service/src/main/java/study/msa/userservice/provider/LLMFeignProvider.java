package study.msa.userservice.provider;

import org.springframework.web.bind.annotation.GetMapping;

public interface LLMFeignProvider {
    @GetMapping("/test")
    String test();
}
