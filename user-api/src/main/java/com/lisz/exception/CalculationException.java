package com.lisz.exception;

public class CalculationException extends RuntimeException {
	public static final int CODE = 10001;

	public CalculationException(String message) {
		super(message);
	}
}
