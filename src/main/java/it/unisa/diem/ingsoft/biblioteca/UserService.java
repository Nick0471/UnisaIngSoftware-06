package it.unisa.diem.ingsoft.biblioteca;

import java.util.List;
import java.util.Optional;

public interface UserService {
    List<User> getAll();
    List<User> get(int maxUsers);
    Optional<User> getByEmail(String email);
    Optional<User> getById(String id);
	void register(User user);
    void registerAll(List<User> users);
}
