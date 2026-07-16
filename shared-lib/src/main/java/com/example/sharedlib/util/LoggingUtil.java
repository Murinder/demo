package com.example.sharedlib.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for centralized and structured logging.
 * Provides methods to log messages with additional context, such as correlation IDs or business-specific data.
 * <p>
 * Example usage:
 * <pre>
 *     LoggingUtil.logInfo(getClass(), "User {} successfully authenticated", userId);
 *     LoggingUtil.logError(getClass(), "Failed to process payment for order {}", orderId, exception);
 * </pre>
 */
public final class LoggingUtil {

    private LoggingUtil() {
        // Private constructor to prevent instantiation
    }

    /**
     * Logs an informational message.
     *
     * @param clazz   The class from which the log is generated.
     * @param message The log message with placeholders {}.
     * @param args    The arguments for the placeholders.
     */
    public static void logInfo(Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.info(message, args);
    }

    /**
     * Logs a warning message.
     *
     * @param clazz   The class from which the log is generated.
     * @param message The log message with placeholders {}.
     * @param args    The arguments for the placeholders.
     */
    public static void logWarn(Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.warn(message, args);
    }

    /**
     * Logs an error message.
     *
     * @param clazz   The class from which the log is generated.
     * @param message The log message with placeholders {}.
     * @param t       The throwable exception to log.
     * @param args    The arguments for the placeholders.
     */
    public static void logError(Class<?> clazz, String message, Throwable t, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.error(message, args, t);
    }

    /**
     * Logs a debug message.
     *
     * @param clazz   The class from which the log is generated.
     * @param message The log message with placeholders {}.
     * @param args    The arguments for the placeholders.
     */
    public static void logDebug(Class<?> clazz, String message, Object... args) {
        Logger logger = LoggerFactory.getLogger(clazz);
        logger.debug(message, args);
    }
}