package com.github.hippoom.dddsample.cargo.core;

public interface AggregateIdentifierGenerator<T> {

	T nextIdentifier();

}
