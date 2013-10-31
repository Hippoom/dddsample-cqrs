package com.github.hippoom.dddsample.cargocqrs.core;

public interface AggregateIdentifierGenerator<T> {

	T nextIdentifier();

}
