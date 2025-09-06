package com.operationHelper.errorHandler;

import java.util.Collections;
import java.util.List;

public class InvalidParameters extends RuntimeException {
   
	private static final long serialVersionUID = 1L;
	private final List<String> errors;

    public InvalidParameters(String message) {
        super(message);
        this.errors = List.of(message);
    }

    public InvalidParameters(List<String> errors) {
        super("Invalid parameters: " + String.join("; ", errors));
        this.errors = List.copyOf(errors);
    }

    public List<String> getErrors() {
        return Collections.unmodifiableList(errors);
    }
}
