package zamk.lib.zio;

import java.util.HashMap;

/**
 * @since 1.0
 */
public class ZamkIntermediateObject {
	
	HashMap<String, ZIOValue> zio;
	
	public ZamkIntermediateObject() {
		this.zio = new HashMap<String, ZIOValue>();
	}

	public HashMap<String, ZIOValue> getMap() {
		return zio;
	}	
}
