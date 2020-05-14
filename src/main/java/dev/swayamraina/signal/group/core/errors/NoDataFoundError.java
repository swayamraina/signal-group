package dev.swayamraina.signal.group.core.errors;

public class NoDataFoundError extends RuntimeException {

    public NoDataFoundError (String msg) { super(msg);}

    public NoDataFoundError (String msg, Throwable t) { super(msg, t); }

}
