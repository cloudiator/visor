package de.uniulm.omi.monitoring.server;

/**
 * Created by daniel on 22.09.14.
 */
public class IllegalRequestException extends Exception {

    public IllegalRequestException(String message) {
        super(message);
    }

    public IllegalRequestException(String message, Throwable e) {
        super(message,e);
    }

}
