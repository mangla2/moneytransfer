package com.revolut.app.exception;

import java.sql.SQLException;

public class InternalServerError extends SQLException {

	private static final long serialVersionUID = 1L;

    public InternalServerError(String message) {
        super(message);

    }
}
