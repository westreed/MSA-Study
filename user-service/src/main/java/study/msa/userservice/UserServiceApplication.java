package study.msa.userservice;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import study.msa.userservice.provider.LLMFeignProvider;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@RestController
public class UserServiceApplication {

	private final LLMFeignProvider llmFeignProvider;

	public UserServiceApplication(LLMFeignProvider llmFeignProvider) {
		this.llmFeignProvider = llmFeignProvider;
	}

	public static void main(String[] args) {
		SpringApplication.run(UserServiceApplication.class, args);
	}

	@GetMapping("/info")
	public String info(@Value("${server.port}") String port) {
		String llm_test = llmFeignProvider.test();
		return "User 서비스의 기본 동작 Port: {" + port + "}\nLLM Test: {" + llm_test + "}";
	}

	@GetMapping("/test/{test_value}")
	public String test(@PathVariable("test_value") int testValue) {
		String result = "Test API Call + PathVariable : " + testValue;
		if(testValue < 1000){
			return result + " ( 1000미만 )";
		}
		else {
			return result + "( 1000이상 )";
		}
	}
}
