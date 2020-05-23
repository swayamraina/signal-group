package dev.swayamraina.signal.group.core.http.response;


public class Response {

    private int code;
    public int code () { return code; }

    private String content;
    public String content () { return content; }



    public Response (int code, String content) {
        this.code = code;
        this.content = content;
    }

}
