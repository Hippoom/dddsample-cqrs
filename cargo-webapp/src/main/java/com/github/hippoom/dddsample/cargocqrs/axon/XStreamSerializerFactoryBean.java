package com.github.hippoom.dddsample.cargocqrs.axon;

import org.axonframework.serializer.xml.XStreamSerializer;
import org.springframework.beans.factory.config.AbstractFactoryBean;

public class XStreamSerializerFactoryBean extends
		AbstractFactoryBean<XStreamSerializer> {

	@Override
	protected XStreamSerializer createInstance() throws Exception {
		XStreamSerializer object = new XStreamSerializer();
		object.getXStream().aliasPackage("cargocqrs",
				"com.github.hippoom.dddsample.cargocqrs");
		return object;
	}

	@Override
	public Class<?> getObjectType() {
		return XStreamSerializer.class;
	}

}
