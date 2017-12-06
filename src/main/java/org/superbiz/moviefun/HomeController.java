package org.superbiz.moviefun;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import javax.transaction.Transactional;
import java.util.List;

@Controller
public class HomeController {

    public MoviesBean moviesBean;

    @Value(value = "${PAGE_SIZE:20}")
    public Integer PAGE_SIZE;

    @Autowired
    public void HomeController(MoviesBean moviesBean) {
        this.moviesBean = moviesBean;
    }

    @GetMapping(value= "/")
    public String home() {
        return "index";
    }

    @GetMapping(value= "/setup")
    @Transactional
    public String setup(Model model) {
        moviesBean.addMovie(new Movie("Wedding Crashers", "David Dobkin", "Comedy", 7, 2005));
        moviesBean.addMovie(new Movie("Starsky & Hutch", "Todd Phillips", "Action", 6, 2004));
        moviesBean.addMovie(new Movie("Shanghai Knights", "David Dobkin", "Action", 6, 2003));
        moviesBean.addMovie(new Movie("I-Spy", "Betty Thomas", "Adventure", 5, 2002));
        moviesBean.addMovie(new Movie("The Royal Tenenbaums", "Wes Anderson", "Comedy", 8, 2001));
        moviesBean.addMovie(new Movie("Zoolander", "Ben Stiller", "Comedy", 6, 2001));
        moviesBean.addMovie(new Movie("Shanghai Noon", "Tom Dey", "Comedy", 7, 2000));
        model.addAttribute ("movies", moviesBean.getMovies());
        return "setup";
    }

    @GetMapping(value= "/moviefun")
    @Transactional
    public String movieFun(@RequestParam(name= "action",required=false) String action,
                @RequestParam(name= "title",required=false) String title,
                @RequestParam(name= "director",required=false) String director,
                @RequestParam(name= "genre",required=false) String genre,
                @RequestParam(name= "rating",required=false) String rating,
                @RequestParam(name= "year",required=false) String year,
                @RequestParam(name= "key",required=false) String key,
                @RequestParam(name= "field",required=false) String field,
                @RequestParam(name= "page",required=false) String page,
                Model model) {
        if ("Add".equals(action)) {

            int ratingI = Integer.parseInt(rating);
            int yearI = Integer.parseInt(year);

            Movie movie = new Movie(title, director, genre, ratingI, yearI);

            moviesBean.addMovie(movie);
            model.addAttribute(moviesBean);
        } else {

            int count = 0;

            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
                count = moviesBean.countAll();
                key = "";
                field = "";
            } else {
                count = moviesBean.count(field, key);
            }

            int pageI =1 ;

            try {
                pageI = Integer.parseInt(page);
            } catch (Exception e) {
            }

            int pageCount = (count / PAGE_SIZE);
            if (pageCount == 0 || count % PAGE_SIZE != 0) {
                pageCount++;
            }

            if (pageI < 1) {
                pageI = 1;
            }

            if (pageI > pageCount) {
                pageI = pageCount;
            }

            int start = (pageI - 1) * PAGE_SIZE;
            List<Movie> range;

            if (StringUtils.isEmpty(key) || StringUtils.isEmpty(field)) {
                range = moviesBean.findAll(start, PAGE_SIZE);
            } else {
                range = moviesBean.findRange(field, key, start, PAGE_SIZE);
            }

            int end = start + range.size();

            model.addAttribute("count", count);
            model.addAttribute("start", start + 1);
            model.addAttribute("end", end);
            model.addAttribute("page", page);
            model.addAttribute("pageCount", pageCount);
            model.addAttribute("movies", range);
            model.addAttribute("key", key);
            model.addAttribute("field", field);
        }
        return "moviefun";
    }
}
