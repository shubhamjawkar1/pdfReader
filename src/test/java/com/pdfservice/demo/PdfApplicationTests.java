package com.pdfservice.demo;

import com.pdfservice.pdf.PdfApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
class PdfApplicationTests {

	@Test
	void contextLoads() {
	}
/*	@Autowired
	private MockMvc mockMvc;

	@Test
	public void testViewPdf_NoFileProvided() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.multipart("/pdf/view"))
				.andExpect(status().isBadRequest());
	}*/

}
