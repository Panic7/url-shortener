package com.flex.url_shortener.security;

import com.flex.url_shortener.entity.User;
import com.flex.url_shortener.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetailsImpl loadUserByUsername(String email) throws UsernameNotFoundException {
        var example = Example.of(
                User.builder()
                        .email(email)
                        .build()
        );
        var user = userRepository.findOne(example);

        if (user.isEmpty()) {
            throw new UsernameNotFoundException("User with email '%s' does not exist.".formatted(email));
        }

        return new UserDetailsImpl(user.get());
    }

}
