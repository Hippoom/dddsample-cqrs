package com.github.hippoom.dddsample.cargocqrs.persistence.jpa;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.test.context.transaction.TransactionalTestExecutionListener;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.TransactionCallback;
import org.springframework.transaction.support.TransactionTemplate;

import com.github.hippoom.dddsample.cargocqrs.persistence.CargoDetailDao;
import com.github.hippoom.dddsample.cargocqrs.rest.CargoDto;
import com.github.hippoom.dddsample.cargocqrs.rest.LegDto;
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

	@Autowired
	private PlatformTransactionManager transactionManager;

	@DatabaseSetup(value = "classpath:cargo_detail_rdbms_save_fixture.xml")
	@ExpectedDatabase(value = "classpath:cargo_detail_rdbms_save_expect.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@Test
	public void orderInserted() throws Throwable {
		CargoDto prototype = dao.findBy("2");

		CargoDto copy = copy(prototype, "3");

		dao.store(copy);
	}

	@DatabaseSetup(value = "classpath:cargo_detail_rdbms_update_fixture.xml")
	@ExpectedDatabase(value = "classpath:cargo_detail_rdbms_update_expect.xml", assertionMode = DatabaseAssertionMode.NON_STRICT)
	@Test
	public void orderUpdated() throws Throwable {
		new TransactionTemplate(transactionManager)
				.execute(new TransactionCallback<Object>() {

					@Override
					public Object doInTransaction(TransactionStatus arg0) {
						CargoDto prototype = dao.findBy("4");
						CargoDto copy = dao.findBy("5");

						copy(prototype, copy);

						System.err.println(copy.getLegs());

						dao.store(copy);
						return null;
					}

				});

	}

	private void copy(CargoDto prototype, CargoDto copy) {
		ModelMapper mapper = new ModelMapper();
		mapper.createTypeMap(CargoDto.class, CargoDto.class);
		mapper.createTypeMap(LegDto.class, LegDto.class);
		mapper.addMappings(new PropertyMap<CargoDto, CargoDto>() {

			@Override
			protected void configure() {
				skip().setTrackingId(null);
			}

		});

		mapper.map(prototype, copy);

	}

	private CargoDto copy(CargoDto prototype, String copyId) {
		ModelMapper mapper = new ModelMapper();
		CargoDto copy = mapper.map(prototype, CargoDto.class);
		copy.setTrackingId(copyId);
		return copy;
	}
}
