// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server_api.proto

package net.manaty.octopusync.api;

/**
 * Protobuf type {@code net.manaty.octopusync.api.Notification}
 */
public  final class Notification extends
    com.google.protobuf.GeneratedMessageV3 implements
    // @@protoc_insertion_point(message_implements:net.manaty.octopusync.api.Notification)
    NotificationOrBuilder {
  // Use Notification.newBuilder() to construct.
  private Notification(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
    super(builder);
  }
  private Notification() {
  }

  @java.lang.Override
  public final com.google.protobuf.UnknownFieldSet
  getUnknownFields() {
    return com.google.protobuf.UnknownFieldSet.getDefaultInstance();
  }
  private Notification(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    this();
    int mutable_bitField0_ = 0;
    try {
      boolean done = false;
      while (!done) {
        int tag = input.readTag();
        switch (tag) {
          case 0:
            done = true;
            break;
          default: {
            if (!input.skipField(tag)) {
              done = true;
            }
            break;
          }
          case 10: {
            net.manaty.octopusync.api.ExperienceStartedEvent.Builder subBuilder = null;
            if (notificationCase_ == 1) {
              subBuilder = ((net.manaty.octopusync.api.ExperienceStartedEvent) notification_).toBuilder();
            }
            notification_ =
                input.readMessage(net.manaty.octopusync.api.ExperienceStartedEvent.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((net.manaty.octopusync.api.ExperienceStartedEvent) notification_);
              notification_ = subBuilder.buildPartial();
            }
            notificationCase_ = 1;
            break;
          }
          case 18: {
            net.manaty.octopusync.api.ExperienceStoppedEvent.Builder subBuilder = null;
            if (notificationCase_ == 2) {
              subBuilder = ((net.manaty.octopusync.api.ExperienceStoppedEvent) notification_).toBuilder();
            }
            notification_ =
                input.readMessage(net.manaty.octopusync.api.ExperienceStoppedEvent.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((net.manaty.octopusync.api.ExperienceStoppedEvent) notification_);
              notification_ = subBuilder.buildPartial();
            }
            notificationCase_ = 2;
            break;
          }
          case 26: {
            net.manaty.octopusync.api.DevEvent.Builder subBuilder = null;
            if (notificationCase_ == 3) {
              subBuilder = ((net.manaty.octopusync.api.DevEvent) notification_).toBuilder();
            }
            notification_ =
                input.readMessage(net.manaty.octopusync.api.DevEvent.parser(), extensionRegistry);
            if (subBuilder != null) {
              subBuilder.mergeFrom((net.manaty.octopusync.api.DevEvent) notification_);
              notification_ = subBuilder.buildPartial();
            }
            notificationCase_ = 3;
            break;
          }
        }
      }
    } catch (com.google.protobuf.InvalidProtocolBufferException e) {
      throw e.setUnfinishedMessage(this);
    } catch (java.io.IOException e) {
      throw new com.google.protobuf.InvalidProtocolBufferException(
          e).setUnfinishedMessage(this);
    } finally {
      makeExtensionsImmutable();
    }
  }
  public static final com.google.protobuf.Descriptors.Descriptor
      getDescriptor() {
    return net.manaty.octopusync.api.ServerApi.internal_static_net_manaty_octopusync_api_Notification_descriptor;
  }

  protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internalGetFieldAccessorTable() {
    return net.manaty.octopusync.api.ServerApi.internal_static_net_manaty_octopusync_api_Notification_fieldAccessorTable
        .ensureFieldAccessorsInitialized(
            net.manaty.octopusync.api.Notification.class, net.manaty.octopusync.api.Notification.Builder.class);
  }

  private int notificationCase_ = 0;
  private java.lang.Object notification_;
  public enum NotificationCase
      implements com.google.protobuf.Internal.EnumLite {
    EXPERIENCE_STARTED_EVENT(1),
    EXPERIENCE_STOPPED_EVENT(2),
    DEV_EVENT(3),
    NOTIFICATION_NOT_SET(0);
    private final int value;
    private NotificationCase(int value) {
      this.value = value;
    }
    /**
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static NotificationCase valueOf(int value) {
      return forNumber(value);
    }

    public static NotificationCase forNumber(int value) {
      switch (value) {
        case 1: return EXPERIENCE_STARTED_EVENT;
        case 2: return EXPERIENCE_STOPPED_EVENT;
        case 3: return DEV_EVENT;
        case 0: return NOTIFICATION_NOT_SET;
        default: return null;
      }
    }
    public int getNumber() {
      return this.value;
    }
  };

  public NotificationCase
  getNotificationCase() {
    return NotificationCase.forNumber(
        notificationCase_);
  }

  public static final int EXPERIENCE_STARTED_EVENT_FIELD_NUMBER = 1;
  /**
   * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
   */
  public net.manaty.octopusync.api.ExperienceStartedEvent getExperienceStartedEvent() {
    if (notificationCase_ == 1) {
       return (net.manaty.octopusync.api.ExperienceStartedEvent) notification_;
    }
    return net.manaty.octopusync.api.ExperienceStartedEvent.getDefaultInstance();
  }
  /**
   * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
   */
  public net.manaty.octopusync.api.ExperienceStartedEventOrBuilder getExperienceStartedEventOrBuilder() {
    if (notificationCase_ == 1) {
       return (net.manaty.octopusync.api.ExperienceStartedEvent) notification_;
    }
    return net.manaty.octopusync.api.ExperienceStartedEvent.getDefaultInstance();
  }

  public static final int EXPERIENCE_STOPPED_EVENT_FIELD_NUMBER = 2;
  /**
   * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
   */
  public net.manaty.octopusync.api.ExperienceStoppedEvent getExperienceStoppedEvent() {
    if (notificationCase_ == 2) {
       return (net.manaty.octopusync.api.ExperienceStoppedEvent) notification_;
    }
    return net.manaty.octopusync.api.ExperienceStoppedEvent.getDefaultInstance();
  }
  /**
   * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
   */
  public net.manaty.octopusync.api.ExperienceStoppedEventOrBuilder getExperienceStoppedEventOrBuilder() {
    if (notificationCase_ == 2) {
       return (net.manaty.octopusync.api.ExperienceStoppedEvent) notification_;
    }
    return net.manaty.octopusync.api.ExperienceStoppedEvent.getDefaultInstance();
  }

  public static final int DEV_EVENT_FIELD_NUMBER = 3;
  /**
   * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
   */
  public net.manaty.octopusync.api.DevEvent getDevEvent() {
    if (notificationCase_ == 3) {
       return (net.manaty.octopusync.api.DevEvent) notification_;
    }
    return net.manaty.octopusync.api.DevEvent.getDefaultInstance();
  }
  /**
   * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
   */
  public net.manaty.octopusync.api.DevEventOrBuilder getDevEventOrBuilder() {
    if (notificationCase_ == 3) {
       return (net.manaty.octopusync.api.DevEvent) notification_;
    }
    return net.manaty.octopusync.api.DevEvent.getDefaultInstance();
  }

  private byte memoizedIsInitialized = -1;
  public final boolean isInitialized() {
    byte isInitialized = memoizedIsInitialized;
    if (isInitialized == 1) return true;
    if (isInitialized == 0) return false;

    memoizedIsInitialized = 1;
    return true;
  }

  public void writeTo(com.google.protobuf.CodedOutputStream output)
                      throws java.io.IOException {
    if (notificationCase_ == 1) {
      output.writeMessage(1, (net.manaty.octopusync.api.ExperienceStartedEvent) notification_);
    }
    if (notificationCase_ == 2) {
      output.writeMessage(2, (net.manaty.octopusync.api.ExperienceStoppedEvent) notification_);
    }
    if (notificationCase_ == 3) {
      output.writeMessage(3, (net.manaty.octopusync.api.DevEvent) notification_);
    }
  }

  public int getSerializedSize() {
    int size = memoizedSize;
    if (size != -1) return size;

    size = 0;
    if (notificationCase_ == 1) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(1, (net.manaty.octopusync.api.ExperienceStartedEvent) notification_);
    }
    if (notificationCase_ == 2) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(2, (net.manaty.octopusync.api.ExperienceStoppedEvent) notification_);
    }
    if (notificationCase_ == 3) {
      size += com.google.protobuf.CodedOutputStream
        .computeMessageSize(3, (net.manaty.octopusync.api.DevEvent) notification_);
    }
    memoizedSize = size;
    return size;
  }

  private static final long serialVersionUID = 0L;
  @java.lang.Override
  public boolean equals(final java.lang.Object obj) {
    if (obj == this) {
     return true;
    }
    if (!(obj instanceof net.manaty.octopusync.api.Notification)) {
      return super.equals(obj);
    }
    net.manaty.octopusync.api.Notification other = (net.manaty.octopusync.api.Notification) obj;

    boolean result = true;
    result = result && getNotificationCase().equals(
        other.getNotificationCase());
    if (!result) return false;
    switch (notificationCase_) {
      case 1:
        result = result && getExperienceStartedEvent()
            .equals(other.getExperienceStartedEvent());
        break;
      case 2:
        result = result && getExperienceStoppedEvent()
            .equals(other.getExperienceStoppedEvent());
        break;
      case 3:
        result = result && getDevEvent()
            .equals(other.getDevEvent());
        break;
      case 0:
      default:
    }
    return result;
  }

  @java.lang.Override
  public int hashCode() {
    if (memoizedHashCode != 0) {
      return memoizedHashCode;
    }
    int hash = 41;
    hash = (19 * hash) + getDescriptor().hashCode();
    switch (notificationCase_) {
      case 1:
        hash = (37 * hash) + EXPERIENCE_STARTED_EVENT_FIELD_NUMBER;
        hash = (53 * hash) + getExperienceStartedEvent().hashCode();
        break;
      case 2:
        hash = (37 * hash) + EXPERIENCE_STOPPED_EVENT_FIELD_NUMBER;
        hash = (53 * hash) + getExperienceStoppedEvent().hashCode();
        break;
      case 3:
        hash = (37 * hash) + DEV_EVENT_FIELD_NUMBER;
        hash = (53 * hash) + getDevEvent().hashCode();
        break;
      case 0:
      default:
    }
    hash = (29 * hash) + unknownFields.hashCode();
    memoizedHashCode = hash;
    return hash;
  }

  public static net.manaty.octopusync.api.Notification parseFrom(
      com.google.protobuf.ByteString data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.manaty.octopusync.api.Notification parseFrom(
      com.google.protobuf.ByteString data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.manaty.octopusync.api.Notification parseFrom(byte[] data)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data);
  }
  public static net.manaty.octopusync.api.Notification parseFrom(
      byte[] data,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws com.google.protobuf.InvalidProtocolBufferException {
    return PARSER.parseFrom(data, extensionRegistry);
  }
  public static net.manaty.octopusync.api.Notification parseFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static net.manaty.octopusync.api.Notification parseFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }
  public static net.manaty.octopusync.api.Notification parseDelimitedFrom(java.io.InputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input);
  }
  public static net.manaty.octopusync.api.Notification parseDelimitedFrom(
      java.io.InputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
  }
  public static net.manaty.octopusync.api.Notification parseFrom(
      com.google.protobuf.CodedInputStream input)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input);
  }
  public static net.manaty.octopusync.api.Notification parseFrom(
      com.google.protobuf.CodedInputStream input,
      com.google.protobuf.ExtensionRegistryLite extensionRegistry)
      throws java.io.IOException {
    return com.google.protobuf.GeneratedMessageV3
        .parseWithIOException(PARSER, input, extensionRegistry);
  }

  public Builder newBuilderForType() { return newBuilder(); }
  public static Builder newBuilder() {
    return DEFAULT_INSTANCE.toBuilder();
  }
  public static Builder newBuilder(net.manaty.octopusync.api.Notification prototype) {
    return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
  }
  public Builder toBuilder() {
    return this == DEFAULT_INSTANCE
        ? new Builder() : new Builder().mergeFrom(this);
  }

  @java.lang.Override
  protected Builder newBuilderForType(
      com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
    Builder builder = new Builder(parent);
    return builder;
  }
  /**
   * Protobuf type {@code net.manaty.octopusync.api.Notification}
   */
  public static final class Builder extends
      com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
      // @@protoc_insertion_point(builder_implements:net.manaty.octopusync.api.Notification)
      net.manaty.octopusync.api.NotificationOrBuilder {
    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return net.manaty.octopusync.api.ServerApi.internal_static_net_manaty_octopusync_api_Notification_descriptor;
    }

    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return net.manaty.octopusync.api.ServerApi.internal_static_net_manaty_octopusync_api_Notification_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              net.manaty.octopusync.api.Notification.class, net.manaty.octopusync.api.Notification.Builder.class);
    }

    // Construct using net.manaty.octopusync.api.Notification.newBuilder()
    private Builder() {
      maybeForceBuilderInitialization();
    }

    private Builder(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      super(parent);
      maybeForceBuilderInitialization();
    }
    private void maybeForceBuilderInitialization() {
      if (com.google.protobuf.GeneratedMessageV3
              .alwaysUseFieldBuilders) {
      }
    }
    public Builder clear() {
      super.clear();
      notificationCase_ = 0;
      notification_ = null;
      return this;
    }

    public com.google.protobuf.Descriptors.Descriptor
        getDescriptorForType() {
      return net.manaty.octopusync.api.ServerApi.internal_static_net_manaty_octopusync_api_Notification_descriptor;
    }

    public net.manaty.octopusync.api.Notification getDefaultInstanceForType() {
      return net.manaty.octopusync.api.Notification.getDefaultInstance();
    }

    public net.manaty.octopusync.api.Notification build() {
      net.manaty.octopusync.api.Notification result = buildPartial();
      if (!result.isInitialized()) {
        throw newUninitializedMessageException(result);
      }
      return result;
    }

    public net.manaty.octopusync.api.Notification buildPartial() {
      net.manaty.octopusync.api.Notification result = new net.manaty.octopusync.api.Notification(this);
      if (notificationCase_ == 1) {
        if (experienceStartedEventBuilder_ == null) {
          result.notification_ = notification_;
        } else {
          result.notification_ = experienceStartedEventBuilder_.build();
        }
      }
      if (notificationCase_ == 2) {
        if (experienceStoppedEventBuilder_ == null) {
          result.notification_ = notification_;
        } else {
          result.notification_ = experienceStoppedEventBuilder_.build();
        }
      }
      if (notificationCase_ == 3) {
        if (devEventBuilder_ == null) {
          result.notification_ = notification_;
        } else {
          result.notification_ = devEventBuilder_.build();
        }
      }
      result.notificationCase_ = notificationCase_;
      onBuilt();
      return result;
    }

    public Builder clone() {
      return (Builder) super.clone();
    }
    public Builder setField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.setField(field, value);
    }
    public Builder clearField(
        com.google.protobuf.Descriptors.FieldDescriptor field) {
      return (Builder) super.clearField(field);
    }
    public Builder clearOneof(
        com.google.protobuf.Descriptors.OneofDescriptor oneof) {
      return (Builder) super.clearOneof(oneof);
    }
    public Builder setRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        int index, Object value) {
      return (Builder) super.setRepeatedField(field, index, value);
    }
    public Builder addRepeatedField(
        com.google.protobuf.Descriptors.FieldDescriptor field,
        Object value) {
      return (Builder) super.addRepeatedField(field, value);
    }
    public Builder mergeFrom(com.google.protobuf.Message other) {
      if (other instanceof net.manaty.octopusync.api.Notification) {
        return mergeFrom((net.manaty.octopusync.api.Notification)other);
      } else {
        super.mergeFrom(other);
        return this;
      }
    }

    public Builder mergeFrom(net.manaty.octopusync.api.Notification other) {
      if (other == net.manaty.octopusync.api.Notification.getDefaultInstance()) return this;
      switch (other.getNotificationCase()) {
        case EXPERIENCE_STARTED_EVENT: {
          mergeExperienceStartedEvent(other.getExperienceStartedEvent());
          break;
        }
        case EXPERIENCE_STOPPED_EVENT: {
          mergeExperienceStoppedEvent(other.getExperienceStoppedEvent());
          break;
        }
        case DEV_EVENT: {
          mergeDevEvent(other.getDevEvent());
          break;
        }
        case NOTIFICATION_NOT_SET: {
          break;
        }
      }
      onChanged();
      return this;
    }

    public final boolean isInitialized() {
      return true;
    }

    public Builder mergeFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      net.manaty.octopusync.api.Notification parsedMessage = null;
      try {
        parsedMessage = PARSER.parsePartialFrom(input, extensionRegistry);
      } catch (com.google.protobuf.InvalidProtocolBufferException e) {
        parsedMessage = (net.manaty.octopusync.api.Notification) e.getUnfinishedMessage();
        throw e.unwrapIOException();
      } finally {
        if (parsedMessage != null) {
          mergeFrom(parsedMessage);
        }
      }
      return this;
    }
    private int notificationCase_ = 0;
    private java.lang.Object notification_;
    public NotificationCase
        getNotificationCase() {
      return NotificationCase.forNumber(
          notificationCase_);
    }

    public Builder clearNotification() {
      notificationCase_ = 0;
      notification_ = null;
      onChanged();
      return this;
    }


    private com.google.protobuf.SingleFieldBuilderV3<
        net.manaty.octopusync.api.ExperienceStartedEvent, net.manaty.octopusync.api.ExperienceStartedEvent.Builder, net.manaty.octopusync.api.ExperienceStartedEventOrBuilder> experienceStartedEventBuilder_;
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
     */
    public net.manaty.octopusync.api.ExperienceStartedEvent getExperienceStartedEvent() {
      if (experienceStartedEventBuilder_ == null) {
        if (notificationCase_ == 1) {
          return (net.manaty.octopusync.api.ExperienceStartedEvent) notification_;
        }
        return net.manaty.octopusync.api.ExperienceStartedEvent.getDefaultInstance();
      } else {
        if (notificationCase_ == 1) {
          return experienceStartedEventBuilder_.getMessage();
        }
        return net.manaty.octopusync.api.ExperienceStartedEvent.getDefaultInstance();
      }
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
     */
    public Builder setExperienceStartedEvent(net.manaty.octopusync.api.ExperienceStartedEvent value) {
      if (experienceStartedEventBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        notification_ = value;
        onChanged();
      } else {
        experienceStartedEventBuilder_.setMessage(value);
      }
      notificationCase_ = 1;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
     */
    public Builder setExperienceStartedEvent(
        net.manaty.octopusync.api.ExperienceStartedEvent.Builder builderForValue) {
      if (experienceStartedEventBuilder_ == null) {
        notification_ = builderForValue.build();
        onChanged();
      } else {
        experienceStartedEventBuilder_.setMessage(builderForValue.build());
      }
      notificationCase_ = 1;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
     */
    public Builder mergeExperienceStartedEvent(net.manaty.octopusync.api.ExperienceStartedEvent value) {
      if (experienceStartedEventBuilder_ == null) {
        if (notificationCase_ == 1 &&
            notification_ != net.manaty.octopusync.api.ExperienceStartedEvent.getDefaultInstance()) {
          notification_ = net.manaty.octopusync.api.ExperienceStartedEvent.newBuilder((net.manaty.octopusync.api.ExperienceStartedEvent) notification_)
              .mergeFrom(value).buildPartial();
        } else {
          notification_ = value;
        }
        onChanged();
      } else {
        if (notificationCase_ == 1) {
          experienceStartedEventBuilder_.mergeFrom(value);
        }
        experienceStartedEventBuilder_.setMessage(value);
      }
      notificationCase_ = 1;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
     */
    public Builder clearExperienceStartedEvent() {
      if (experienceStartedEventBuilder_ == null) {
        if (notificationCase_ == 1) {
          notificationCase_ = 0;
          notification_ = null;
          onChanged();
        }
      } else {
        if (notificationCase_ == 1) {
          notificationCase_ = 0;
          notification_ = null;
        }
        experienceStartedEventBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
     */
    public net.manaty.octopusync.api.ExperienceStartedEvent.Builder getExperienceStartedEventBuilder() {
      return getExperienceStartedEventFieldBuilder().getBuilder();
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
     */
    public net.manaty.octopusync.api.ExperienceStartedEventOrBuilder getExperienceStartedEventOrBuilder() {
      if ((notificationCase_ == 1) && (experienceStartedEventBuilder_ != null)) {
        return experienceStartedEventBuilder_.getMessageOrBuilder();
      } else {
        if (notificationCase_ == 1) {
          return (net.manaty.octopusync.api.ExperienceStartedEvent) notification_;
        }
        return net.manaty.octopusync.api.ExperienceStartedEvent.getDefaultInstance();
      }
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStartedEvent experience_started_event = 1;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        net.manaty.octopusync.api.ExperienceStartedEvent, net.manaty.octopusync.api.ExperienceStartedEvent.Builder, net.manaty.octopusync.api.ExperienceStartedEventOrBuilder> 
        getExperienceStartedEventFieldBuilder() {
      if (experienceStartedEventBuilder_ == null) {
        if (!(notificationCase_ == 1)) {
          notification_ = net.manaty.octopusync.api.ExperienceStartedEvent.getDefaultInstance();
        }
        experienceStartedEventBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            net.manaty.octopusync.api.ExperienceStartedEvent, net.manaty.octopusync.api.ExperienceStartedEvent.Builder, net.manaty.octopusync.api.ExperienceStartedEventOrBuilder>(
                (net.manaty.octopusync.api.ExperienceStartedEvent) notification_,
                getParentForChildren(),
                isClean());
        notification_ = null;
      }
      notificationCase_ = 1;
      onChanged();;
      return experienceStartedEventBuilder_;
    }

    private com.google.protobuf.SingleFieldBuilderV3<
        net.manaty.octopusync.api.ExperienceStoppedEvent, net.manaty.octopusync.api.ExperienceStoppedEvent.Builder, net.manaty.octopusync.api.ExperienceStoppedEventOrBuilder> experienceStoppedEventBuilder_;
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
     */
    public net.manaty.octopusync.api.ExperienceStoppedEvent getExperienceStoppedEvent() {
      if (experienceStoppedEventBuilder_ == null) {
        if (notificationCase_ == 2) {
          return (net.manaty.octopusync.api.ExperienceStoppedEvent) notification_;
        }
        return net.manaty.octopusync.api.ExperienceStoppedEvent.getDefaultInstance();
      } else {
        if (notificationCase_ == 2) {
          return experienceStoppedEventBuilder_.getMessage();
        }
        return net.manaty.octopusync.api.ExperienceStoppedEvent.getDefaultInstance();
      }
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
     */
    public Builder setExperienceStoppedEvent(net.manaty.octopusync.api.ExperienceStoppedEvent value) {
      if (experienceStoppedEventBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        notification_ = value;
        onChanged();
      } else {
        experienceStoppedEventBuilder_.setMessage(value);
      }
      notificationCase_ = 2;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
     */
    public Builder setExperienceStoppedEvent(
        net.manaty.octopusync.api.ExperienceStoppedEvent.Builder builderForValue) {
      if (experienceStoppedEventBuilder_ == null) {
        notification_ = builderForValue.build();
        onChanged();
      } else {
        experienceStoppedEventBuilder_.setMessage(builderForValue.build());
      }
      notificationCase_ = 2;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
     */
    public Builder mergeExperienceStoppedEvent(net.manaty.octopusync.api.ExperienceStoppedEvent value) {
      if (experienceStoppedEventBuilder_ == null) {
        if (notificationCase_ == 2 &&
            notification_ != net.manaty.octopusync.api.ExperienceStoppedEvent.getDefaultInstance()) {
          notification_ = net.manaty.octopusync.api.ExperienceStoppedEvent.newBuilder((net.manaty.octopusync.api.ExperienceStoppedEvent) notification_)
              .mergeFrom(value).buildPartial();
        } else {
          notification_ = value;
        }
        onChanged();
      } else {
        if (notificationCase_ == 2) {
          experienceStoppedEventBuilder_.mergeFrom(value);
        }
        experienceStoppedEventBuilder_.setMessage(value);
      }
      notificationCase_ = 2;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
     */
    public Builder clearExperienceStoppedEvent() {
      if (experienceStoppedEventBuilder_ == null) {
        if (notificationCase_ == 2) {
          notificationCase_ = 0;
          notification_ = null;
          onChanged();
        }
      } else {
        if (notificationCase_ == 2) {
          notificationCase_ = 0;
          notification_ = null;
        }
        experienceStoppedEventBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
     */
    public net.manaty.octopusync.api.ExperienceStoppedEvent.Builder getExperienceStoppedEventBuilder() {
      return getExperienceStoppedEventFieldBuilder().getBuilder();
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
     */
    public net.manaty.octopusync.api.ExperienceStoppedEventOrBuilder getExperienceStoppedEventOrBuilder() {
      if ((notificationCase_ == 2) && (experienceStoppedEventBuilder_ != null)) {
        return experienceStoppedEventBuilder_.getMessageOrBuilder();
      } else {
        if (notificationCase_ == 2) {
          return (net.manaty.octopusync.api.ExperienceStoppedEvent) notification_;
        }
        return net.manaty.octopusync.api.ExperienceStoppedEvent.getDefaultInstance();
      }
    }
    /**
     * <code>.net.manaty.octopusync.api.ExperienceStoppedEvent experience_stopped_event = 2;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        net.manaty.octopusync.api.ExperienceStoppedEvent, net.manaty.octopusync.api.ExperienceStoppedEvent.Builder, net.manaty.octopusync.api.ExperienceStoppedEventOrBuilder> 
        getExperienceStoppedEventFieldBuilder() {
      if (experienceStoppedEventBuilder_ == null) {
        if (!(notificationCase_ == 2)) {
          notification_ = net.manaty.octopusync.api.ExperienceStoppedEvent.getDefaultInstance();
        }
        experienceStoppedEventBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            net.manaty.octopusync.api.ExperienceStoppedEvent, net.manaty.octopusync.api.ExperienceStoppedEvent.Builder, net.manaty.octopusync.api.ExperienceStoppedEventOrBuilder>(
                (net.manaty.octopusync.api.ExperienceStoppedEvent) notification_,
                getParentForChildren(),
                isClean());
        notification_ = null;
      }
      notificationCase_ = 2;
      onChanged();;
      return experienceStoppedEventBuilder_;
    }

    private com.google.protobuf.SingleFieldBuilderV3<
        net.manaty.octopusync.api.DevEvent, net.manaty.octopusync.api.DevEvent.Builder, net.manaty.octopusync.api.DevEventOrBuilder> devEventBuilder_;
    /**
     * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
     */
    public net.manaty.octopusync.api.DevEvent getDevEvent() {
      if (devEventBuilder_ == null) {
        if (notificationCase_ == 3) {
          return (net.manaty.octopusync.api.DevEvent) notification_;
        }
        return net.manaty.octopusync.api.DevEvent.getDefaultInstance();
      } else {
        if (notificationCase_ == 3) {
          return devEventBuilder_.getMessage();
        }
        return net.manaty.octopusync.api.DevEvent.getDefaultInstance();
      }
    }
    /**
     * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
     */
    public Builder setDevEvent(net.manaty.octopusync.api.DevEvent value) {
      if (devEventBuilder_ == null) {
        if (value == null) {
          throw new NullPointerException();
        }
        notification_ = value;
        onChanged();
      } else {
        devEventBuilder_.setMessage(value);
      }
      notificationCase_ = 3;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
     */
    public Builder setDevEvent(
        net.manaty.octopusync.api.DevEvent.Builder builderForValue) {
      if (devEventBuilder_ == null) {
        notification_ = builderForValue.build();
        onChanged();
      } else {
        devEventBuilder_.setMessage(builderForValue.build());
      }
      notificationCase_ = 3;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
     */
    public Builder mergeDevEvent(net.manaty.octopusync.api.DevEvent value) {
      if (devEventBuilder_ == null) {
        if (notificationCase_ == 3 &&
            notification_ != net.manaty.octopusync.api.DevEvent.getDefaultInstance()) {
          notification_ = net.manaty.octopusync.api.DevEvent.newBuilder((net.manaty.octopusync.api.DevEvent) notification_)
              .mergeFrom(value).buildPartial();
        } else {
          notification_ = value;
        }
        onChanged();
      } else {
        if (notificationCase_ == 3) {
          devEventBuilder_.mergeFrom(value);
        }
        devEventBuilder_.setMessage(value);
      }
      notificationCase_ = 3;
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
     */
    public Builder clearDevEvent() {
      if (devEventBuilder_ == null) {
        if (notificationCase_ == 3) {
          notificationCase_ = 0;
          notification_ = null;
          onChanged();
        }
      } else {
        if (notificationCase_ == 3) {
          notificationCase_ = 0;
          notification_ = null;
        }
        devEventBuilder_.clear();
      }
      return this;
    }
    /**
     * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
     */
    public net.manaty.octopusync.api.DevEvent.Builder getDevEventBuilder() {
      return getDevEventFieldBuilder().getBuilder();
    }
    /**
     * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
     */
    public net.manaty.octopusync.api.DevEventOrBuilder getDevEventOrBuilder() {
      if ((notificationCase_ == 3) && (devEventBuilder_ != null)) {
        return devEventBuilder_.getMessageOrBuilder();
      } else {
        if (notificationCase_ == 3) {
          return (net.manaty.octopusync.api.DevEvent) notification_;
        }
        return net.manaty.octopusync.api.DevEvent.getDefaultInstance();
      }
    }
    /**
     * <code>.net.manaty.octopusync.api.DevEvent dev_event = 3;</code>
     */
    private com.google.protobuf.SingleFieldBuilderV3<
        net.manaty.octopusync.api.DevEvent, net.manaty.octopusync.api.DevEvent.Builder, net.manaty.octopusync.api.DevEventOrBuilder> 
        getDevEventFieldBuilder() {
      if (devEventBuilder_ == null) {
        if (!(notificationCase_ == 3)) {
          notification_ = net.manaty.octopusync.api.DevEvent.getDefaultInstance();
        }
        devEventBuilder_ = new com.google.protobuf.SingleFieldBuilderV3<
            net.manaty.octopusync.api.DevEvent, net.manaty.octopusync.api.DevEvent.Builder, net.manaty.octopusync.api.DevEventOrBuilder>(
                (net.manaty.octopusync.api.DevEvent) notification_,
                getParentForChildren(),
                isClean());
        notification_ = null;
      }
      notificationCase_ = 3;
      onChanged();;
      return devEventBuilder_;
    }
    public final Builder setUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }

    public final Builder mergeUnknownFields(
        final com.google.protobuf.UnknownFieldSet unknownFields) {
      return this;
    }


    // @@protoc_insertion_point(builder_scope:net.manaty.octopusync.api.Notification)
  }

  // @@protoc_insertion_point(class_scope:net.manaty.octopusync.api.Notification)
  private static final net.manaty.octopusync.api.Notification DEFAULT_INSTANCE;
  static {
    DEFAULT_INSTANCE = new net.manaty.octopusync.api.Notification();
  }

  public static net.manaty.octopusync.api.Notification getDefaultInstance() {
    return DEFAULT_INSTANCE;
  }

  private static final com.google.protobuf.Parser<Notification>
      PARSER = new com.google.protobuf.AbstractParser<Notification>() {
    public Notification parsePartialFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
        return new Notification(input, extensionRegistry);
    }
  };

  public static com.google.protobuf.Parser<Notification> parser() {
    return PARSER;
  }

  @java.lang.Override
  public com.google.protobuf.Parser<Notification> getParserForType() {
    return PARSER;
  }

  public net.manaty.octopusync.api.Notification getDefaultInstanceForType() {
    return DEFAULT_INSTANCE;
  }

}

