package com.scheduler.infrastructure.users;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.google.common.collect.Iterables;
import com.scheduler.infrastructure.GenericTenantAwareHibernateRepository;
import com.scheduler.shared.users.domain.users.User;
import com.scheduler.shared.users.domain.users.UserId;
import com.scheduler.shared.users.domain.users.UserRepository;

@Repository
public class UserHibernateRepository implements UserRepository {

    @Autowired
    private GenericTenantAwareHibernateRepository<User> userRepository;

    @Override
    public List<User> list() {
        return (List<User>) userRepository.list(User.class);
    }

    @Override
    public void update(final User user) {
        userRepository.update(user);
    }

    @Override
    public void delete(final User user) {
        userRepository.delete(user);
    }

    @Override
    public void create(final User user) {
        userRepository.save(user);
    }

    @Override
    public User findById(final UserId userId) {
        Criteria criteria = userRepository.createCriteria(User.class);
        criteria.add(Restrictions.eq("userId", userId));
        List list = criteria.list();
        userRepository.commit();
        return Iterables.getOnlyElement((List<User>) list, null);
    }

}
