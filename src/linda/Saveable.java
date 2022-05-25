package linda;

import java.io.FileNotFoundException;

public interface Saveable {
	
	/**
	 * Enregistrer des données dans un fichier
	 * @param fileName fichier où seront enregistrées les données
	 */
	void saveToFile(String fileName);
	
	/**
	 * Récupère les informations stockées
	 * @param fileName fichier où sont stockées les données
	 * @throws FileNotFoundException si le fichier n'existe pas
	 */
	void loadFromFile(String fileName) throws FileNotFoundException;
}
