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
import java.nio.charset.StandardCharsets;
import org.apache.bookkeeper.client.BookKeeper;
import org.apache.bookkeeper.client.BookKeeper.DigestType;
import org.apache.bookkeeper.client.LedgerHandle;
import org.apache.bookkeeper.client.api.BKException;
import org.apache.bookkeeper.client.api.LedgerEntries;
import org.apache.bookkeeper.client.api.LedgerEntry;

public class BookkeeperDemo1 {

    public static void main(String[] args) throws BKException, IOException, InterruptedException {
        BookKeeper bkc = new BookKeeper("127.0.0.1:2181");

        LedgerHandle lh = bkc.openLedger(86, DigestType.MAC, "".getBytes(StandardCharsets.UTF_8));
        LedgerEntries entries = lh.read(1, 1);

        for (LedgerEntry entry : entries) {
            byte[] entryBytes = entry.getEntryBytes();
            System.out.println(new String(entryBytes));
        }

        lh.close();
        bkc.close();

    }

}
