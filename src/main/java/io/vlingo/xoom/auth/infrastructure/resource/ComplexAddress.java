package io.vlingo.xoom.auth.infrastructure.resource;

import io.vlingo.xoom.actors.Address;

import java.util.function.Function;

public class ComplexAddress<ID> implements Address {
  private final ID id;
  private final Function<ID, String> transformer;

  public ComplexAddress(ID id, Function<ID, String> transformer) {
    this.id = id;
    this.transformer = transformer;
  }

  @Override
  public long id() {
    byte[] idBytes = transformer.apply(id).getBytes();
    long outcome = 0;
    for (int i = 0; i < idBytes.length; i++) {
      outcome = ((outcome << 8) | (idBytes[i] & 0xFF));
    }
    return outcome;
  }

  @Override
  public long idSequence() {
    return id();
  }

  @Override
  public String idSequenceString() {
    return idString();
  }

  @Override
  public String idString() {
    return String.valueOf(id());
  }

  @Override
  public <T> T idTyped() {
    return (T) id;
  }

  @Override
  public String name() {
    return "complex";
  }

  @Override
  public boolean isDistributable() {
    return true;
  }

  @Override
  public int compareTo(Address o) {
    throw new UnsupportedOperationException();
  }
}
