// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: server_api.proto

package net.manaty.octopusync.api;

public interface ClientSyncMessageOrBuilder extends
    // @@protoc_insertion_point(interface_extends:net.manaty.octopusync.api.ClientSyncMessage)
    com.google.protobuf.MessageOrBuilder {

  /**
   * <code>.net.manaty.octopusync.api.Session session = 1;</code>
   */
  net.manaty.octopusync.api.Session getSession();
  /**
   * <code>.net.manaty.octopusync.api.Session session = 1;</code>
   */
  net.manaty.octopusync.api.SessionOrBuilder getSessionOrBuilder();

  /**
   * <code>.net.manaty.octopusync.api.SyncTimeResponse sync_time_response = 2;</code>
   */
  net.manaty.octopusync.api.SyncTimeResponse getSyncTimeResponse();
  /**
   * <code>.net.manaty.octopusync.api.SyncTimeResponse sync_time_response = 2;</code>
   */
  net.manaty.octopusync.api.SyncTimeResponseOrBuilder getSyncTimeResponseOrBuilder();

  public net.manaty.octopusync.api.ClientSyncMessage.MessageCase getMessageCase();
}