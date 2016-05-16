package pl.spring.demo.rest;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

@Controller
@ResponseBody
public class BookRestService {

	// TODO: Inject properly book service
	@Autowired
	private BookService bookService;

	// TODO: implement all necessary CRUD operations as a rest service

	@RequestMapping(value = "/findAll", method = RequestMethod.GET)
	public List<BookTo> findAllBooks() {
		return bookService.findAllBooks();
	}

	@RequestMapping(value = "/findTitle", method = RequestMethod.GET)
	public List<BookTo> findBooksByTitle(@RequestParam("title") String title) {
		return bookService.findBooksByTitle(title);
	}

	@RequestMapping(value = "/findBooks", method = RequestMethod.GET)
	public List<BookTo> findBooksByTitleAndAuthors(@RequestParam("title") String title,
			@RequestParam("author") String author) {
		return bookService.findBooksByTitleAndAuthor(title, author);
	}

	@RequestMapping(value = "/findBooksArray", method = RequestMethod.GET)
	public List<BookTo> findBooksByTitleAndAuthors(@RequestParam(value = "params") String[] paramArray) {
		return bookService.findBooksByTitleAndAuthor(paramArray[0], paramArray[1]);
	}

	@RequestMapping(value = "/book", method = RequestMethod.POST)
	public BookTo saveBook(@RequestBody BookTo book) {
		return bookService.saveBook(book);
	}

	@RequestMapping(value = "/book", method = RequestMethod.PUT)
	public BookTo addBook(@RequestBody BookTo book) {
		return bookService.saveBook(book);
	}

	@RequestMapping(value = "/book", method = RequestMethod.DELETE)
	public void deleteBook(@RequestBody BookTo book) {
		bookService.deleteBook(book.getId());
	}

	// TODO: implement some search methods considering single request parameters
	// / multiple request parameters / array request parameters

}
