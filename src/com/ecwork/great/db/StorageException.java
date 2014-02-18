package com.ecwork.great.db;

/**
 * User: ecsark
 * Date: 2/8/14
 * Time: 9:58 PM
 */
public class StorageException extends Exception {

    public StorageException() {
        super();
    }

    public StorageException(String message) {
        super(message);
    }

    public StorageException(Throwable throwable) {
        super(throwable);
    }
}