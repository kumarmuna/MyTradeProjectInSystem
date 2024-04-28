package manas.muna.bestautotrade.service;

import manas.muna.bestautotrade.model.User;
import manas.muna.bestautotrade.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {

    @Autowired
    private UserRepository repository;

    public void saveUser(User user) {
        repository.save(user);
    }
    public User getUser(String id) {
        return repository.findById(null).orElse(null);
    }
    public List<User> getAllUsers(){
        return repository.findAll();
    }
    public void deleteUser(String id) {
        repository.deleteById(null);
    }
}