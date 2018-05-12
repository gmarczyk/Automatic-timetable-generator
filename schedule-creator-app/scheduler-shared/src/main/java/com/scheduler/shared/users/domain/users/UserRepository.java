package com.scheduler.shared.users.domain.users;

import java.util.List;

public interface UserRepository {

    public List<User> list();

    public void update(User user);

    public void delete(User user);

    public void create(User user);

    public User findById(UserId userId);
}
