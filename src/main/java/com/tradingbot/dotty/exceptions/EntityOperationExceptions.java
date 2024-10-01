package com.tradingbot.dotty.exceptions;

public class EntityOperationExceptions {

    public static class NotFoundException extends RuntimeException {
        public NotFoundException(String message) {
            super(message);
        }
    }

    public static class AlreadyExistsException extends RuntimeException {
        public AlreadyExistsException(String message) {
            super(message);
        }
    }

    public static class SaveException extends RuntimeException {
        public SaveException(String message) {
            super(message);
        }
    }

    public static class UpdateException extends RuntimeException {
        public UpdateException(String message) {
            super(message);
        }
    }

    public static class DeleteException extends RuntimeException {
        public DeleteException(String message) {
            super(message);
        }
    }

    public static class InvalidException extends RuntimeException {
        public InvalidException(String message) {
            super(message);
        }
    }

}
