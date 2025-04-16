package org.storkforge.barkr.dto.factDto;

import java.io.Serializable;

public record Fact(String id, String type, FactAttributes attributes) implements Serializable {}
