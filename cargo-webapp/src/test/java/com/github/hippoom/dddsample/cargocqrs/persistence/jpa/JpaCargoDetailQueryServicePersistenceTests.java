package com.github.hippoom.dddsample.cargocqrs.persistence.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;

import com.github.hippoom.dddsample.cargocqrs.persistence.CargoDetailDao;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;
import com.github.springtestdbunit.DbUnitTestExecutionListener;
import com.github.springtestdbunit.annotation.DatabaseSetup;
import com.github.springtestdbunit.annotation.ExpectedDatabase;
import com.github.springtestdbunit.assertion.DatabaseAssertionMode;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath:persistence.xml",
		"classpath:config.xml", "classpath:dbdeploy.xml" })
@TestExecutionListeners({ DependencyInjectionTestExecutionListener.class,
		DirtiesContextTestExecutionListener.class,
		TransactionalTestExecutionListener.class,
		DbUnitTestExecutionListener.class })
public class JpaCargoDetailQueryServicePersistenceTests {

	@Autowired
	private CargoDetailDao dao;

	@DatabaseSetup(value = "classpath:cargo_detail_rdbms_save_fixture.xml")
	@ExpectedDatabase(value = "classpath:cargo_detail_rdbms_save_expect.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@Test
	public void orderInserted() throws Throwable {
		CargoDto prototype = dao.findBy("2");

		CargoDto copy = copy(prototype, "3");

		dao.save(copy);
	}

	private CargoDto copy(CargoDto prototype, String copyId) {
		ModelMapper mapper = new ModelMapper();
		CargoDto copy = mapper.map(prototype, CargoDto.class);
		copy.setTrackingId(copyId);
		return copy;
	}
}
