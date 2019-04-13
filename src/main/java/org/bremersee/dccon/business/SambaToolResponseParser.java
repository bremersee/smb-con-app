/*
 * Copyright 2016 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.bremersee.dccon.business;

import java.util.List;
import javax.validation.constraints.NotNull;
import org.bremersee.dccon.model.DnsEntry;
import org.bremersee.dccon.model.DnsZone;

/**
 * The samba tool response parser interface.
 *
 * @author Christian Bremer
 */
public interface SambaToolResponseParser {

  /**
   * Parse dns zones list.
   *
   * @param response the response
   * @return the list
   */
  List<DnsZone> parseDnsZones(@NotNull CommandExecutorResponse response);

  /**
   * Parse dns records list.
   *
   * @param response the response
   * @return the list
   */
  List<DnsEntry> parseDnsRecords(@NotNull CommandExecutorResponse response);

}