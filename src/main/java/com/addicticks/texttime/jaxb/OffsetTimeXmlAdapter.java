/*
 * Copyright 2018 Addicticks.
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
package com.addicticks.texttime.jaxb;

import com.addicticks.texttime.formatters.DateTimeFormatterXSD;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * JAXB adapter to convert between {@code xs:time} and {@code OffsetTime}.
 * 
 * <p>
 * If the adapter is used for unmarshalling (parsing XML to object) and
 * there is no zone offset in the input data then it may be needed to
 * extend this class and customize the
 * {@link #getZoneOffsetForTime(java.time.LocalTime)} method.
 */
public class OffsetTimeXmlAdapter extends XmlAdapter<String, OffsetTime> {

  
    /**
     * Converts from {@code xs:time} format.
     * 
     * <p>
     * A number of minor deviations from the standard are accepted while parsing. 
     * See {@link DateTimeFormatterXSD#XSD_TIME_PARSER} for more information.
     * 
     * <p>
     * If there are more than 9 fractional digits on the second value then
     * digits after the 9th digit will be ignored.
     *
     */    
    @Override
    public final OffsetTime unmarshal(String v) {
        return DateTimeFormatterXSD.parseIntoOffsetTime(v, this::getZoneOffsetForTime);
    }

    /**
     * Converts to {@code xs:time} format.
     */
    @Override
    public final String marshal(OffsetTime v) {
        if (v == null) return null;
        return DateTimeFormatterXSD.XSD_TIME.format(v);
    }


    /**
     * Gets ZoneOffset for a given {@code LocalTime} value. 
     * Sub-classes may override this.
     * 
     * <p>
     * This method is needed because the XML Schema 
     * {@code time} data type allows to leave out the offset. Therefore,
     * when unmarshalling there may be no offset in the input data. If this is
     * the case then this method will be called. In summary, the method
     * is only called when unmarshalling and only when input data has no offset.
     * 
     * <p>
     * The default implementation uses the the system's default zone id 
     * (from {@link java.time.ZoneOffset#systemDefault()}) and then finds 
     * the appropriate offset for that zone given a point in time of
     * current-LocalDate + {@code localTime}.
     * 
     * @param localTime
     * @return offset to use when none is present in XML input data, never null
     */
    public ZoneOffset getZoneOffsetForTime(LocalTime localTime) {
        ZoneId systemDefaultZoneId = ZoneOffset.systemDefault();
        LocalDate localDate = LocalDate.now(systemDefaultZoneId);
        LocalDateTime localDateTime = LocalDateTime.of(localDate, localTime);
        return  systemDefaultZoneId.getRules().getOffset(localDateTime);
    }    

    
}
