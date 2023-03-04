package fr.uga.l3miage.library.authors;

import fr.uga.l3miage.data.domain.Author;
import fr.uga.l3miage.data.domain.Book;
import fr.uga.l3miage.library.books.BookDTO;
import fr.uga.l3miage.library.books.BooksMapper;
import fr.uga.l3miage.library.service.AuthorService;
import fr.uga.l3miage.library.service.DeleteAuthorException;
import fr.uga.l3miage.library.service.EntityNotFoundException;
import jakarta.validation.Valid;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;

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
public class AuthorsController {

    private final AuthorService authorService;
    private final AuthorMapper authorMapper;
    private final BooksMapper booksMapper;

    @Autowired
    public AuthorsController(AuthorService authorService, AuthorMapper authorMapper, BooksMapper booksMapper) {
        this.authorService = authorService;
        this.authorMapper = authorMapper;
        this.booksMapper = booksMapper;
    }

    @GetMapping("/authors")
    public Collection<AuthorDTO> authors(@RequestParam(value = "q", required = false) String query) {
        Collection<Author> authors;
        if (query == null) {
            authors = authorService.list();
        } else {
            authors = authorService.searchByName(query);
        }
        return authors.stream()
                .map(authorMapper::entityToDTO)
                .toList();
    }

    // ----------------------------------------------------------------
    // recherche d'un auteur en fonction de son id + gestion du cas où l'auteur cherché n'est pas trouvé
    // DONE -----------------------------------------------------------
    @ResponseStatus(HttpStatus.OK)
    @GetMapping("/authors/{id}")
    public AuthorDTO author(@PathVariable("id") Long id){
        try {
            Author res;
            res = authorService.get(id);
            AuthorDTO ret = authorMapper.entityToDTO(res);
            return ret;
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }

    // ----------------------------------------------------------------
    // création d'un auteur + gestion du cas d'un nom invalide
    // DONE -----------------------------------------------------------
    @ResponseStatus(HttpStatus.CREATED)
    @PostMapping("/authors")
    public AuthorDTO newAuthor(@RequestBody @Valid AuthorDTO author){
        Author res  = authorMapper.dtoToEntity(author);
        res = authorService.save(res);
        AuthorDTO ret = authorMapper.entityToDTO(res);
        return ret;
    }

    // ----------------------------------------------------------------
    // Mise à jour de l'auteur dont l'id est donné en paramètre + gestion du cas où celui-ci n'est pas trouvé
    // DONE -----------------------------------------------------------
    @ResponseStatus(HttpStatus.OK)
    @PutMapping("/authors/{id}")
    public AuthorDTO updateAuthor(@RequestBody @Valid AuthorDTO author, @PathVariable("id") Long id) throws EntityNotFoundException {
        // attention AuthorDTO.id() doit être égale à id, sinon la requête utilisateur est mauvaise
        if(!author.id().equals(id)){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        return authorMapper.entityToDTO(authorService.update(authorMapper.dtoToEntity(author)));
    }

    // ----------------------------------------------------------------
    // Suppression de l'auteur correspondant + gestion non trouvé et erreur de suppression
    // DONE -----------------------------------------------------------
    @DeleteMapping("/authors/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteAuthor(@PathVariable("id") Long id) throws EntityNotFoundException{
        try{
            authorService.delete(id);
        }catch(DeleteAuthorException e){
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
        }
        
    }

    // ----------------------------------------------------------------
    @GetMapping ("/authors/{id}/books")
    @ResponseStatus(HttpStatus.OK)
    public Collection<BookDTO> books(@PathVariable("id") Long authorId) {
        try {
            Author author = authorService.get(authorId);
            Collection<Book> books = author.getBooks();
            Collection<BookDTO> booksDTOs = books.stream().map(booksMapper::entityToDTO).toList();
            return booksDTOs;
        }catch(EntityNotFoundException e){
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
