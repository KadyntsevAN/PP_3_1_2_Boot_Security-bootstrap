package ru.kata.spring.boot_security.demo.dao;

import ru.kata.spring.boot_security.demo.models.Role;

public interface RoleDao {
    void save(Role role);
    Role find(String role);
}
