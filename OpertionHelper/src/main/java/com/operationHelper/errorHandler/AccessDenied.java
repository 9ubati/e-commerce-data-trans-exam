package com.operationHelper.errorHandler;

public class AccessDenied extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public AccessDenied(String message) {
		super(message);
	}
}
