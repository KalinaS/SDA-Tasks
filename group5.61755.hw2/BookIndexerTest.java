
public class BookIndexerTest {
	public static void main(String[] args) {
		new BookIndexer61755().buildIndex("d:/book.txt", new String[] { "lorem", "quisque", "aenean" }, "d:/book.index.txt");
	}
}