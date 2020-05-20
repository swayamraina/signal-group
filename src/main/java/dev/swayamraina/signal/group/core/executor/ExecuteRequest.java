package dev.swayamraina.signal.group.core.executor;


import dev.swayamraina.signal.group.annotations.Internal;

public class ExecuteRequest {

    @Internal private long start;
    public long start () { return start; }

    private String uid;
    public String uid () { return uid; }


    public ExecuteRequest (String uid) {
        this.uid = uid;
        this.start = System.nanoTime();
    }

}
