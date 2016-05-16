package pl.spring.demo.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mock;
import org.mockito.Mockito;
import static org.mockito.Mockito.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import com.fasterxml.jackson.databind.ObjectMapper;

import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;
import pl.spring.demo.controller.BookController;
import pl.spring.demo.controller.LoginController;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "controller-test-configuration.xml")
@WebAppConfiguration
public class LoginControllerTest {

	private MockMvc mockMvc;
	// private Principal userMock;

	@Before
	public void setup() {
		// userMock = Mockito.mock(Principal.class);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");

		LoginController loginController = new LoginController();
		mockMvc = MockMvcBuilders.standaloneSetup(loginController).setViewResolvers(viewResolver).build();

	}

	@Test
	public void testLoginFailedage() throws Exception {
		// given
		// when(user.getName()).thenReturn("Name");

		ResultActions resultActions = mockMvc.perform(get("/loginFailed"));
		// then
		resultActions.andExpect(model().attribute("error", new ArgumentMatcher<Object>() {
			@Override
			public boolean matches(Object argument) {
				String text = (String) argument;
				return null != text && text.length() > 0;
			}
		}));

	}

	@Test
	@Ignore
	public void test403Page() throws Exception {
		// given
		// when(user.getName()).thenReturn("Name");

		ResultActions resultActions = mockMvc.perform(get("/403"));
		// then
		resultActions.andExpect(model().attribute(ModelConstants.ERROR_MESSAGE, new ArgumentMatcher<Object>() {
			@Override
			public boolean matches(Object argument) {
				String errorMessage = (String) argument;
				return null != errorMessage && errorMessage.length() > 0;
			}
		}));

	}

}
