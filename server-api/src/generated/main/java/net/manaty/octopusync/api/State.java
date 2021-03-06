// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server_api.proto

package net.manaty.octopusync.api;

/**
 * Protobuf enum {@code net.manaty.octopusync.api.State}
 */
public enum State
    implements com.google.protobuf.ProtocolMessageEnum {
  /**
   * <code>NONE = 0;</code>
   */
  NONE(0),
  /**
   * <code>NEUTRE = 1;</code>
   */
  NEUTRE(1),
  /**
   * <code>FAIBLE_EMOTION = 2;</code>
   */
  FAIBLE_EMOTION(2),
  /**
   * <code>EMOTION_INTENSE = 3;</code>
   */
  EMOTION_INTENSE(3),
  /**
   * <code>FRISSON = 4;</code>
   */
  FRISSON(4),
  UNRECOGNIZED(-1),
  ;

  /**
   * <code>NONE = 0;</code>
   */
  public static final int NONE_VALUE = 0;
  /**
   * <code>NEUTRE = 1;</code>
   */
  public static final int NEUTRE_VALUE = 1;
  /**
   * <code>FAIBLE_EMOTION = 2;</code>
   */
  public static final int FAIBLE_EMOTION_VALUE = 2;
  /**
   * <code>EMOTION_INTENSE = 3;</code>
   */
  public static final int EMOTION_INTENSE_VALUE = 3;
  /**
   * <code>FRISSON = 4;</code>
   */
  public static final int FRISSON_VALUE = 4;


  public final int getNumber() {
    if (this == UNRECOGNIZED) {
      throw new java.lang.IllegalArgumentException(
          "Can't get the number of an unknown enum value.");
    }
    return value;
  }

  /**
   * @deprecated Use {@link #forNumber(int)} instead.
   */
  @java.lang.Deprecated
  public static State valueOf(int value) {
    return forNumber(value);
  }

  public static State forNumber(int value) {
    switch (value) {
      case 0: return NONE;
      case 1: return NEUTRE;
      case 2: return FAIBLE_EMOTION;
      case 3: return EMOTION_INTENSE;
      case 4: return FRISSON;
      default: return null;
    }
  }

  public static com.google.protobuf.Internal.EnumLiteMap<State>
      internalGetValueMap() {
    return internalValueMap;
  }
  private static final com.google.protobuf.Internal.EnumLiteMap<
      State> internalValueMap =
        new com.google.protobuf.Internal.EnumLiteMap<State>() {
          public State findValueByNumber(int number) {
            return State.forNumber(number);
          }
        };

  public final com.google.protobuf.Descriptors.EnumValueDescriptor
      getValueDescriptor() {
    return getDescriptor().getValues().get(ordinal());
  }
  public final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptorForType() {
    return getDescriptor();
  }
  public static final com.google.protobuf.Descriptors.EnumDescriptor
      getDescriptor() {
    return net.manaty.octopusync.api.ServerApi.getDescriptor().getEnumTypes().get(0);
  }

  private static final State[] VALUES = values();

  public static State valueOf(
      com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
    if (desc.getType() != getDescriptor()) {
      throw new java.lang.IllegalArgumentException(
        "EnumValueDescriptor is not for this type.");
    }
    if (desc.getIndex() == -1) {
      return UNRECOGNIZED;
    }
    return VALUES[desc.getIndex()];
  }

  private final int value;

  private State(int value) {
    this.value = value;
  }

  // @@protoc_insertion_point(enum_scope:net.manaty.octopusync.api.State)
}

