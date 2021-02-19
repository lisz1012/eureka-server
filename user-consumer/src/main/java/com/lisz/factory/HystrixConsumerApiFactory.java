package com.lisz.factory;

import com.lisz.api.ConsumerApi;
import com.lisz.api.UserApi;
import com.lisz.entity.Person;
import com.lisz.exception.CalculationException;
import feign.FeignException;
import feign.hystrix.FallbackFactory;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

@Component
//@RequestMapping("/fallback/user")
public class HystrixConsumerApiFactory implements FallbackFactory<ConsumerApi> {

	@Override
	public ConsumerApi create(Throwable cause) {
		return new ConsumerApi() {
			@Override
			public Map<Integer, String> getMap(Integer id) {
				System.out.println("getMap failed for ID: " + id);
				return null;
			}

			@Override
			public Map<Integer, String> getMap(Integer id, String name) {
				System.out.println("getMap failed for ID: " + id + " and name " + name);
				return null;
			}

			@Override
			public Map<Integer, String> getMap(Map<String, Object> map) {
				System.out.println("Failed calling getMap for map: " + map);
				return null;
			}

			@Override
			public Map<Integer, String> postMap(Map<String, Object> map) {
				System.out.println("Failed calling postMap for map: " + map);
				return null;
			}

			@Override
			public String helloZuul() {
				return "Hello, downgraded zuul";
			}

			@Override
			public String alive() {
				System.out.println(cause);
				System.out.println(ToStringBuilder.reflectionToString(cause, ToStringStyle.MULTI_LINE_STYLE));
				cause.printStackTrace();
				if (cause instanceof FeignException.InternalServerError) {
					return "不赖我，我所调用的服务报了500错误." + cause.getLocalizedMessage();
				}
				if (cause instanceof CalculationException) {
					return "Code: " + CalculationException.CODE + " error message: " + cause.getMessage();
				}
				return "alive API call failed";
			}

			@Override
			public String getById(Integer id) {
				return "getById failed for: " + id;
			}

			@Override
			public Person postPerson(Person person) {
				System.out.println("postPerson failed for person: " + person);
				return null;
			}
		};
	}
}
