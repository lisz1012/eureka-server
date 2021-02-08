package com.lisz.service;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Service;

@Service
public class HealthStatusService implements HealthIndicator {
	private Boolean status = true;

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public Health health() {
		if (status) {
			return new Health.Builder().up().build();
		}
		// 代码里有某种异常，可以在代码里面把服务自身下线.称病不朝(社死)，其实真有病吗？不一定，可能业务上不必被Eureka发现.
		// 注意，这里只是Eureka发现不了当前执行下面代码的机器了，但是对方如果不通过Eureka服务列表，而是拿着IP地址直接调用，则还是能
		// 访问到这一台机器上的服务。这不同于http://192.168.1.102:82/actuator/shutdown 手动关闭某台机器的情况，后者会直接停掉Springboot
		return new Health.Builder().down().build();
	}

	public Boolean getStatus() {
		return status;
	}
}
