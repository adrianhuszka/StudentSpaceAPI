package hu.studentspace.main.common;

/**
 * Convenience single-type variant for controllers that use the same type
 * for both the entity (T) and the DTO (U).
 * Example: implement `IControllerSimple<Forum>` instead of `IController<Forum,
 * Forum>`.
 */
public interface IControllerSimple<T> extends IController<T, T> {
    // no extra methods; purely a convenience alias
}
