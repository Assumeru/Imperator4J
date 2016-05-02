package com.ee.imperator.api;

public interface RequestHandler<Input, Output> {
	Output handle(Input input);
}
