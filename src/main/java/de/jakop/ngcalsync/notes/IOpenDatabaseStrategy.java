package de.jakop.ngcalsync.notes;

import de.bea.domingo.DDatabase;

/**
 * 
 * @author jakop
 *
 */
public interface IOpenDatabaseStrategy {

	/**
	 * 
	 * @param dominoServer
	 * @param database
	 * @return the open database
	 */
	DDatabase openDatabase(String dominoServer, String database);

}