package org.bremersee.dccon.repository.ldap.transcoder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.Month;
import java.time.OffsetDateTime;
import org.junit.Test;

/**
 * The when time value transcoder test.
 *
 * @author Christian Bremer
 */
public class WhenTimeValueTranscoderTest {

  private static final WhenTimeValueTranscoder transcoder = new WhenTimeValueTranscoder();

  private static final String ldapValue = "20191226154554";

  /**
   * Decode and encode.
   */
  @Test
  public void decodeAndEncode() {
    OffsetDateTime dateTime = transcoder.decodeStringValue(ldapValue);
    assertNotNull(dateTime);
    assertEquals(2019, dateTime.getYear());
    assertEquals(Month.DECEMBER, dateTime.getMonth());
    assertEquals(26, dateTime.getDayOfMonth());
    assertEquals(15, dateTime.getHour());
    assertEquals(45, dateTime.getMinute());
    assertEquals(54, dateTime.getSecond());

    assertEquals(ldapValue, transcoder.encodeStringValue(dateTime));
  }

  /**
   * Gets type.
   */
  @Test
  public void getType() {
    assertEquals(OffsetDateTime.class, transcoder.getType());
  }
}