import awc.csv.Entry;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(MockitoJUnitRunner.class)
public class TestEntry {

    private Entry entry;

    @Before
    public void setup() {
        entry = new Entry();
    }

    @Test
    public void testInvalidAssignment() {
        entry.set("blahblah", "poggers");
        assertNull(entry.get("blahblah"));
    }

    @Test
    public void testValidAssignment() {
        entry.set("RECORD_ID", "001");
        assertEquals("001", entry.get("RECORD_ID"));
    }

    @Test
    public void testNullAssignment() {
        entry.set(null, "bree");
        assertNull(entry.get(null));
    }




}
