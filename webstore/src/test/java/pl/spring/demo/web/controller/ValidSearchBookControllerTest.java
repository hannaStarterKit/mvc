package pl.spring.demo.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatcher;
import org.mockito.Mockito;
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
import pl.spring.demo.controller.SearchBookController;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "controller-test-configuration.xml")
@WebAppConfiguration
public class ValidSearchBookControllerTest {

	@Autowired
	private BookService bookService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		Mockito.reset(bookService);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");

		SearchBookController bookController = new SearchBookController();
		mockMvc = MockMvcBuilders.standaloneSetup(bookController).setViewResolvers(viewResolver).build();
		// Due to fact, that We are trying to construct real Bean - Book
		// Controller, we have to use reflection to mock existing field book
		// service
		ReflectionTestUtils.setField(bookController, "bookService", bookService);
	}

	@Test
	public void testShouldFindBookByTitleAndAuthors() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "Test title", "Test Author", BookStatus.FREE);
		BookTo testBookSearch = new BookTo(null, "t", "a", null);

		Mockito.when(bookService.findBooksByTitleAndAuthor(Mockito.any(), Mockito.any()))
				.thenReturn(Arrays.asList(testBook));
		// when
		ResultActions resultActions = mockMvc
				.perform(post("/search/searchBooks").flashAttr(ModelConstants.SEARCH_BOOK, testBookSearch));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOKS))
				.andExpect(model().attribute(ModelConstants.BOOK_LIST, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> books = (List<BookTo>) argument;
						return books.isEmpty() != true && testBook.getTitle().equals(books.get(0).getTitle());
					}
				}));

	}

	@Test
	public void testShouldFindBookByTitle() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "Test title", "Test Author", BookStatus.FREE);
		BookTo testBookSearch = new BookTo(null, "t", "", null);
		Mockito.when(bookService.findBooksByTitle(Mockito.any())).thenReturn(Arrays.asList(testBook));
		// when
		ResultActions resultActions = mockMvc
				.perform(post("/search/searchBooks").flashAttr(ModelConstants.SEARCH_BOOK, testBookSearch));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOKS))
				.andExpect(model().attribute(ModelConstants.BOOK_LIST, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> books = (List<BookTo>) argument;
						return books.isEmpty() != true && testBook.getTitle().equals(books.get(0).getTitle());
					}
				}));

	}

	@Test
	public void testShouldFindBookByAuthor() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "title", "author", BookStatus.FREE);
		BookTo testBookSearch = new BookTo(null, "", "a", null);
		Mockito.when(bookService.findBooksByAuthor(Mockito.any())).thenReturn(Arrays.asList(testBook));
		// when
		ResultActions resultActions = mockMvc
				.perform(post("/search/searchBooks").flashAttr(ModelConstants.SEARCH_BOOK, testBookSearch));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOKS))
				.andExpect(model().attribute(ModelConstants.BOOK_LIST, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						List<BookTo> books = (List<BookTo>) argument;
						return books.isEmpty() != true && testBook.getTitle().equals(books.get(0).getTitle());
					}
				}));

	}

	@Test
	public void testShouldNotFindAnyBook() throws Exception {
		// given
		BookTo testBookSearch = new BookTo(null, "", "", null);
		// when
		ResultActions resultActions = mockMvc
				.perform(post("/search/searchBooks").flashAttr(ModelConstants.SEARCH_BOOK, testBookSearch));
		// then
		resultActions.andExpect(view().name(ViewNames.SEARCH))
				.andExpect(model().attribute(ModelConstants.ANNOUNCEMENT, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						String announcement = (String) argument;
						return null != announcement && announcement.length() > 0;
					}
				}));

	}

	/**
	 * Sample method which convert's any object from Java to String
	 */
	private static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
