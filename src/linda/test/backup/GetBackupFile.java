package linda.test.backup;

import linda.server.LindaClient;

public class GetBackupFile {
	public static void main(String[] args) {
		LindaClient linda = new linda.server.LindaClient("localhost:4000/MonServeur");
		
		linda.debug(null);
	}
}
