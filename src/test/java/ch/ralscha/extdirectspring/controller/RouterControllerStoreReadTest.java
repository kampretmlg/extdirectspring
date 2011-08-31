/**
 * Copyright 2010-2011 Ralph Schaer <ralphschaer@gmail.com>
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

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ch.ralscha.extdirectspring.bean.ExtDirectResponse;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreReadRequest;
import ch.ralscha.extdirectspring.bean.ExtDirectStoreResponse;
import ch.ralscha.extdirectspring.bean.GroupInfo;
import ch.ralscha.extdirectspring.bean.SortDirection;
import ch.ralscha.extdirectspring.bean.SortInfo;
import ch.ralscha.extdirectspring.provider.Row;

/**
 * Tests for {@link RouterController}.
 * 
 * @author Ralph Schaer
 */
@SuppressWarnings("all")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:/testApplicationContext.xml")
public class RouterControllerStoreReadTest {

	@Autowired
	private RouterController controller;

	private MockHttpServletResponse response;
	private MockHttpServletRequest request;

	@Before
	public void beforeTest() {
		response = new MockHttpServletResponse();
		request = new MockHttpServletRequest();
	}

	@Test
	public void testNoArgumentsNoRequestParameters() {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		List<Row> rows = (List<Row>) resp.getResult();
		assertEquals(100, rows.size());

	}

