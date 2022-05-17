package linda;

public interface Saveable {
	void saveToFile(String fileName);
	
	void loadFromFile(String fileName);
}
