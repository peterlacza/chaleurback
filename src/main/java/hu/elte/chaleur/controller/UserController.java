package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.User;
import hu.elte.chaleur.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserService userService;

    @GetMapping(params = "userName")
    public User getUserProfile(String userName){
        return userService.getUserProfile(userName);
    }

    @GetMapping("/followers")
    public List<User> followersByUser(@RequestParam("userName") String userName){
        return userService.followersByUser(userName);
    }

    @GetMapping("/followings")
    public List<User> myFollowings(){
        return userService.myFollowings();
    }

    @PostMapping("/addImage")
    public void addRecipeImg(@RequestParam("picture") MultipartFile picture,
                             @RequestParam("userName") String userName) throws InterruptedException, IOException {
        userService.addRecipeImg(picture, userName);
    }

    @PostMapping("/follow")
    public void follow(@RequestParam("userName") String userName){
        userService.follow(userName);
    }

    @PostMapping("/unfollow")
    public void unfollow(@RequestParam("userName") String userName){
        userService.unfollow(userName);
    }
}