	@Test
	public void testNoArgumentsWithRequestParameters() {

		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("ralph");

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method1", 1,
				storeRead);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method1", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		List<Row> rows = (List<Row>) resp.getResult();
		assertEquals(100, rows.size());
	}

	@Test
	public void testReturnsNull() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method2", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method2", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());
	}

	@Test
	public void testSupportedArguments() {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method3", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);
		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method3", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		List<Row> rows = (List<Row>) resp.getResult();
		assertEquals(100, rows.size());
	}

	@Test
	public void testWithExtDirectStoreReadRequest() {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("name");
		ExtDirectResponse resp = executeWithExtDirectStoreReadRequest(storeRead);
		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(50), storeResponse.getTotal());
		assertEquals(50, storeResponse.getRecords().size());
		for (Row row : storeResponse.getRecords()) {
			assertTrue(row.getName().startsWith("name"));
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("firstname");
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(50), storeResponse.getTotal());
		assertEquals(50, storeResponse.getRecords().size());
		for (Row row : storeResponse.getRecords()) {
			assertTrue(row.getName().startsWith("firstname"));
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("ASC");
		storeRead.setLimit(10);
		storeRead.setStart(10);
		assertTrue(storeRead.isAscendingSort());
		assertFalse(storeRead.isDescendingSort());

		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("DESC");
		storeRead.setLimit(10);
		storeRead.setStart(20);
		assertFalse(storeRead.isAscendingSort());
		assertTrue(storeRead.isDescendingSort());

		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setGroupBy("id");
		storeRead.setGroupDir("ASC");
		storeRead.setLimit(10);
		storeRead.setStart(10);
		assertTrue(storeRead.isAscendingGroupSort());
		assertFalse(storeRead.isDescendingGroupSort());

		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setGroupBy("id");
		storeRead.setGroupDir("DESC");
		storeRead.setLimit(10);
		storeRead.setStart(20);
		assertFalse(storeRead.isAscendingGroupSort());
		assertTrue(storeRead.isDescendingGroupSort());

		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("ASC");
		storeRead.setPage(1);
		storeRead.setLimit(10);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 0;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		storeRead.setSort("id");
		storeRead.setDir("ASC");
		storeRead.setPage(2);
		storeRead.setLimit(10);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}
	}

	@Test
	public void testWithExtDirectStoreReadRequestMultipeGroups() {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		List<GroupInfo> groups = new ArrayList<GroupInfo>();
		groups.add(new GroupInfo("id", SortDirection.ASCENDING));
		storeRead.setGroups(groups);
		storeRead.setLimit(10);
		storeRead.setStart(10);
		ExtDirectResponse resp = executeWithExtDirectStoreReadRequest(storeRead);
		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		groups = new ArrayList<GroupInfo>();
		groups.add(new GroupInfo("id", SortDirection.DESCENDING));
		storeRead.setGroups(groups);
		storeRead.setLimit(10);
		storeRead.setStart(20);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id--;
		}
	}

	@Test
	public void testWithExtDirectStoreReadRequestMultipleSorters() {
		ExtDirectStoreReadRequest storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");

		List<SortInfo> sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.ASCENDING));
		storeRead.setSorters(sorters);

		storeRead.setLimit(10);
		storeRead.setPage(2);
		ExtDirectResponse resp = executeWithExtDirectStoreReadRequest(storeRead);
		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		int id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.DESCENDING));
		storeRead.setSorters(sorters);
		storeRead.setLimit(10);
		storeRead.setPage(3);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.ASCENDING));
		storeRead.setSorters(sorters);
		storeRead.setLimit(10);
		storeRead.setPage(2);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.DESCENDING));
		storeRead.setSorters(sorters);
		storeRead.setLimit(10);
		storeRead.setPage(3);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 79;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id--;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.ASCENDING));
		storeRead.setSorters(sorters);
		storeRead.setPage(1);
		storeRead.setLimit(10);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 0;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}

		storeRead = new ExtDirectStoreReadRequest();
		storeRead.setQuery("");
		sorters = new ArrayList<SortInfo>();
		sorters.add(new SortInfo("id", SortDirection.ASCENDING));
		storeRead.setSorters(sorters);
		storeRead.setPage(2);
		storeRead.setLimit(10);
		resp = executeWithExtDirectStoreReadRequest(storeRead);
		storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(100), storeResponse.getTotal());
		assertEquals(10, storeResponse.getRecords().size());
		id = 10;
		for (Row row : storeResponse.getRecords()) {
			assertEquals(id, row.getId());
			id++;
		}
	}

	private ExtDirectResponse executeWithExtDirectStoreReadRequest(ExtDirectStoreReadRequest storeRead) {
		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method4", 1,
				storeRead);
		Map<String, Object> data = (Map<String, Object>) ((List) edRequest.get("data")).get(0);
		List<Map<String, Object>> sorters = (List<Map<String, Object>>) data.get("sorters");

		if (sorters != null && !sorters.isEmpty()) {
			for (Map<String, Object> map : sorters) {
				if ("DESCENDING".equals(map.get("direction"))) {
					map.put("direction", "DESC");
				} else {
					map.put("direction", "ASC");
				}
			}
			data.remove("sorters");
			data.put("sort", sorters);
		}

		List<Map<String, Object>> groups = (List<Map<String, Object>>) data.get("groups");
		if (groups != null && !groups.isEmpty()) {
			for (Map<String, Object> map : groups) {
				if ("DESCENDING".equals(map.get("direction"))) {
					map.put("direction", "DESC");
				} else {
					map.put("direction", "ASC");
				}
			}
			data.remove("groups");
			data.put("group", groups);
		}

		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method4", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());
		return resp;
	}

	@Test
	public void testWithAdditionalParameters() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 10);
		readRequest.put("query", "name");

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method5", 1,
				readRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method5", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(50), storeResponse.getTotal());
		assertEquals(50, storeResponse.getRecords().size());
		for (Row row : storeResponse.getRecords()) {
			assertTrue(row.getName().startsWith("name"));
		}

		readRequest = new HashMap<String, Object>();
		readRequest.put("query", "name");

		edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method5", 1, readRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method5", resp.getMethod());
		assertEquals("exception", resp.getType());
		assertEquals(1, resp.getTid());
		assertEquals("Server Error", resp.getMessage());
	}

	@Test
	public void testWithAdditionalParametersDefaultValue() {
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("query", "firstname");

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method6", 1,
				readRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method6", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(Integer.valueOf(50), storeResponse.getTotal());
		assertEquals(50, storeResponse.getRecords().size());
		for (Row row : storeResponse.getRecords()) {
			assertTrue(row.getName().startsWith("firstname"));
		}
	}

	@Test
	public void testWithAdditionalParametersOptional() {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method7", 1, null);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method7", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		List<Row> rows = (List<Row>) resp.getResult();
		assertEquals(100, rows.size());

		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("id", 11);
		readRequest.put("query", "");

		edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method7", 1, readRequest);
		responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method7", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		rows = (List<Row>) resp.getResult();
		assertEquals(100, rows.size());

	}

	@Test
	public void testWithAdditionalParametersAndConversion() {
		DateTime today = new DateTime();
		Map<String, Object> readRequest = new HashMap<String, Object>();
		readRequest.put("endDate", ISODateTimeFormat.dateTime().print(today));

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "method8", 1,
				readRequest);
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("method8", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());
		ExtDirectStoreResponse<Row> storeResponse = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(50, storeResponse.getRecords().size());

	}

	@Test
	public void testMetadata() {

		Map<String, Object> edRequest = ControllerUtil.createRequestJson("remoteProviderStoreRead", "methodMetadata",
				1, new HashMap<String, Object>());
		List<ExtDirectResponse> responses = controller.router(request, response, Locale.ENGLISH, edRequest);

		assertEquals(1, responses.size());
		ExtDirectResponse resp = responses.get(0);

		assertEquals("remoteProviderStoreRead", resp.getAction());
		assertEquals("methodMetadata", resp.getMethod());
		assertEquals("rpc", resp.getType());
		assertEquals(1, resp.getTid());
		assertNull(resp.getMessage());
		assertNull(resp.getWhere());
		assertNotNull(resp.getResult());

		ExtDirectStoreResponse<Row> response = (ExtDirectStoreResponse<Row>) resp.getResult();
		assertEquals(50, response.getRecords().size());
		assertEquals(100, response.getTotal().intValue());
		Map<String, Object> metadata = response.getMetaData();
		assertNotNull(metadata);

		assertThat(metadata).includes(entry("root", "records"));
		assertThat(metadata).includes(entry("totalProperty", "total"));
		assertThat(metadata).includes(entry("successProperty", "success"));
		assertThat(metadata).includes(entry("start", 0));
		assertThat(metadata).includes(entry("limit", 50));

		Map<String, String> sortInfo = (Map<String, String>) metadata.get("sortInfo");
		assertEquals(2, sortInfo.size());
		assertThat(sortInfo).includes(entry("field", "name"));
		assertThat(sortInfo).includes(entry("direction", "ASC"));

		List<Map<String, Object>> fields = (List<Map<String, Object>>) metadata.get("fields");
		assertEquals(4, fields.size());

		Map<String, Object> field1 = fields.get(0);
		assertThat(field1).includes(entry("name", "id"));
		assertThat(field1).includes(entry("type", "int"));
		assertThat(field1).includes(entry("header", "ID"));
		assertThat(field1).includes(entry("width", 20));
		assertThat(field1).includes(entry("sortable", true));
		assertThat(field1).includes(entry("resizable", true));
		assertThat(field1).includes(entry("hideable", false));

		Map<String, Object> field2 = fields.get(1);
		assertThat(field2).includes(entry("name", "name"));
		assertThat(field2).includes(entry("type", "string"));
		assertThat(field2).includes(entry("header", "Name"));
		assertThat(field2).includes(entry("width", 70));
		assertThat(field2).includes(entry("sortable", true));
		assertThat(field2).includes(entry("resizable", true));
		assertThat(field2).includes(entry("hideable", false));

		Map<String, Object> field3 = fields.get(2);
		assertThat(field3).includes(entry("name", "admin"));
		assertThat(field3).includes(entry("type", "boolean"));
		assertThat(field3).includes(entry("header", "Administrator"));
		assertThat(field3).includes(entry("width", 30));
		assertThat(field3).includes(entry("sortable", true));
		assertThat(field3).includes(entry("resizable", true));
		assertThat(field3).includes(entry("hideable", true));

		Map<String, Object> field4 = fields.get(3);
		assertThat(field4).includes(entry("name", "salary"));
		assertThat(field4).includes(entry("type", "float"));
		assertThat(field4).includes(entry("header", "Salary"));
		assertThat(field4).includes(entry("width", 50));
		assertThat(field4).includes(entry("sortable", false));
		assertThat(field4).includes(entry("resizable", true));
		assertThat(field4).includes(entry("hideable", true));

	}

}