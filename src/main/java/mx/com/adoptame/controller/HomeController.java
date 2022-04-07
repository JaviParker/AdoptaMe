package mx.com.adoptame.controller;

import mx.com.adoptame.entities.donation.DonationService;
import mx.com.adoptame.entities.news.NewsService;
import mx.com.adoptame.entities.pet.services.PetService;
import mx.com.adoptame.entities.request.Request;
import mx.com.adoptame.entities.request.RequestService;
import mx.com.adoptame.entities.user.User;
import mx.com.adoptame.entities.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;
import java.util.Optional;

@Controller()
public class HomeController {
    @Autowired
    private NewsService newsService;
    @Autowired
    private DonationService donationService;
    @Autowired
    private UserService userService;
    @Autowired
    private RequestService requestService;
    @Autowired
    private PetService petService;

    @GetMapping("/")
    public String index(Model model) {
        model.addAttribute("navbar", "navbar-all");
        model.addAttribute("fix", "fix");
        model.addAttribute("mainNews", newsService.findMainNews());
        return "index";
    }

    @GetMapping("/login")
    public String login(Model model, Request request) {
        model.addAttribute("request", request);
        model.addAttribute("navbar", "navbar-all");
        return "views/authentication/login";
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication, HttpSession httpSession){
        // Pets Attributes
        model.addAttribute("petsCount",petService.countTotal());
        model.addAttribute("petsActive",petService.coutnByIsActive(true));
        model.addAttribute("petsDeactivate",petService.coutnByIsActive(false));
        model.addAttribute("petsAdopted",petService.coutnByIsAdopted(true));
        model.addAttribute("petsTop",petService.findTopFive());

        // Users Attribute
        model.addAttribute("usersCount",userService.countTotal());
        model.addAttribute("usersVolunteer",userService.countVolunteer());
        model.addAttribute("usersAdopter",userService.countAdopter());
        model.addAttribute("usersRequest",requestService.findPending());

        // Blogs Attribute
        model.addAttribute("blogCount",newsService.countNews());
        model.addAttribute("blogMain",newsService.countMainNews());
        model.addAttribute("blogPublished",newsService.countPublishedNews());

        // Donation Attribute
        model.addAttribute("donationCuantity",donationService.sumCuantity());
        model.addAttribute("donationTop5",donationService.findTop5());

        // GET user
        String username = authentication.getName();
        Optional<User> user = userService.findByEmail(username);
        user.ifPresent(value -> {
            value.setPassword(null);
            httpSession.setAttribute("user", value);
        });
        return "views/dashboard";
    }

    @GetMapping("/noscript")
    public String noscript(){
        return "views/errorpages/noscript";
    }


}
