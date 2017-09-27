/**
 *
 */
package com.joybean.yogg.support;

/**
 * @author joybean
 */
public class YoggException extends RuntimeException {

    /**
     *
     */
    private static final long serialVersionUID = 2860110285079564578L;

    public YoggException() {
        super();
    }

    public YoggException(String message) {
        super(message);
    }

    public YoggException(String messageTemplate, Object... args) {
        super(String.format(messageTemplate, args));
    }

    public YoggException(String message, Throwable cause) {
        super(message, cause);
    }

    public YoggException(Throwable cause, String messageTemplate, Object... args) {
        super(String.format(messageTemplate, args), cause);
    }

    public YoggException(Throwable cause) {
        super(cause);
    }

}
