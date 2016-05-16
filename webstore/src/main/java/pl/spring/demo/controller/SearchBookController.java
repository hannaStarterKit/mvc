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
import org.springframework.web.servlet.ModelAndView;

import pl.spring.demo.constants.ModelConstants;
import pl.spring.demo.constants.ViewNames;
import pl.spring.demo.service.BookService;
import pl.spring.demo.to.BookTo;

/**
 * search Book controller
 * 
 * @author hsienkie
 *
 */
@Controller
@RequestMapping("/search")
//@ResponseBody
public class SearchBookController {

	@Autowired
	private BookService bookService;
	

	@ModelAttribute("searchBooks")
	public BookTo construct(){
		return new BookTo();
	}

	/**
	 * Method searches books by title and author
	 */
	@RequestMapping(value = "/searchBooks", method = RequestMethod.GET)
	public String book(Model model) {
		model.addAttribute(ModelConstants.SEARCH_BOOK, new BookTo());
		return ViewNames.SEARCH;
	}

	@RequestMapping(value = "/searchBooks", method = RequestMethod.POST)
	public ModelAndView searchBook(@ModelAttribute("searchBooks") BookTo searchBooks, ModelMap model) {
		ModelAndView modelAndView = new ModelAndView();
		String title = searchBooks.getTitle();
		String authors  = searchBooks.getAuthors();
		if (title.isEmpty() && authors.isEmpty()){
			modelAndView.addObject(ModelConstants.ANNOUNCEMENT, "Parameters are empty!");
			modelAndView.setViewName(ViewNames.SEARCH);
		}
		if (title.isEmpty() && !authors.isEmpty()){
			modelAndView.addObject(ModelConstants.BOOK_LIST, bookService.findBooksByAuthor(authors));
			modelAndView.setViewName(ViewNames.BOOKS);
		}
		if (authors.isEmpty() && !title.isEmpty()){
			modelAndView.addObject(ModelConstants.BOOK_LIST, bookService.findBooksByTitle(title));
			modelAndView.setViewName(ViewNames.BOOKS);
		}
		if (!authors.isEmpty() && !title.isEmpty()){
			modelAndView.addObject(ModelConstants.BOOK_LIST, bookService.findBooksByTitleAndAuthor(title, authors));
			modelAndView.setViewName(ViewNames.BOOKS);
		}
		return modelAndView;
	}

	/**
	 * Binder initialization
	 */
	@InitBinder
	public void initialiseBinder(WebDataBinder binder) {
		binder.setAllowedFields("id", "title", "authors", "status");
	}

}
