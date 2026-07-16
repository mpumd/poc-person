package com.mpumd.poc.person.context.exception;

import lombok.experimental.StandardException;

/**
 * Structural integrity violation detected while rebuilding a
 * {@link com.mpumd.poc.person.context.aggregat.Person} from persisted state.
 * Signals corrupted data — an ops incident to alert on, not an invalid client request.
 */
@StandardException
public class PersonRehydrationException extends IllegalStateException {

    public PersonRehydrationException(String message) {
        super(message);
    }

    public PersonRehydrationException(String message, Object... formattedArgs) {
        super(message.formatted(formattedArgs));
    }
}
