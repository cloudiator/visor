package de.uniulm.omi.monitoring.probes.impl;

/**
 * Created by daniel on 24.09.14.
 */
public class MetricNotAvailableException extends Exception{

    public MetricNotAvailableException(String message) {
        super(message);
    }

    public MetricNotAvailableException(String message, Throwable cause) {
        super(message, cause);
    }

    public MetricNotAvailableException(Throwable cause) {
        super(cause);
    }

    public MetricNotAvailableException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public MetricNotAvailableException() {
    }
}
