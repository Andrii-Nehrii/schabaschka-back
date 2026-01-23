package schabaschka.security;

import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import schabaschka.user.dao.UserRepository;
import schabaschka.user.model.User;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;
    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username));


        String[] roles;
        if(user.getRole() != null){
            roles = new String[]{"ROLE_" + user.getRole()};
        }else{
            roles = new String[0];
        }
        return org.springframework.security.core.userdetails.User.withUsername(user.getEmail()).password(user.getPasswordHash())
                .authorities(AuthorityUtils.createAuthorityList(roles)).build();//?

    }

}
