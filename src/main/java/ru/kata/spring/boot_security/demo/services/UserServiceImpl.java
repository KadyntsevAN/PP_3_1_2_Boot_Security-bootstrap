package ru.kata.spring.boot_security.demo.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.kata.spring.boot_security.demo.dao.RoleDao;
import ru.kata.spring.boot_security.demo.dao.UserDao;
import ru.kata.spring.boot_security.demo.models.Role;
import ru.kata.spring.boot_security.demo.models.User;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Transactional(readOnly = true)
@Service
public class UserServiceImpl implements UserService, UserDetailsService {

    private final PasswordEncoder bCryptPasswordEncoder;

    private final RoleDao roleDao;
    private final UserDao userDao;

    @Autowired
    public UserServiceImpl(@Lazy PasswordEncoder bCryptPasswordEncoder, RoleDao roleDao, UserDao userDao) {
        this.bCryptPasswordEncoder = bCryptPasswordEncoder;
        this.roleDao = roleDao;
        this.userDao = userDao;
    }

    @Transactional
    @Override
    public void save(User user, String[] rolesNames) {
        user.setPass(bCryptPasswordEncoder.encode(user.getPassword()));
        setUserRoles(user, rolesNames);
        userDao.save(user);
    }

    @Transactional(readOnly = true)
    @Override
    public List<User> show() {
        return userDao.show();
    }

    @Transactional
    @Override
    public void delete(int id) {
        userDao.delete(id);
    }

    @Transactional
    @Override
    public void update(int id, User updateUser, String[] rolesNames) {
        User currentUser = userDao.find(id);
        setUserRoles(updateUser, rolesNames);
        updateUser.setPass(currentUser.getPass());
        userDao.update(id, updateUser);
    }

    private void setUserRoles(User user, String[] rolesNames) {
        Set<Role> rolesSet = new HashSet<>();
        for (String roleName : rolesNames) {
            Role role = roleDao.find(roleName);
            if (role == null) {
                role = new Role(roleName);
                roleDao.save(role);
            }
            rolesSet.add(role);
        }
        user.setRoles(rolesSet);
    }

    @Transactional(readOnly = true)
    @Override
    public User find(int id) {
        return userDao.find(id);
    }

    @Transactional(readOnly = true)
    @Override
    public User find(String name) {
        return userDao.find(name);
    }

    @Transactional(readOnly = true)
    @Override
    public User findEmail(String email) {
        return userDao.findEmail(email);
    }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userDao.joinFetch(findEmail(email));
        if (user == null) {
            throw new UsernameNotFoundException(String.format("User %s not found :", email));
        }
        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(),
                user.getRoles());
    }
}
