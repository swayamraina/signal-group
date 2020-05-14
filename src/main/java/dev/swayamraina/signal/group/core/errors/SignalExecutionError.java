package dev.swayamraina.signal.group.core.errors;

public class SignalExecutionError extends RuntimeException {

    public SignalExecutionError (String msg) { super(msg);}

    public SignalExecutionError (String msg, Throwable t) { super(msg, t); }

}
