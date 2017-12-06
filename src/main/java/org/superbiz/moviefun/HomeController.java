package org.superbiz.moviefun;

import org.springframework.stereotype.Controller;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.superbiz.moviefun.albums.Album;
import org.superbiz.moviefun.albums.AlbumFixtures;
import org.superbiz.moviefun.albums.AlbumsBean;
import org.superbiz.moviefun.movies.Movie;
import org.superbiz.moviefun.movies.MovieFixtures;
import org.superbiz.moviefun.movies.MoviesBean;

import java.util.List;
import java.util.Map;

@Controller
public class HomeController {

    private final MoviesBean moviesBean;
    private final AlbumsBean albumsBean;

    private final MovieFixtures movieFixtures;
    private final AlbumFixtures albumFixtures;

    public HomeController(MoviesBean moviesBean, AlbumsBean albumsBean, MovieFixtures movieFixtures, AlbumFixtures albumFixtures) {
        this.moviesBean = moviesBean;
        this.albumsBean = albumsBean;
        this.movieFixtures = movieFixtures;
        this.albumFixtures = albumFixtures;

    }


    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/setup")
    public String setup(Map<String, Object> model) {
        createMovies();
        createAlbums();

        model.put("movies", moviesBean.getMovies());
        model.put("albums", albumsBean.getAlbums());

        return "setup";
    }

    @Transactional(value = "transactionManagerAlbums")
    private void createAlbums() {
        for (Album album : albumFixtures.load()) {
            albumsBean.addAlbum(album);
        }
    }

    //@Transactional(value = "transactionManagerMovies")
    private void createMovies() {
        for (Movie movie : movieFixtures.load()) {
            addMovie(movie);
        }
    }

    @Transactional(value = "transactionManagerMovies")
    public void addMovie(Movie movie) {
        moviesBean.addMovie(movie);
    }

    @Transactional(value = "transactionManagerMovies")
    public void deleteMovieId(long id) {
        moviesBean.deleteMovieId(id);
    }

    public int countAll() {
        return moviesBean.countAll();
    }

    public int count(String field, String searchTerm) {

        return moviesBean.count(field, searchTerm);
    }

    public List<Movie> findAll(int firstResult, int maxResults) {
        return moviesBean.findAll(firstResult, maxResults);
    }

    public List<Movie> findRange(String field, String searchTerm, int firstResult, int maxResults) {

        return moviesBean.findRange(field
                , searchTerm, firstResult, maxResults);
    }


}