package org.manathome.schema2doc.scanner.impl;

import org.manathome.schema2doc.scanner.ScannerException;
import org.manathome.schema2doc.util.Ensure;
import org.manathome.schema2doc.util.NotNull;
import org.manathome.schema2doc.util.Require;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Iterator;
import java.util.function.Function;

/** 
 * map a jdbc result set to java8 stream.
 * 
 * @param <T> return type the result set is mapped into.
 *  */
public class ResultSetIterator<T> implements Iterator<T> {
	
	private static final Logger LOG = LoggerFactory.getLogger(ResultSetIterator.class);

    private ResultSet rsSource;
    private Function<ResultSet, T> mapRow;
    private int cntTables = 0;

    public ResultSetIterator(
    		@NotNull ResultSet rsSource, 
    		@NotNull Function<ResultSet, T> mapRow) {
    	
        this.rsSource 	= Require.notNull(rsSource, "rsSource");
        this.mapRow 	= Require.notNull(mapRow, "mapRow");
    }

    @Override
    public boolean hasNext() {
        try {
            boolean hasMore = rsSource != null && rsSource.next();            
            if (!hasMore) {
                close();
                LOG.debug("hasNext() closes");
            } else {
            	cntTables++;           	
            }
            return hasMore;
            
        } catch (SQLException ex) {
            close();
            throw new ScannerException(ex.getMessage(), ex);
        }

    }

    private void close() {
        try {
        	if (rsSource != null) {
        		LOG.debug("close resultSetIterator");
        		rsSource.close();
        	}
        } catch (SQLException e) {          
        	 //nothing we can do here
        } finally {
        	rsSource = null;
        }
    }

    @Override
    public T next() {
        try {      	
        	
            T obj = mapRow.apply(this.rsSource);
		    LOG.debug(" ... created from row " + obj);
            
   			if (cntTables == 0 || (cntTables % 100) == 0) {
			    LOG.debug(" ... scanning row " + obj);
			}          	

            return Ensure.notNull(obj, "next()-> null");
            
        } catch (Exception ex) {
        	LOG.error(ex.getMessage(), ex);
            close();
            throw ex;
        }
    }
}
