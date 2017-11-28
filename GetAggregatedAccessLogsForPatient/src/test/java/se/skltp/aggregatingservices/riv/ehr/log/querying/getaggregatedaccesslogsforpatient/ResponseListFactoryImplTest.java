/**
 * Copyright (c) 2014 Inera AB, <http://inera.se/>
 *
 * This file is part of SKLTP.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
 */
package se.skltp.aggregatingservices.riv.ehr.log.querying.getaggregatedaccesslogsforpatient;

import org.junit.Test;
import org.soitoolkit.commons.mule.jaxb.JaxbUtil;
import riv.ehr.log.querying.getaccesslogsforpatientresponder.v1.GetAccessLogsForPatientResponseType;
import riv.ehr.log.querying.v1.AccessLogType;
import riv.ehr.log.querying.v1.AccessLogsResultType;
import riv.ehr.log.querying.v1.AccessLogsType;
import se.skltp.agp.riv.interoperability.headers.v1.ProcessingStatusType;
import se.skltp.agp.riv.itintegration.engagementindex.findcontentresponder.v1.FindContentType;
import se.skltp.agp.service.api.QueryObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

public class ResponseListFactoryImplTest {

    private static final JaxbUtil jaxbUtil = new JaxbUtil(GetAccessLogsForPatientResponseType.class, ProcessingStatusType.class);

    @Test
    public void getXmlFromAggregatedResponse() {
        FindContentType fc = new FindContentType();
        fc.setRegisteredResidentIdentification("1212121212");
        QueryObject queryObject = new QueryObject(fc, null);
        List<Object> responseList = new ArrayList<Object>(2);
        responseList.add(createGetAccessLogsForPatientResponse());
        responseList.add(createGetAccessLogsForPatientResponse());
        ResponseListFactoryImpl responseListFactory = new ResponseListFactoryImpl();

        String responseXML = responseListFactory.getXmlFromAggregatedResponse(queryObject, responseList);
        GetAccessLogsForPatientResponseType response = (GetAccessLogsForPatientResponseType) jaxbUtil.unmarshal(responseXML);
        assertEquals(2, response.getAccessLogsResultType().size());
    }

    private GetAccessLogsForPatientResponseType createGetAccessLogsForPatientResponse() {
        GetAccessLogsForPatientResponseType getCareDocResponse = new GetAccessLogsForPatientResponseType();
        getCareDocResponse.getAccessLogsResultType().add(new AccessLogsResultType());
        getCareDocResponse.getAccessLogsResultType().get(0).setAccesssLogs(new AccessLogsType());
        getCareDocResponse.getAccessLogsResultType().get(0).getAccesssLogs().getAccessLog().add(new AccessLogType());

        return getCareDocResponse;
    }

    @Test
    public void getXmlFromAggregatedResponse_incorrect() throws Exception {

        InputStream stream = ResponseListFactoryImplTest.class.getResourceAsStream("/GetAccessLogsForPatientResponse-example-incorrect.xml");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        GetAccessLogsForPatientResponseType response = (GetAccessLogsForPatientResponseType) jaxbUtil.unmarshal(sb.toString());
        assertTrue(response.getAccessLogsResultType().size()==0);
    }

    @Test
    public void getXmlFromAggregatedResponse_correct() throws Exception {

        InputStream stream = ResponseListFactoryImplTest.class.getResourceAsStream("/GetAccessLogsForPatientResponse-example-correct.xml");
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));

        StringBuilder sb = new StringBuilder();

        String line;
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }

        GetAccessLogsForPatientResponseType response = (GetAccessLogsForPatientResponseType) jaxbUtil.unmarshal(sb.toString());
        assertEquals(1, response.getAccessLogsResultType().size());
    }
}
