package com.interview.order;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.interview.order.model.Order;
import com.interview.order.model.Status;
import com.interview.order.repository.OrderRepository;
import com.interview.order.repository.StatusRepository;
import com.interview.order.service.OrderManager;

@SpringBootTest
class OrderManagerTest {
	private static final Logger log = LoggerFactory.getLogger(OrderManagerTest.class);
	@MockBean
	private OrderRepository orderRepository;
	@Autowired
	private StatusRepository statusRepository;
	@Autowired
	private OrderManager orderManager;

	@Test
	void testCreateOrder_when_ChefAvailable() {
		Status inProgressStatus = statusRepository.findByName("in-progress");
		when(orderRepository.countByStatus(inProgressStatus.getId())).thenReturn(2);
		Order order = orderManager.createOrder("California Roll");
		assertThat(order.getStatus_id()).isEqualTo(inProgressStatus.getId());
	}

	@Test
	void testCreateOrder_when_ChefUnAvailable() {
		Status createdStatus = statusRepository.findByName("created");
		Status inProgressStatus = statusRepository.findByName("in-progress");
		when(orderRepository.countByStatus(inProgressStatus.getId())).thenReturn(3);
		Order order = orderManager.createOrder("California Roll");
		assertThat(order.getStatus_id()).isEqualTo(createdStatus.getId());
	}
}
