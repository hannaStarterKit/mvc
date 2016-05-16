package pl.spring.demo.web.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
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
import pl.spring.demo.controller.BookController;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "controller-test-configuration.xml")
@WebAppConfiguration
public class ValidBookControllerTest {

	@Autowired
	private BookService bookService;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		Mockito.reset(bookService);

		InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
		viewResolver.setPrefix("/WEB-INF/views/");
		viewResolver.setSuffix(".jsp");

		BookController bookController = new BookController();
		mockMvc = MockMvcBuilders.standaloneSetup(bookController).setViewResolvers(viewResolver).build();
		// Due to fact, that We are trying to construct real Bean - Book
		// Controller, we have to use reflection to mock existing field book
		// service
		ReflectionTestUtils.setField(bookController, "bookService", bookService);
	}

	@Test
	public void testAddBookPage() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "Test title", "Test Author", BookStatus.FREE);
		Mockito.when(bookService.saveBook(Mockito.any())).thenReturn(testBook);

		// TODO: please take a look how we pass @ModelAttribute as a request
		// attribute
		ResultActions resultActions = mockMvc.perform(post("/books/add").flashAttr(ModelConstants.NEW_BOOK, testBook));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOKS))
				.andExpect(model().attribute(ModelConstants.NEW_BOOK, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						BookTo book = (BookTo) argument;
						return null != book && testBook.getTitle().equals(book.getTitle());
					}
				}));

	}

	@Test
	public void testShouldNotAddBook() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "", "Test Author", BookStatus.FREE);

		// when
		ResultActions resultActions = mockMvc.perform(post("/books/add").flashAttr(ModelConstants.NEW_BOOK, testBook));
		// then
		resultActions.andExpect(view().name(ViewNames.ADD_BOOK))
				.andExpect(model().attribute(ModelConstants.ANNOUNCEMENT, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						String announcement = (String) argument;
						return null != announcement && announcement.length() > 0;
					}
				}));

	}

	@Test
	public void testShouldDeleteBook() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "Test title", "Test Author", BookStatus.FREE);
		Mockito.when(bookService.findBookById(Mockito.any(Long.class))).thenReturn(testBook);

		ResultActions resultActions = mockMvc.perform(get("/books/delete?id=" + testBook.getId().toString()));
		// then
		resultActions.andExpect(view().name(ViewNames.DELETE))
				.andExpect(model().attribute(ModelConstants.BOOK, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						BookTo book = (BookTo) argument;
						return null != book && testBook.getTitle().equals(book.getTitle());
					}
				}));
		Mockito.verify(bookService).deleteBook(1L);

	}

	@Test
	public void testShouldFindAllBooks() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "Test title", "Test Author", BookStatus.FREE);
		Mockito.when(bookService.findAllBooks()).thenReturn(Arrays.asList(testBook));
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/all"));
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
	public void testShouldShowBookDetails() throws Exception {
		// given
		BookTo testBook = new BookTo(1L, "Test title", "Test Author", BookStatus.FREE);
		Mockito.when(bookService.findBookById(Mockito.any(Long.class))).thenReturn(testBook);
		// when
		ResultActions resultActions = mockMvc.perform(get("/books/book?id=" + testBook.getId().toString()));
		// then
		resultActions.andExpect(view().name(ViewNames.BOOK))
				.andExpect(model().attribute(ModelConstants.BOOK, new ArgumentMatcher<Object>() {
					@Override
					public boolean matches(Object argument) {
						BookTo book = (BookTo) argument;
						return null != book && testBook.getTitle().equals(book.getTitle());
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
