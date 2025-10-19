package hu.educloud.main.common;

/**
 * Convenience single-type variant so implementations that use the same type
 * for both the returned model (T) and the input DTO (U) don't have to
 * supply the second generic parameter.
 * Example: implement `IServiceSimple<Users>` instead of `IService<Users, Users>`.
 */
public interface IServiceSimple<T> extends IService<T, T> {}


