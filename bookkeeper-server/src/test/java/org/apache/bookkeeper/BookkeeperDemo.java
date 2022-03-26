package org.apache.bookkeeper;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import org.apache.bookkeeper.client.BKException;
import org.apache.bookkeeper.client.BookKeeper;
import org.apache.bookkeeper.client.LedgerEntry;
import org.apache.bookkeeper.client.LedgerHandle;

public class BookkeeperDemo {

  public static void main(String[] args) throws BKException, IOException, InterruptedException {
    // Create a client object for the local ensemble. This
    // operation throws multiple exceptions, so make sure to
    // use a try/catch block when instantiating client objects.
    BookKeeper bkc = new BookKeeper("127.0.0.1:2181");

    // A password for the new ledger
    byte[] ledgerPassword = "horizon".getBytes(
        StandardCharsets.UTF_8);/* some sequence of bytes, perhaps random */

    // Create a new ledger and fetch its identifier
    LedgerHandle lh = bkc.createLedger(BookKeeper.DigestType.MAC, ledgerPassword);
    long ledgerId = lh.getId();

    // Create a buffer for four-byte entries
    ByteBuffer entry = ByteBuffer.allocate(4);

    int numberOfEntries = 100;

    // Add entries to the ledger, then close it
    for (int i = 0; i < numberOfEntries; i++) {
      entry.putInt(i);
      entry.position(0);
      lh.addEntry(entry.array());
    }
    lh.close();

    // Open the ledger for reading
    lh = bkc.openLedger(ledgerId, BookKeeper.DigestType.MAC, ledgerPassword);

    // Read all available entries
    Enumeration<LedgerEntry> entries = lh.readEntries(0, numberOfEntries - 1);

    while (entries.hasMoreElements()) {
      ByteBuffer result = ByteBuffer.wrap(entries.nextElement().getEntry());
      Integer retrEntry = result.getInt();

      // Print the integer stored in each entry
      System.out.println(String.format("Result: %s", retrEntry));
    }

    // Close the ledger and the client
    lh.close();
    bkc.close();
  }

}
