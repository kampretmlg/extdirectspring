/**
 * Copyright 2010-2013 Ralph Schaer <ralphschaer@gmail.com>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package ch.ralscha.extdirectspring.controller;

import static org.fest.assertions.api.Assertions.assertThat;
import static org.fest.assertions.api.Assertions.entry;
import static org.junit.Assert.fail;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.context.WebApplicationContext;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.provider.FormInfo;

import com.fasterxml.jackson.core.JsonProcessingException;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration("classpath:/testApplicationContext.xml")
public class RouterControllerFormPostJsonTest {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Autowired
	private ConfigurationService configurationService;

	@Before
	public void setupMockMvc() throws Exception {
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
	}

	@Test
	public void testCallExistsFormPostMethod() throws Exception {
		MockHttpServletRequestBuilder request = post("/router").accept(MediaType.ALL)
				.contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8");

		request.param("extTID", "14");
		request.param("extAction", "formInfoController3");
		request.param("extMethod", "updateInfoJson");
		request.param("extType", "rpc");

		mockMvc.perform(request).andExpect(status().isOk());
	}

	@SuppressWarnings({"unchecked", "null"})
    @Test
	public void testCallFormPostMethod() throws Exception {

		FormInfo formInfo = new FormInfo("Ralph", 20, true, new BigDecimal(12.3), "theResult");
		
		//Request Params are sent as part of the json content payload
		formInfo.set("p1", 1000);
		formInfo.set("p2", "2nd mandatory param");
		
		MvcResult resultMvc = null;
		try {
			resultMvc = ControllerUtil.performRouterRequest(mockMvc,
					ControllerUtil.createEdsRequest("formInfoController3", "updateInfoJsonDirect", 14, formInfo));
		} catch (JsonProcessingException e) {
			fail("perform post to /router" + e.getMessage());
		} catch (Exception e) {
			fail("perform post to /router" + e.getMessage());
		}

		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(resultMvc.getResponse()
				.getContentAsByteArray());
		assertThat(responses).hasSize(1);

		ExtDirectResponse edsResponse = responses.get(0);

		assertThat(edsResponse.getAction()).isEqualTo("formInfoController3");
		assertThat(edsResponse.getMethod()).isEqualTo("updateInfoJsonDirect");
		assertThat(edsResponse.getTid()).isEqualTo(14);
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getType()).isEqualTo("rpc");
		assertThat(edsResponse.getMessage()).isNull();

		Map<String, Object> result = (Map<String, Object>) edsResponse.getResult();
		assertThat(result).hasSize(6).contains(entry("name", "RALPH"), entry("age", 30), entry("admin", false),
				entry("salary", 1012.3), entry("result", "theResultRESULT"), entry("success", true));
	}

	public void testCallFormPostMethodOld() throws Exception {

        FormInfo formInfo = new FormInfo("Ralph", 20, true, new BigDecimal(12.3), "theResult");
        
        MockHttpServletRequestBuilder request = post("/router").accept(MediaType.ALL)
                .contentType(MediaType.APPLICATION_JSON).characterEncoding("UTF-8");

        request.param("extTID", "14");
        request.param("extAction", "formInfoController3");
        request.param("extMethod", "updateInfoJsonDirect");
        request.param("extType", "rpc");
        request.param("p1", "1000");
        request.param("p2", "2nd mandatory param");
        request.content(new String(ControllerUtil.writeAsByte(formInfo)));

        MvcResult resultMvc = null;
        try {
            resultMvc = mockMvc.perform(request).andExpect(status().isOk())
            .andExpect(content().contentType("application/json;charset=UTF-8")).andExpect(content().encoding("UTF-8"))
            .andReturn();
        } catch (JsonProcessingException e) {
            fail("perform post to /router" + e.getMessage());
        } catch (Exception e) {
            fail("perform post to /router" + e.getMessage());
        }

        ExtDirectResponse edsResponse = ControllerUtil.readDirectResponse(resultMvc.getResponse()
                .getContentAsByteArray());

        assertThat(edsResponse.getAction()).isEqualTo("formInfoController3");
        assertThat(edsResponse.getMethod()).isEqualTo("updateInfoJsonDirect");
        assertThat(edsResponse.getTid()).isEqualTo(14);
        assertThat(edsResponse.getWhere()).isNull();
        assertThat(edsResponse.getType()).isEqualTo("rpc");
        assertThat(edsResponse.getMessage()).isNull();

        Map<String, Object> result = (Map<String, Object>) edsResponse.getResult();
        assertThat(result).hasSize(6).contains(entry("name", "RALPH"), entry("age", 30), entry("admin", false),
                entry("salary", 1012.3), entry("result", "theResultRESULT"), entry("success", true));
    }
	@SuppressWarnings({ "unchecked", "null", "rawtypes" })
	@Test
	public void testCallFormPostMethodError() throws Exception {

		FormInfo formInfo = new FormInfo("Ralph", 20, true, new BigDecimal(12.3), "theResult");

		MvcResult resultMvc = null;
		try {
			resultMvc = ControllerUtil.performRouterRequest(mockMvc,
					ControllerUtil.createEdsRequest("formInfoController3", "updateInfoJsonDirectError", 14, formInfo));
		} catch (JsonProcessingException e) {
			fail("perform post to /router" + e.getMessage());
		} catch (Exception e) {
			fail("perform post to /router" + e.getMessage());
		}

		List<ExtDirectResponse> responses = ControllerUtil.readDirectResponses(resultMvc.getResponse()
				.getContentAsByteArray());
		assertThat(responses).hasSize(1);

		ExtDirectResponse edsResponse = responses.get(0);

		assertThat(edsResponse.getAction()).isEqualTo("formInfoController3");
		assertThat(edsResponse.getMethod()).isEqualTo("updateInfoJsonDirectError");
		assertThat(edsResponse.getTid()).isEqualTo(14);
		assertThat(edsResponse.getWhere()).isNull();
		assertThat(edsResponse.getType()).isEqualTo("rpc");

		Map<String, Object> result = (Map<String, Object>) edsResponse.getResult();
		assertThat(result).hasSize(2).contains(entry("success", false));
		assertThat(result).hasSize(2).containsKey("errors");
		Map age = (Map) result.get("errors");
		assertThat(age).hasSize(1).containsKey("age");
		ArrayList value = (ArrayList) age.get("age");
		assertThat(value).contains("age is wrong");
	}

	@Test
	public void testCallFormPostMethodNotRegisteredWithBindingResultAsParameter() throws Exception {
		ControllerUtil.sendAndReceive(mockMvc, "formInfoController3",
				"updateInfoJsonDirectNotRegisteredWithBindingResultAsParameter", null);
	}

	@Test
	public void testCallFormPostMethodNotRegisteredWithMultipartFileAsParameter() throws Exception {
		ControllerUtil.sendAndReceive(mockMvc, "formInfoController3",
				"updateInfoJsonDirectNotRegisteredWithMultipartFileAsParameter", null);
	}
}
