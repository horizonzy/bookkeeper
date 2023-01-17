/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.bookkeeper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import org.apache.bookkeeper.client.BKException;
import org.apache.bookkeeper.client.BookKeeper;
import org.apache.bookkeeper.client.BookKeeper.DigestType;
import org.apache.bookkeeper.client.LedgerEntry;
import org.apache.bookkeeper.client.LedgerHandle;
import org.apache.bookkeeper.conf.ClientConfiguration;

public class BookkeeperDemo {
    
    public static void main(String[] args) throws BKException, IOException, InterruptedException {
    
        ClientConfiguration clientConf = new ClientConfiguration().setMetadataServiceUri(
                "zk+null://127.0.0.1:2181/ledgers");
        clientConf.setUseV2WireProtocol(true);
        BookKeeper bkc = new BookKeeper(clientConf);
    
        for (int i = 0; i < 10000; i++) {
            try{
                LedgerHandle lh = bkc.createLedger(3, 3, 2, BookKeeper.DigestType.MAC, "".getBytes(StandardCharsets.UTF_8));
                ByteBuffer entry = ByteBuffer.allocate(4);
    
                int numberOfEntries = 100;
                for (int j = 0; j < numberOfEntries; j++) {
                    entry.putInt(j);
                    entry.position(0);
                    lh.addEntry(entry.array());
                }
            } catch (Exception e) {
            
            }
        }
    }
    
}
