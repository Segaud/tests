package com.example;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.UserService.User;
import com.example.UserService.UserRepository;

// mock - records the calls that get made.
// stub - returns pre-decided data.
// spy - wraps a real object and allows stubbing.

@ExtendWith(MockitoExtension.class) // builds a fake class + objects at runtime.
public class UserServiceTest {
    // alternative(popular)
    // @Mock
    // UserRepository userRepository;

    // flow: stub= email not registered.
    // call register and verify that save was called.
    @Test
    void register_saves_new_user_and_returns_it(){
        // arrange
        UserRepository repo = mock(UserRepository.class);
        when(repo.existsByEmail("a@a.com")).thenReturn(false);

        UserService sut = new UserService(repo);
        // act
        User result = sut.register("a@a.com");
        // assert
        assertNotNull(result);
        assertEquals("a@a.com", result.getEmail());
        verify(repo).existsByEmail("a@a.com");
        verify(repo).save(result);

        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);;
        verify(repo, times(1)).save(userCaptor.capture()); 

        assertEquals("a@a.com", userCaptor.getValue().getEmail());

        // verify(repo).save(any());

        // verify
        // check no other unintended calls are made.
        verifyNoMoreInteractions(repo);
    }
    
}
