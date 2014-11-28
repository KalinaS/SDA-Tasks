import java.util.LinkedList;


public class FileEncoder61755Test {

	public static void main(String[] args) {
		FileEncoderFN encoder = new FileEncoder61755();
		LinkedList<Character> generateKey = generateKey();
		encoder.encode("D:/input.bin", "D:/input.enc.bin", generateKey);
		encoder.decode("D:/input.enc.bin", "D:/input.dec.bin", generateKey);
	}
	
	private static LinkedList<Character> generateKey(){
		LinkedList<Character> linkedList = new LinkedList<Character>();
		for (int i = 255; i >= 0; i--) {
			linkedList.add((char)i);
		}
		return linkedList;
	}

}
