/*
 *  BSD License (http://www.galagosearch.org/license)
 */
package org.galagosearch.core.index.merge;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.galagosearch.core.index.KeyIterator;
import org.galagosearch.core.index.ValueIterator;
import org.galagosearch.tupleflow.DataStream;
import org.galagosearch.tupleflow.Utility;

/**
 * Uses mapping data to map the keys of a KeyIterator
 * 
 *  --- Assumes the mapping for a key iterator is in ascending order ---
 *      0 -> 10
 *      1 -> (x > 10)
 *
 * @author sjh
 */
public class KeyIteratorWrapper implements Comparable<KeyIteratorWrapper> {
  int id;
  KeyIterator iterator;
  DocumentMappingReader mappingReader;

  public KeyIteratorWrapper(int indexId, KeyIterator iterator, boolean mappingKeys, DocumentMappingReader mappingReader){
    this.iterator = iterator;
    if(mappingKeys){
      this.mappingReader = mappingReader;
    } else {
      this.mappingReader = null;
    }
  }

  // Perform the mapping on the current key

  public byte[] getKeyBytes() throws IOException {
    if(mappingReader != null){
      return mappingReader.map(id, this.iterator.getKeyBytes());
    } else {
      return this.iterator.getKeyBytes();
    }
  }

  public int compareTo(KeyIteratorWrapper t) {
    try {
      return Utility.compare(getKeyBytes(), t.getKeyBytes());
    } catch (IOException ex) {
      Logger.getLogger(KeyIteratorWrapper.class.getName()).log(Level.SEVERE, "There is a problem comparing mapped keys.", ex);
      throw new RuntimeException (ex);
    }
  }

  public boolean nextKey() throws IOException{
    return iterator.nextKey();
  }
}