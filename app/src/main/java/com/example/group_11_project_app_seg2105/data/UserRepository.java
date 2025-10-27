package com.example.group_11_project_app_seg2105.data;

import java.util.List;

public interface UserRepository {
    boolean create(User user);
    User findByEmail(String email);
    boolean update(User user);
    boolean delete(String email);
    List<User> findAll();
    List<User> findByRole(String role);
}
