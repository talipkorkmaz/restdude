/**
 *
 * Restdude
 * -------------------------------------------------------------------
 *
 * Copyright © 2005 Manos Batsis (manosbatsis gmail)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.restdude.mdd.binding;

import com.fasterxml.jackson.dataformat.csv.CsvMapper;
import com.fasterxml.jackson.dataformat.csv.CsvSchema;
import com.restdude.domain.users.model.UserRegistrationCodeInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.GenericTypeResolver;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.util.List;

public class CsvMessageConverter extends AbstractHttpMessageConverter<List<UserRegistrationCodeInfo>> {

    private static final Logger LOGGER = LoggerFactory.getLogger(CsvMessageConverter.class);

    public static final MediaType MEDIA_TYPE = new MediaType("text", "csv");
    public static final MediaType MEDIA_TYPE_UTF_8 = new MediaType("text", "csv", Charset.forName("utf-8"));

    public CsvMessageConverter() {
        super(MEDIA_TYPE, MEDIA_TYPE_UTF_8);
    }

    @Override
    protected boolean supports(Class<?> clazz) {
        boolean supports = List.class.isAssignableFrom(clazz);
        LOGGER.debug("supports class: {}, result: {}", clazz.getCanonicalName(), supports);
        return supports;
    }


    @Override
    protected List<UserRegistrationCodeInfo> readInternal(Class<? extends List<UserRegistrationCodeInfo>> clazz, HttpInputMessage inputMessage) throws IOException, HttpMessageNotReadableException {
        Class domainClass = GenericTypeResolver.resolveTypeArgument(clazz, Iterable.class);

        return null;
    }

    @Override
    protected void writeInternal(List<UserRegistrationCodeInfo> rows, HttpOutputMessage outputMessage) throws IOException, HttpMessageNotWritableException {
        LOGGER.debug("writeInternal, rows: {}", rows);
        Class rowClass = UserRegistrationCodeInfo.class;//GenericTypeResolver.resolveTypeArgument(rows.getClass(), Iterable.class);
        LOGGER.debug("writeInternal, row class: {}", rowClass.getName());

        // get output stream
        OutputStream out = outputMessage.getBody();
        OutputStream buffOs = new BufferedOutputStream(out);
        OutputStreamWriter writer = new OutputStreamWriter(buffOs);

        // Schema from POJO (usually has @JsonPropertyOrder annotation)
        CsvMapper mapper = new CsvMapper();
        CsvSchema schema = mapper.schemaFor(rowClass).withHeader();

        // write and cleanup
        mapper.writer(schema).writeValue(writer, rows);
        writer.close();

    }

}