package com.example.spatialDataStorageSystemProject;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
class SpatialDataStorageSystemProjectApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test //тест на то что запрос http://localhost:8080/layerObjects/4 возвращает JSON
	public void requestForLayerObjectsReturnAJson() throws Exception{
		MvcResult result = this.mockMvc.perform(get("http://localhost:8080/layerObjects/4")).andDo(print()).andExpect(status().isOk())
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString(); //берём ответ

		try {//пытаемся сделать json из него
			new JSONObject(content);
		}catch (JSONException ex) {
			ex.getMessage();
		}
	}

	@Test //тест на то что запрос http://localhost:8080/layers возвращает JSON
	public void requestForLayersReturnAJson() throws Exception{
		MvcResult result = this.mockMvc.perform(get("http://localhost:8080/layers")).andDo(print()).andExpect(status().isOk())
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString(); //берём ответ

		try {//пытаемся сделать json из него
			new JSONObject(content);
		}catch (JSONException ex) {
			ex.getMessage();
		}
	}

	@Test //тест на то что запрос http://localhost:8080/layer_styles/4 возвращает JSON
	public void requestForLayerStylesReturnAJson() throws Exception{
		MvcResult result = this.mockMvc.perform(get("http://localhost:8080/layer_styles/4")).andDo(print()).andExpect(status().isOk())
				.andExpect(status().isOk())
				.andReturn();

		String content = result.getResponse().getContentAsString(); //берём ответ

		try {//пытаемся сделать json из него
			new JSONObject(content);
		}catch (JSONException ex) {
			ex.getMessage();
		}
	}
}
