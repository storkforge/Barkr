package org.storkforge.barkr.dto.postDto;

import jakarta.validation.constraints.NotNull;

import java.io.Serializable;
import java.util.List;

public record ResponsePostList(@NotNull List<ResponsePost> posts) implements Serializable {
}
