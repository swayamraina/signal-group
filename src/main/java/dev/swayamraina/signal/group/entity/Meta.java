package dev.swayamraina.signal.group.entity;

import java.util.Optional;

import static dev.swayamraina.signal.group.utils.Constants.EMPTY;

public final class Meta {

    private Optional<String> name;
    private String name () { return name.isPresent() ? name.get() : EMPTY; }

    private Optional<String> description;
    private String description () { return description.isPresent() ? description.get() : EMPTY; }

}
