// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: s2s_api.proto

package net.manaty.octopusync.s2s.api;

public final class S2SApi {
  private S2SApi() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_net_manaty_octopusync_s2s_api_SyncTimeRequest_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_net_manaty_octopusync_s2s_api_SyncTimeRequest_fieldAccessorTable;
  static final com.google.protobuf.Descriptors.Descriptor
    internal_static_net_manaty_octopusync_s2s_api_SyncTimeResponse_descriptor;
  static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_net_manaty_octopusync_s2s_api_SyncTimeResponse_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\rs2s_api.proto\022\035net.manaty.octopusync.s" +
      "2s.api\"!\n\017SyncTimeRequest\022\016\n\006seqnum\030\001 \001(" +
      "\003\"=\n\020SyncTimeResponse\022\016\n\006seqnum\030\001 \001(\003\022\031\n" +
      "\021received_time_utc\030\002 \001(\0032\200\001\n\rOctopuSyncS" +
      "2S\022o\n\010SyncTime\022..net.manaty.octopusync.s" +
      "2s.api.SyncTimeRequest\032/.net.manaty.octo" +
      "pusync.s2s.api.SyncTimeResponse(\0010\001B)\n\035n" +
      "et.manaty.octopusync.s2s.apiB\006S2SApiP\001b\006" +
      "proto3"
    };
    com.google.protobuf.Descriptors.FileDescriptor.InternalDescriptorAssigner assigner =
        new com.google.protobuf.Descriptors.FileDescriptor.    InternalDescriptorAssigner() {
          public com.google.protobuf.ExtensionRegistry assignDescriptors(
              com.google.protobuf.Descriptors.FileDescriptor root) {
            descriptor = root;
            return null;
          }
        };
    com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        }, assigner);
    internal_static_net_manaty_octopusync_s2s_api_SyncTimeRequest_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_net_manaty_octopusync_s2s_api_SyncTimeRequest_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_net_manaty_octopusync_s2s_api_SyncTimeRequest_descriptor,
        new java.lang.String[] { "Seqnum", });
    internal_static_net_manaty_octopusync_s2s_api_SyncTimeResponse_descriptor =
      getDescriptor().getMessageTypes().get(1);
    internal_static_net_manaty_octopusync_s2s_api_SyncTimeResponse_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_net_manaty_octopusync_s2s_api_SyncTimeResponse_descriptor,
        new java.lang.String[] { "Seqnum", "ReceivedTimeUtc", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
