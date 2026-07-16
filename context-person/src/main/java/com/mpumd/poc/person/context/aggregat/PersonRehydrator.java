package com.mpumd.poc.person.context.aggregat;

/**
 * Construction gate for the persistence layer.
 * <p>
 * Rebuilding a {@link Person} from the store is a technical act: the stored state is the truth,
 * no business rule must be replayed (business creation goes through {@link Person#register}).
 * Extending this class is the explicit way for an adapter to turn a {@link PersonSnapshot} back
 * into a living aggregate.
 */
public abstract class PersonRehydrator {

    protected PersonRehydrator() {
    }

    protected static Person rehydrate(PersonSnapshot snapshot) {
        return Person.fromMementoSnapshot(snapshot);
    }
}
