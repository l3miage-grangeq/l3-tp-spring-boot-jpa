package fr.uga.l3miage.library.books;

import fr.uga.l3miage.data.domain.Book;
import fr.uga.l3miage.library.authors.AuthorDTO;
import fr.uga.l3miage.library.service.BookService;
import fr.uga.l3miage.library.service.EntityNotFoundException;
import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.Collection;

@RestController
@RequestMapping(value = "/api/v1", produces = "application/json")
public class BooksController {

    private final BookService bookService;
    private final BooksMapper booksMapper;

    @Autowired
    public BooksController(BookService bookService, BooksMapper booksMapper) {
        this.bookService = bookService;
        this.booksMapper = booksMapper;
    }
    // DONE
    @GetMapping("/books")
    public Collection<BookDTO> books(@RequestParam("q") String query) {
        Collection<Book> books;
        if (query == null) {
            books = bookService.list();
        } else {
            books = bookService.findByTitle(query);
        }
        return books.stream()
                .map(booksMapper::entityToDTO)
                .toList();
    }

    // ----------------------------------------------------------------
    // recherche d'un livere en fonction de son id + gestion du cas où le livre cherché n'est pas trouvé
    //  -----------------------------------------------------------
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/books/{id}")
    public BookDTO book(@PathVariable("id") Long id) {
        try{
            Book book = bookService.get(id);
            BookDTO bookDTO = booksMapper.entityToDTO(book);
            return bookDTO;
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // ----------------------------------------------------------------
    // création d'un nouveau livre + gestion cas invalide
    //  -----------------------------------------------------------
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/authors/{id}/books")
    public BookDTO newBook( @PathVariable("id") Long authorId, @RequestBody @Valid BookDTO book) {
        try{
            Book book1 = booksMapper.dtoToEntity(book);
            Book newBook = bookService.save(authorId,book1);
            BookDTO ret = booksMapper.entityToDTO(newBook);
            return ret;
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }

    }

    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/books/{id}")
    public BookDTO updateBook(@PathVariable("id") Long id, @RequestBody @Valid BookDTO book) {
        if (!book.id().equals(id)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        Book bookEntity=booksMapper.dtoToEntity(book);
        try{
            
            Book bookUpdated=bookService.update(bookEntity);
            return booksMapper.entityToDTO(bookUpdated);
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @DeleteMapping("books/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBook(@PathVariable("id") Long id) {
        try{
            bookService.delete(id);
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    @PutMapping("books/{id}/authors")
    public BookDTO addAuthor(@PathVariable("id")Long bookId, @RequestBody @Valid AuthorDTO author) {
        try{
            Book book = bookService.addAuthor(bookId, author.id());
            return booksMapper.entityToDTO(book);
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
