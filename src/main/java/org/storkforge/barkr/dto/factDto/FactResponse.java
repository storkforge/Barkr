package org.storkforge.barkr.dto.factDto;

import java.io.Serializable;
import java.util.List;

public record FactResponse(List<Fact> data) implements Serializable {}
