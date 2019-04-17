package bookstore;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import javax.validation.Valid;

@RestController
public class BookController{

    BookAccess bookAccess = new BookAccess();

    @RequestMapping(value = "/books", method = RequestMethod.GET)
    public ResponseEntity<List<Book>> getBooks(){
        List<Book> bookList = bookAccess.getAllBooks();
        if(bookList.isEmpty()) return new ResponseEntity<List<Book>>(bookList, HttpStatus.NO_CONTENT);
        else return new ResponseEntity<List<Book>>(bookList, HttpStatus.OK);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.GET)
    public ResponseEntity<Book> getBook(@PathVariable(value="id") int id){
        Book book = bookAccess.getBook(id);
        if(book == null) return new ResponseEntity<Book>(book, HttpStatus.NOT_FOUND);
        return new ResponseEntity<Book>(book, HttpStatus.OK);
    }

    @RequestMapping(value = "/books", method = RequestMethod.POST)
    public ResponseEntity<Response> addBook(@Valid @RequestBody Book book, 
                                            UriComponentsBuilder b){
        int response = bookAccess.addBook(book);
        // building header
        List<Book> list = bookAccess.getAllBooks();
        Book element = list.get(list.size() - 1);
        HttpHeaders headers = headerBuilder(b, element.getId());

        if(response == 1) return new ResponseEntity<Response>(new Response("success", "Book added"), headers, HttpStatus.CREATED);
        return new ResponseEntity<Response>(new Response("failure", "Book already exists"), HttpStatus.BAD_REQUEST);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.PUT)
    public ResponseEntity<Response> updateBook(@PathVariable(value="id") int oid,
                                               @Valid @RequestBody Book book,
                                               UriComponentsBuilder b){
        if(bookAccess.getBook(oid) != null){
            int response = bookAccess.updateBook(oid, book);
            Book element;
            HttpHeaders headers;
            if(book.getId() == null){
                element = bookAccess.getBook(oid);
            }
            else{
                element = bookAccess.getBook(book.getId());
            }
            headers = headerBuilder(b, element.getId());

            if(response == 1) return new ResponseEntity<Response>(new Response("success", "Book updated"), headers, HttpStatus.OK);
            else return new ResponseEntity<Response>(new Response("failure", "Book already exists"), HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<Response>(new Response("failure", "Could not find book"), HttpStatus.NOT_FOUND);
    }
    
    @RequestMapping(value = "/books/{id}", method = RequestMethod.PATCH)
    public ResponseEntity<Response> patchBook(@PathVariable(value="id") int oid,
                                              @RequestBody Book book,
                                              UriComponentsBuilder b){
        if(bookAccess.getBook(oid) != null){
            int response = bookAccess.patchBook(oid, book);
            Book element;
            HttpHeaders headers;
            if(book.getId() == null){
                element = bookAccess.getBook(oid);
            }
            else{
                element = bookAccess.getBook(book.getId());
            }
            headers = headerBuilder(b, element.getId());
        
            if(response == 1) return new ResponseEntity<Response>(new Response("success", "Book updated"), headers, HttpStatus.OK);
            else return new ResponseEntity<Response>(new Response("failure", "Book already exists"), HttpStatus.BAD_REQUEST);
        }
        else return new ResponseEntity<Response>(new Response("failure", "Could not find book"), HttpStatus.NOT_FOUND);
    }

    @RequestMapping(value = "/books/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Response> deleteBook(@PathVariable(value="id") int id){
        int response = bookAccess.deleteBook(id);
        if(response == 1) return new ResponseEntity<Response>(new Response("success", "Book deleted"), HttpStatus.OK);
        return new ResponseEntity<Response>(new Response("failure", "Could not find book"), HttpStatus.NOT_FOUND);
    }

    public HttpHeaders headerBuilder(UriComponentsBuilder b, int id){
        UriComponents uriComponents = b.path("/books/{id}").buildAndExpand(id);
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(uriComponents.toUri());
        return headers;
    }
}
