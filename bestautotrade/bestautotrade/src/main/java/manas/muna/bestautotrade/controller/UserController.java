package manas.muna.bestautotrade.controller;

import manas.muna.bestautotrade.model.User;
import manas.muna.bestautotrade.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserService userService;

    @GetMapping("/userdata/{id}")
    public void getUserData(@PathVariable String id) {
        User userData = userService.getUser(id);
        if (userData != null)
            System.out.println(userData.getKey().getName());
    }

    @GetMapping("/userdata")
    public void getAllUserData() {
        List<User> users = userService.getAllUsers();
        System.out.println("users=" + users.size());
        users.stream().forEach(user -> System.out.println(user.getKey().getName()));
    }

    @PostMapping("/addUser")
    public String saveUser(@RequestBody User user){
        userService.saveUser(user);
        return "stored";
    }
}
