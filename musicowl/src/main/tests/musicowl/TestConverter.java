package musicowl;

import java.util.*;
import static org.junit.Assert.*;
import org.junit.*;

import de.wwu.music2rdf.util.Util;

public class TestConverter {
	@Test
	public void testTimeElapsed() {

		assertEquals(Util.timeElapsed(new Date(), new Date()), "0 ms");

	}
}