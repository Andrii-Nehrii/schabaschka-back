package schabaschka.user.controller;


import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import schabaschka.user.dto.UserDto;
import schabaschka.user.service.UserService;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/users")
public class UserController {

   private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }


    @GetMapping("/{id}")
    public ResponseEntity<UserDto> getById( @PathVariable long id ){
        Optional<UserDto> userOpt = userService.findById(id);
        return userOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping
    public List<UserDto> list(){
        return userService.findAll();
    }

    @GetMapping("/search")
    public ResponseEntity<UserDto> findByEmail(@RequestParam String email){
        Optional<UserDto> userOpt = userService.findByEmail(email);
        return userOpt.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

}
