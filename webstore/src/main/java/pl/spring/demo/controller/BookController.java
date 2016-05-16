package pl.spring.demo.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;
import pl.spring.demo.enumerations.BookStatus;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

/**
 * Book controller
 * 
 * @author mmotowid
 *
 */
@Controller
@RequestMapping("/books")
// @ResponseBody
public class BookController {

	@Autowired
	private BookService bookService;

	@ModelAttribute("newBook")
	public BookTo construct(){
		return new BookTo();
	}
	
	@RequestMapping
	public String list(Model model) {

		return ViewNames.BOOKS;
	}

	/**
	 * Method collects info about all books
	 */
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public ModelAndView allBooks() {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(ModelConstants.BOOK_LIST, bookService.findAllBooks());
		modelAndView.setViewName(ViewNames.BOOKS);
		return modelAndView;
	}

	// TODO: here implement methods which displays book info based on query
	// arguments
	@RequestMapping(value = "/book")
	public ModelAndView detailsBook(@RequestParam("id") Long id) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(ModelConstants.BOOK, bookService.findBookById(id));
		modelAndView.setViewName(ViewNames.BOOK);
		return modelAndView;
	}
	
	@RequestMapping(value = "/delete")
	public ModelAndView deleteBook(@RequestParam("id") Long id) {
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.addObject(ModelConstants.BOOK, bookService.findBookById(id));
		bookService.deleteBook(id);
		modelAndView.setViewName(ViewNames.DELETE);
		return modelAndView;
	}

	// TODO: Implement GET / POST methods for "add book" functionality
	@RequestMapping(value = "/add", method = RequestMethod.GET)
	public String book(Model model) {
		model.addAttribute(ModelConstants.NEW_BOOK, new BookTo());
		return ViewNames.ADD_BOOK;
	}

	@RequestMapping(value = "/add", method = RequestMethod.POST)
	public ModelAndView addBook(@ModelAttribute("newBook") BookTo newBook, ModelMap model) {
		String authors = newBook.getAuthors();
		String title = newBook.getTitle();
		BookStatus bookStatus = newBook.getStatus();
		if (bookStatus == null || title.isEmpty() || authors.isEmpty()){
			ModelAndView modelAndView = new ModelAndView();
			modelAndView.addObject(ModelConstants.ANNOUNCEMENT, "All parameters must be filed!");
			modelAndView.setViewName(ViewNames.ADD_BOOK);
			return modelAndView;
		}
		bookService.saveBook(newBook);
		return allBooks();
	}

	/**
	 * Binder initialization
	 */
	@InitBinder
	public void initialiseBinder(WebDataBinder binder) {
		binder.setAllowedFields("id", "title", "authors", "status");
	}

}
