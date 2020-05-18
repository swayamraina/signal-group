package dev.swayamraina.signal.group.core.extractor;

import dev.swayamraina.signal.group.annotations.Internal;

public final class ExtractorKey {

    @Internal private String key;
    public String key () { return key; }

    private String path;
    public String path () { return path; }



    public ExtractorKey (String path) { this.path = path; }

}
