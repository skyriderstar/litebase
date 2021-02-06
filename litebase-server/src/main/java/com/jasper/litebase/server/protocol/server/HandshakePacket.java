/*
 * Copyright 1999-2012 Alibaba Group.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jasper.litebase.server.protocol.server;

import com.jasper.litebase.server.protocol.MySQLPacket;
import com.jasper.litebase.server.protocol.util.BufferUtil;
import io.netty.buffer.ByteBuf;

/**
 * From server to client during initial handshake.
 *
 * <pre>
 * Bytes                        Name
 * -----                        ----
 * 1                            protocol_version
 * n (Null-Terminated String)   server_version
 * 4                            thread_id
 * 8                            scramble_buff
 * 1                            (filler) always 0x00
 * 2                            server_capabilities
 * 1                            server_language
 * 2                            server_status
 * 13                           (filler) always 0x00 ...
 * 13                           rest of scramble_buff (4.1)
 *
 * http://forge.mysql.com/wiki/MySQL_Internals_ClientServer_Protocol#Handshake_Initialization_Packet
 * </pre>
 *
 * @author xianmao.hexm 2010-7-14 下午05:18:15
 */
public class HandshakePacket extends MySQLPacket {
  private static final byte[] FILLER_13 = new byte[] {0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

  public byte protocolVersion;
  public byte[] serverVersion;
  public long threadId;
  public byte[] seed;
  public int serverCapabilities;
  public byte serverCharsetIndex;
  public int serverStatus;
  public byte[] restOfScrambleBuff;

  @Override
  public void writeToBuffer(ByteBuf buffer) {
    BufferUtil.writeUB3(buffer, calcPacketSize());
    buffer.writeByte(packetId);
    buffer.writeByte(protocolVersion);
    BufferUtil.writeWithNull(buffer, serverVersion);
    BufferUtil.writeUB4(buffer, threadId);
    BufferUtil.writeWithNull(buffer, seed);
    BufferUtil.writeUB2(buffer, serverCapabilities);
    buffer.writeByte(serverCharsetIndex);
    BufferUtil.writeUB2(buffer, serverStatus);
    buffer.writeBytes(FILLER_13);
    BufferUtil.writeWithNull(buffer, restOfScrambleBuff);
  }

  @Override
  public int calcPacketSize() {
    int size = 1;
    size += serverVersion.length; // n
    size += 5; // 1+4
    size += seed.length; // 8
    size += 19; // 1+2+1+2+13
    size += restOfScrambleBuff.length; // 12
    size += 1; // 1
    return size;
  }

  @Override
  protected String getPacketInfo() {
    return "MySQL Handshake Packet";
  }
}
