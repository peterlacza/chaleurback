package hu.elte.chaleur.controller;

import hu.elte.chaleur.model.Image;
import hu.elte.chaleur.model.User;
import hu.elte.chaleur.repository.ImageRepository;
import hu.elte.chaleur.repository.UserRepository;
import hu.elte.chaleur.security.AuthService;
import hu.elte.chaleur.specification.UserSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class UserController {
    private final UserRepository userRepository;
    private final AuthService authService;
    private final ImageRepository imageRepository;

    @GetMapping(params = "userName")
    public User getUserProfile(String userName){
        User getUser = userRepository.findByUsername(userName).get();
        User user = new User();
        user.setFullName(getUser.getFullName());
        user.setId(getUser.getId());
        user.setIntroduction(getUser.getIntroduction());
        user.setRecipes(getUser.getRecipes());
        user.setUsername(getUser.getUsername());
        return user;
    }

    @GetMapping("/followers")
    public List<User> myFollowers(String userName){
        return userRepository.findAll(Specification.where(
                UserSpecification.findMyFollowers(userRepository.findByUsername(userName).get())
        ));
    }

    @GetMapping("/followings")
    public List<User> myFollowings(){
        return userRepository.findAll(Specification.where(
                UserSpecification.findMyFollowing(authService.getActUser())
        ));
    }

    @PostMapping("/addImage")
    public void addRecipeImg(@RequestParam("picture") MultipartFile picture,
                             @RequestParam("userName") String userName) throws InterruptedException, IOException {
        Thread.sleep(2000);
        User user = userRepository.findByUsername(userName).get();
        Image image = new Image();
        image.setName(picture.getName());
        image.setType(picture.getContentType());
        image.setPicByte(picture.getBytes());
        image = imageRepository.save(image);
        user.setAvatar(image);
        userRepository.save(user);
    }

    @PostMapping("/follow")
    public void follow(String userName){
        User toFollow = userRepository.findByUsername(userName).get();
        User user = authService.getActUser();
        List<User> followings = user.getFollowing();
        followings.add(toFollow);
        user.setFollowing(followings);
        userRepository.save(user);

        List<User> followers = toFollow.getFollowers();
        followers.add(user);
        toFollow.setFollowers(followers);
        userRepository.save(toFollow);
    }

    @PostMapping("/unfollow")
    public void unfollow(String userName){
        User toFollow = userRepository.findByUsername(userName).get();
        User user = authService.getActUser();

        List<User> followers = toFollow.getFollowers();
        followers.remove(user);
        toFollow.setFollowers(followers);
        userRepository.save(toFollow);

        List<User> followings = user.getFollowing();
        followings.remove(toFollow);
        user.setFollowing(followings);
        userRepository.save(user);
    }
}
